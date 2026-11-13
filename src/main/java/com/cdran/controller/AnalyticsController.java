package com.cdran.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cdran.dto.AnalyticsDTO;
import com.cdran.service.AnalyticsService;

@Controller
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics")
    public String showAnalyticsDashboard(Model model) {
        AnalyticsDTO analyticsData = analyticsService.getDashboardData();
        model.addAttribute("analytics", analyticsData);
        return "analytics/dashboard"; 
    }
}