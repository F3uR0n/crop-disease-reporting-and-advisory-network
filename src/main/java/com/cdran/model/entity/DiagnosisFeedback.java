package com.cdran.model.entity;

import com.cdran.model.enums.FollowUpStatus;
import com.cdran.model.enums.RecoveryOutcome;
import com.cdran.model.enums.ReviewDecision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "diagnosis_feedback")
public class DiagnosisFeedback extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disease_case_id", nullable = false, unique = true)
    private DiseaseCase diseaseCase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submitted_by_id", nullable = false)
    private User submittedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ReviewDecision decision;

    @Enumerated(EnumType.STRING)
    @Column(name = "recovery_outcome", nullable = false, length = 32)
    private RecoveryOutcome recoveryOutcome;

    @Enumerated(EnumType.STRING)
    @Column(name = "follow_up_status", nullable = false, length = 32)
    private FollowUpStatus followUpStatus = FollowUpStatus.ANSWERED;

    @Column(name = "ai_label", length = 160)
    private String aiLabel;

    @Column(name = "expert_label", length = 160)
    private String expertLabel;

    @Column(name = "ai_confidence", nullable = false)
    private double aiConfidence;

    @Column(name = "ai_was_correct", nullable = false)
    private boolean aiWasCorrect;

    @Column(length = 1000)
    private String notes;

    public void applyFeedback(
            User actor,
            ReviewDecision decision,
            RecoveryOutcome recoveryOutcome,
            FollowUpStatus followUpStatus,
            String aiLabel,
            String expertLabel,
            double aiConfidence,
            String notes) {
        this.submittedBy = actor;
        this.decision = decision;
        this.recoveryOutcome = recoveryOutcome;
        this.followUpStatus = followUpStatus != null ? followUpStatus : FollowUpStatus.ANSWERED;
        this.aiLabel = aiLabel;
        this.expertLabel = expertLabel;
        this.aiConfidence = aiConfidence;
        this.aiWasCorrect = (decision == ReviewDecision.CONFIRMED);
        this.notes = notes != null ? notes.trim() : null;
    }

    public DiseaseCase getDiseaseCase() { return diseaseCase; }
    public void setDiseaseCase(DiseaseCase diseaseCase) { this.diseaseCase = diseaseCase; }

    public User getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(User submittedBy) { this.submittedBy = submittedBy; }

    public ReviewDecision getDecision() { return decision; }
    public void setDecision(ReviewDecision decision) { this.decision = decision; }

    public RecoveryOutcome getRecoveryOutcome() { return recoveryOutcome; }
    public void setRecoveryOutcome(RecoveryOutcome recoveryOutcome) { this.recoveryOutcome = recoveryOutcome; }

    public FollowUpStatus getFollowUpStatus() { return followUpStatus; }
    public void setFollowUpStatus(FollowUpStatus followUpStatus) { this.followUpStatus = followUpStatus; }

    public String getAiLabel() { return aiLabel; }
    public void setAiLabel(String aiLabel) { this.aiLabel = aiLabel; }

    public String getExpertLabel() { return expertLabel; }
    public void setExpertLabel(String expertLabel) { this.expertLabel = expertLabel; }

    public double getAiConfidence() { return aiConfidence; }
    public void setAiConfidence(double aiConfidence) { this.aiConfidence = aiConfidence; }

    public boolean isAiWasCorrect() { return aiWasCorrect; }
    public void setAiWasCorrect(boolean aiWasCorrect) { this.aiWasCorrect = aiWasCorrect; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}