package com.cdran.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdran.dto.EscalationForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.exception.ResourceNotFoundException;
import com.cdran.model.entity.CaseEscalation;
import com.cdran.model.entity.DiseaseCase;
import com.cdran.model.entity.Farmer;
import com.cdran.model.entity.User;
import com.cdran.model.enums.AccountStatus;
import com.cdran.model.enums.CaseStatus;
import com.cdran.model.enums.NotificationType;
import com.cdran.model.enums.Role;
import com.cdran.repository.CaseEscalationRepository;
import com.cdran.repository.DiseaseCaseRepository;
import com.cdran.repository.UserRepository;

@Service
public class LowConfidenceEscalationService {

    private final CaseEscalationRepository escalationRepository;
    private final DiseaseCaseRepository diseaseCaseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final double confidenceThreshold;

    public LowConfidenceEscalationService(
            CaseEscalationRepository escalationRepository,
            DiseaseCaseRepository diseaseCaseRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            @Value("${app.escalation.confidence-threshold:0.75}") double confidenceThreshold) {
        this.escalationRepository = escalationRepository;
        this.diseaseCaseRepository = diseaseCaseRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.confidenceThreshold = confidenceThreshold;
    }

    public double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public boolean shouldEscalate(String predictedLabel, double confidence, boolean aiUnavailable) {
        if (aiUnavailable) {
            return true;
        }
        if (predictedLabel == null || predictedLabel.isBlank() || "UNKNOWN".equalsIgnoreCase(predictedLabel.trim())) {
            return true;
        }
        return confidence < confidenceThreshold;
    }

    @Transactional(readOnly = true)
    public List<CaseEscalation> listForCase(Long caseId, String actorPhone) {
        User actor = requireUser(actorPhone);
        DiseaseCase diseaseCase = requireAuthorizedCase(caseId, actor);
        return escalationRepository.findByDiseaseCaseIdOrderByCreatedAtDesc(diseaseCase.getId());
    }

    @Transactional
    public Optional<CaseEscalation> evaluateAndEscalateAutomatically(
            Long caseId, String predictedLabel, double confidence, boolean aiUnavailable) {
        if (!shouldEscalate(predictedLabel, confidence, aiUnavailable)) {
            return Optional.empty();
        }
        DiseaseCase diseaseCase = diseaseCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found", caseId));

        if (escalationRepository.existsByDiseaseCaseIdAndResolvedFalse(diseaseCase.getId())) {
            return Optional.empty();
        }

        String autoReason = aiUnavailable
                ? "AI classification service unavailable or uncertain; escalated for review."
                : String.format("AI confidence %.2f below threshold %.2f.", confidence, confidenceThreshold);

        return Optional.of(persistEscalation(diseaseCase, null, predictedLabel, confidence, autoReason));
    }

    @Transactional
    public CaseEscalation escalateExplicitly(Long caseId, EscalationForm form, String actorPhone) {
        User actor = requireUser(actorPhone);
        DiseaseCase diseaseCase = requireAuthorizedCase(caseId, actor);

        if (!shouldEscalate(form.getPredictedLabel(), form.getConfidence(), form.isAiUnavailable())) {
            throw new BusinessRuleException("confidence", "escalation.error.aboveThreshold");
        }
        if (escalationRepository.existsByDiseaseCaseIdAndResolvedFalse(diseaseCase.getId())) {
            throw new BusinessRuleException("reason", "escalation.error.alreadyOpen");
        }

        return persistEscalation(
                diseaseCase,
                actor,
                form.getPredictedLabel(),
                form.getConfidence(),
                form.getReason().trim());
    }

    private CaseEscalation persistEscalation(
            DiseaseCase diseaseCase,
            User actor,
            String predictedLabel,
            double confidence,
            String reason) {
        CaseStatus currentStatus = diseaseCase.getStatus();
        if (currentStatus == CaseStatus.SUBMITTED) {
            if (currentStatus.canMoveTo(CaseStatus.AI_ANALYZED)) {
                diseaseCase.transitionTo(CaseStatus.AI_ANALYZED, "Recorded initial diagnosis before escalation.");
            }
            if (diseaseCase.getStatus().canMoveTo(CaseStatus.UNDER_REVIEW)) {
                diseaseCase.transitionTo(CaseStatus.UNDER_REVIEW, reason);
            }
        } else if (currentStatus != null && currentStatus.canMoveTo(CaseStatus.UNDER_REVIEW)) {
            diseaseCase.transitionTo(CaseStatus.UNDER_REVIEW, reason);
        }

        diseaseCaseRepository.save(diseaseCase);

        CaseEscalation escalation = new CaseEscalation();
        escalation.setDiseaseCase(diseaseCase);
        escalation.setEscalatedBy(actor);
        escalation.setPredictedLabel(predictedLabel != null ? predictedLabel.trim() : "UNKNOWN");
        escalation.setConfidence(confidence);
        escalation.setThreshold(confidenceThreshold);
        escalation.setReason(reason);
        escalation.setResolved(false);

        CaseEscalation saved = escalationRepository.save(escalation);

        Farmer owner = diseaseCase.getFarmer();
        if (owner != null) {
            notificationService.notify(
                    owner,
                    NotificationType.CASE_UPDATE,
                    "notification.escalation.created",
                    String.valueOf(diseaseCase.getId()),
                    "/farmer/cases/" + diseaseCase.getId());
        }

        for (User candidate : userRepository.findByStatus(AccountStatus.ACTIVE)) {
            if (candidate.getRole() == Role.EXPERT || candidate.getRole() == Role.FIELD_OFFICER) {
                notificationService.notify(
                        candidate,
                        NotificationType.CASE_UPDATE,
                        "notification.escalation.reviewer",
                        String.valueOf(diseaseCase.getId()),
                        "/cases/" + diseaseCase.getId() + "/escalations");
            }
        }

        return saved;
    }

    private User requireUser(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", phone));
    }

    private DiseaseCase requireAuthorizedCase(Long caseId, User actor) {
        DiseaseCase diseaseCase = diseaseCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found", caseId));

        if (actor.getRole() == Role.FARMER) {
            Farmer owner = diseaseCase.getFarmer();
            if (owner == null || !owner.getPhone().equals(actor.getPhone())) {
                throw new ResourceNotFoundException("Case not found", caseId);
            }
        }
        return diseaseCase;
    }
}