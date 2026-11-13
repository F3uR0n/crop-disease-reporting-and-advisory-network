package com.cdran.controller;

import com.cdran.service.SeasonalRiskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/advisory")
public class SeasonalRiskController {

    private final SeasonalRiskService riskService;

    public SeasonalRiskController(SeasonalRiskService riskService) {
        this.riskService = riskService;
    }

    @GetMapping("/forecasts")
    public String viewSeasonalForecasts(Model model) {
        model.addAttribute("forecasts", riskService.getAllForecasts());
        return "risk-forecast"; 
    }
}