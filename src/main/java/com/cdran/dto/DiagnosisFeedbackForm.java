package com.cdran.dto;

import com.cdran.model.enums.FollowUpStatus;
import com.cdran.model.enums.RecoveryOutcome;
import com.cdran.model.enums.ReviewDecision;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DiagnosisFeedbackForm {

    @NotNull(message = "{feedback.validation.decision.required}")
    private ReviewDecision decision;

    @NotNull(message = "{feedback.validation.outcome.required}")
    private RecoveryOutcome recoveryOutcome;

    @NotNull(message = "{feedback.validation.followUpStatus.required}")
    private FollowUpStatus followUpStatus = FollowUpStatus.ANSWERED;

    @Size(max = 160, message = "{feedback.validation.label.size}")
    private String aiLabel;

    @Size(max = 160, message = "{feedback.validation.label.size}")
    private String expertLabel;

    @DecimalMin(value = "0.0", message = "{feedback.validation.confidence.range}")
    @DecimalMax(value = "1.0", message = "{feedback.validation.confidence.range}")
    private double aiConfidence;

    @Size(max = 1000, message = "{feedback.validation.notes.size}")
    private String notes;

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}