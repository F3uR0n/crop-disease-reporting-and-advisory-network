package com.cdran.dto;

import java.util.List;

public class AnalyticsDTO {
    private List<String> diseaseLabels;
    private List<Long> diseaseCounts;
    private List<String> regionLabels;
    private List<Long> regionCounts;
    private List<String> cropLabels;
    private List<Long> cropCounts;
    private double aiAccuracy;
    private double avgExpertResponseTimeHours;

    // Getters and Setters
    public List<String> getDiseaseLabels() { return diseaseLabels; }
    public void setDiseaseLabels(List<String> diseaseLabels) { this.diseaseLabels = diseaseLabels; }

    public List<Long> getDiseaseCounts() { return diseaseCounts; }
    public void setDiseaseCounts(List<Long> diseaseCounts) { this.diseaseCounts = diseaseCounts; }

    public List<String> getRegionLabels() { return regionLabels; }
    public void setRegionLabels(List<String> regionLabels) { this.regionLabels = regionLabels; }

    public List<Long> getRegionCounts() { return regionCounts; }
    public void setRegionCounts(List<Long> regionCounts) { this.regionCounts = regionCounts; }

    public List<String> getCropLabels() { return cropLabels; }
    public void setCropLabels(List<String> cropLabels) { this.cropLabels = cropLabels; }

    public List<Long> getCropCounts() { return cropCounts; }
    public void setCropCounts(List<Long> cropCounts) { this.cropCounts = cropCounts; }

    public double getAiAccuracy() { return aiAccuracy; }
    public void setAiAccuracy(double aiAccuracy) { this.aiAccuracy = aiAccuracy; }

    public double getAvgExpertResponseTimeHours() { return avgExpertResponseTimeHours; }
    public void setAvgExpertResponseTimeHours(double avgExpertResponseTimeHours) { this.avgExpertResponseTimeHours = avgExpertResponseTimeHours; }
}