package com.cdran.service;

import com.cdran.model.entity.RiskLevel;
import com.cdran.model.entity.Season;
import com.cdran.model.entity.SeasonalRisk;
import com.cdran.repository.SeasonalRiskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeasonalRiskService {

    private final SeasonalRiskRepository repository;

    public SeasonalRiskService(SeasonalRiskRepository repository) {
        this.repository = repository;
    }

    public List<SeasonalRisk> getAllForecasts() {
        return repository.findAll();
    }

    @Transactional
    public void generateForecast(String region, String crop, Season currentSeason, int historicalOutbreakCount) {
        int baseScore = calculateBaseScore(crop, currentSeason);
        int finalScore = Math.min(100, baseScore + (historicalOutbreakCount * 5));
        
        RiskLevel level = determineRiskLevel(finalScore);

        SeasonalRisk forecast = new SeasonalRisk();
        forecast.setRegion(region);
        forecast.setCrop(crop);
        forecast.setSeason(currentSeason);
        forecast.setRiskScore(finalScore);
        forecast.setRiskLevel(level);
        forecast.setLastCalculatedAt(LocalDateTime.now());

        repository.save(forecast);
    }

    private int calculateBaseScore(String crop, Season season) {
        if (crop.equalsIgnoreCase("Rice") && season == Season.KHARIF_2) return 60;
        if (crop.equalsIgnoreCase("Potato") && season == Season.RABI) return 70;
        if (crop.equalsIgnoreCase("Wheat") && season == Season.RABI) return 40;
        return 20; 
    }

    private RiskLevel determineRiskLevel(int score) {
        if (score >= 80) return RiskLevel.CRITICAL;
        if (score >= 60) return RiskLevel.HIGH;
        if (score >= 40) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
}
