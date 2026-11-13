package com.cdran.config;

import com.cdran.model.entity.Season;
import com.cdran.repository.SeasonalRiskRepository;
import com.cdran.service.SeasonalRiskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("h2")
public class SeasonalRiskSampleDataInitializer implements ApplicationRunner {

    private final SeasonalRiskRepository repository;
    private final SeasonalRiskService riskService;

    public SeasonalRiskSampleDataInitializer(SeasonalRiskRepository repository, SeasonalRiskService riskService) {
        this.repository = repository;
        this.riskService = riskService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }

        riskService.generateForecast("Dhaka", "Rice", Season.KHARIF_2, 0);
        riskService.generateForecast("Rajshahi", "Potato", Season.RABI, 2);
        riskService.generateForecast("Rangpur", "Wheat", Season.RABI, 0);
        riskService.generateForecast("Khulna", "Rice", Season.KHARIF_1, 0);
    }
}
