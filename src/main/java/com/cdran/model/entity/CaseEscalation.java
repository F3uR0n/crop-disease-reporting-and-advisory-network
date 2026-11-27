package com.cdran.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "case_escalations")
public class CaseEscalation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disease_case_id", nullable = false)
    private DiseaseCase diseaseCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalated_by_id")
    private User escalatedBy;

    @Column(name = "predicted_label", length = 160)
    private String predictedLabel;

    @Column(nullable = false)
    private double confidence;

    @Column(nullable = false)
    private double threshold;

    @Column(nullable = false, length = 600)
    private String reason;

    @Column(nullable = false)
    private boolean resolved = false;

    public void markResolved() {
        this.resolved = true;
    }

    public DiseaseCase getDiseaseCase() { return diseaseCase; }
    public void setDiseaseCase(DiseaseCase diseaseCase) { this.diseaseCase = diseaseCase; }

    public User getEscalatedBy() { return escalatedBy; }
    public void setEscalatedBy(User escalatedBy) { this.escalatedBy = escalatedBy; }

    public String getPredictedLabel() { return predictedLabel; }
    public void setPredictedLabel(String predictedLabel) { this.predictedLabel = predictedLabel; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}