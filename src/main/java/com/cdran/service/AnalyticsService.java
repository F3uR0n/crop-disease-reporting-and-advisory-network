package com.cdran.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cdran.dto.AnalyticsDTO;

@Service
public class AnalyticsService {

    public AnalyticsDTO getDashboardData() {
        AnalyticsDTO dto = new AnalyticsDTO();

        // Note: For now, we are using fake (mock) data so you can see the dashboard.
        // Later, you will connect this to your repositories to get real database data.
        
        dto.setDiseaseLabels(List.of("Leaf Blight", "Powdery Mildew", "Rust", "Healthy"));
        dto.setDiseaseCounts(List.of(45L, 30L, 15L, 10L));

        dto.setRegionLabels(List.of("Dhaka", "Chittagong", "Rajshahi", "Khulna"));
        dto.setRegionCounts(List.of(80L, 40L, 25L, 15L));

        dto.setCropLabels(List.of("Rice", "Wheat", "Tomato", "Potato"));
        dto.setCropCounts(List.of(120L, 50L, 30L, 20L));

        dto.setAiAccuracy(87.5); 
        dto.setAvgExpertResponseTimeHours(4.2);

        return dto;
    }
}