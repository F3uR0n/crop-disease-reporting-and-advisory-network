package com.cdran;

import com.cdran.config.SeasonalRiskSampleDataInitializer;
import com.cdran.model.entity.RiskLevel;
import com.cdran.model.entity.SeasonalRisk;
import com.cdran.repository.SeasonalRiskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
class SeasonalRiskSampleDataInitializerTests {

    @Autowired
    private SeasonalRiskRepository repository;

    @Autowired
    private SeasonalRiskSampleDataInitializer initializer;

    @Test
    void seedsSampleForecastsOnceForH2() {
        Set<String> regions = repository.findAll().stream()
                .map(SeasonalRisk::getRegion)
                .collect(Collectors.toSet());

        assertThat(regions).containsExactlyInAnyOrder("Dhaka", "Rajshahi", "Rangpur", "Khulna");
        assertThat(repository.findAll())
                .extracting(SeasonalRisk::getRiskLevel)
                .containsExactlyInAnyOrder(
                        RiskLevel.HIGH, RiskLevel.CRITICAL, RiskLevel.MEDIUM, RiskLevel.LOW);

        ApplicationArguments args = new DefaultApplicationArguments(new String[0]);
        initializer.run(args);

        assertThat(repository.count()).isEqualTo(4);
    }
}
