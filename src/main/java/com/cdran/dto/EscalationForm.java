package com.cdran.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EscalationForm {

    @Size(max = 160, message = "{escalation.validation.label.size}")
    private String predictedLabel;

    @DecimalMin(value = "0.0", message = "{escalation.validation.confidence.range}")
    @DecimalMax(value = "1.0", message = "{escalation.validation.confidence.range}")
    private double confidence;

    private boolean aiUnavailable;

    @NotBlank(message = "{escalation.validation.reason.required}")
    @Size(max = 600, message = "{escalation.validation.reason.size}")
    private String reason;

    public String getPredictedLabel() { return predictedLabel; }
    public void setPredictedLabel(String predictedLabel) { this.predictedLabel = predictedLabel; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public boolean isAiUnavailable() { return aiUnavailable; }
    public void setAiUnavailable(boolean aiUnavailable) { this.aiUnavailable = aiUnavailable; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}