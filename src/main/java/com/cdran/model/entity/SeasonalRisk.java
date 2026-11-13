package com.cdran.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seasonal_risk_forecasts")
public class SeasonalRisk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String crop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Season season;

    @Column(nullable = false)
    private int riskScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private LocalDateTime lastCalculatedAt;

    // Getters and Setters omitted for brevity
    public void setRegion(String region) { this.region = region; }
    public void setCrop(String crop) { this.crop = crop; }
    public void setSeason(Season season) { this.season = season; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public void setLastCalculatedAt(LocalDateTime lastCalculatedAt) { this.lastCalculatedAt = lastCalculatedAt; }
    
    public String getRegion() { return region; }
    public String getCrop() { return crop; }
    public Season getSeason() { return season; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public LocalDateTime getLastCalculatedAt() { return lastCalculatedAt; }
}

