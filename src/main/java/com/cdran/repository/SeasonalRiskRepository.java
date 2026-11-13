package com.cdran.repository;

import com.cdran.model.entity.RiskLevel;
import com.cdran.model.entity.SeasonalRisk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonalRiskRepository extends JpaRepository<SeasonalRisk, Long> {
    List<SeasonalRisk> findByRegionIgnoreCase(String region);
    List<SeasonalRisk> findByRiskLevel(RiskLevel riskLevel);
}