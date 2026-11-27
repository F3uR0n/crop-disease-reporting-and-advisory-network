package com.cdran.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdran.dto.DiagnosisFeedbackForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.exception.ResourceNotFoundException;
import com.cdran.model.entity.DiagnosisFeedback;
import com.cdran.model.entity.DiseaseCase;
import com.cdran.model.entity.Farmer;
import com.cdran.model.entity.User;
import com.cdran.model.enums.NotificationType;
import com.cdran.model.enums.ReviewDecision;
import com.cdran.model.enums.Role;
import com.cdran.repository.DiagnosisFeedbackRepository;
import com.cdran.repository.DiseaseCaseRepository;
import com.cdran.repository.UserRepository;

@Service
public class DiagnosisFeedbackService {

    private final DiagnosisFeedbackRepository feedbackRepository;
    private final DiseaseCaseRepository diseaseCaseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public DiagnosisFeedbackService(
            DiagnosisFeedbackRepository feedbackRepository,
            DiseaseCaseRepository diseaseCaseRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.feedbackRepository = feedbackRepository;
        this.diseaseCaseRepository = diseaseCaseRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public Optional<DiagnosisFeedback> findByCaseId(Long caseId) {
        return feedbackRepository.findByDiseaseCaseId(caseId);
    }

    @Transactional(readOnly = true)
    public DiagnosisFeedbackForm loadFormForCase(Long caseId, String actorPhone) {
        User actor = requireUser(actorPhone);
        DiseaseCase diseaseCase = requireAuthorizedCase(caseId, actor);

        return feedbackRepository.findByDiseaseCaseId(diseaseCase.getId())
                .map(this::toForm)
                .orElseGet(DiagnosisFeedbackForm::new);
    }

    @Transactional
    public DiagnosisFeedback submitOrUpdate(Long caseId, DiagnosisFeedbackForm form, String actorPhone) {
        User actor = requireUser(actorPhone);
        DiseaseCase diseaseCase = requireAuthorizedCase(caseId, actor);

        if (form.getDecision() == ReviewDecision.CORRECTED
                && (form.getExpertLabel() == null || form.getExpertLabel().isBlank())) {
            throw new BusinessRuleException("expertLabel", "feedback.validation.expertLabel.requiredOnCorrection");
        }

        DiagnosisFeedback feedback = feedbackRepository.findByDiseaseCaseId(diseaseCase.getId())
                .orElseGet(() -> {
                    DiagnosisFeedback created = new DiagnosisFeedback();
                    created.setDiseaseCase(diseaseCase);
                    return created;
                });

        feedback.applyFeedback(
                actor,
                form.getDecision(),
                form.getRecoveryOutcome(),
                form.getFollowUpStatus(),
                form.getAiLabel(),
                form.getExpertLabel(),
                form.getAiConfidence(),
                form.getNotes());

        DiagnosisFeedback saved = feedbackRepository.save(feedback);

        Farmer owner = diseaseCase.getFarmer();
        if (owner != null && !owner.getPhone().equals(actor.getPhone())) {
            notificationService.notify(
                    owner,
                    NotificationType.FOLLOW_UP,
                    "notification.feedback.updated",
                    String.valueOf(diseaseCase.getId()),
                    "/farmer/cases/" + diseaseCase.getId());
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public double overallAccuracy() {
        long total = feedbackRepository.count();
        if (total == 0) {
            return 0.0;
        }
        return (feedbackRepository.countByAiWasCorrectTrue() * 100.0) / total;
    }

    private DiagnosisFeedbackForm toForm(DiagnosisFeedback entity) {
        DiagnosisFeedbackForm form = new DiagnosisFeedbackForm();
        form.setDecision(entity.getDecision());
        form.setRecoveryOutcome(entity.getRecoveryOutcome());
        form.setFollowUpStatus(entity.getFollowUpStatus());
        form.setAiLabel(entity.getAiLabel());
        form.setExpertLabel(entity.getExpertLabel());
        form.setAiConfidence(entity.getAiConfidence());
        form.setNotes(entity.getNotes());
        return form;
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