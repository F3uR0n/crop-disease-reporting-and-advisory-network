package com.cdran.controller;

import com.cdran.model.enums.CaseStatus;
import com.cdran.service.CaseService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Landing page and role-based dashboard routing.
 * After login every user hits /dashboard and is redirected to their own area.
 */
@Controller
public class HomeController {

    private final CaseService caseService;

    public HomeController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (hasRole(auth, "ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        if (hasRole(auth, "ROLE_EXPERT")) {
            return "redirect:/expert/dashboard";
        }
        if (hasRole(auth, "ROLE_FIELD_OFFICER")) {
            return "redirect:/officer/dashboard";
        }
        return "redirect:/farmer/dashboard";
    }

    @GetMapping("/farmer/dashboard")
    public String farmerDashboard(Principal principal, Model model) {
        model.addAttribute("mainStages", CaseStatus.MAIN_STAGES);
        model.addAttribute("statusCounts", caseService.statusCounts(principal.getName()));
        model.addAttribute("recentCases", caseService.recentForFarmer(principal.getName()));
        return "farmer/dashboard";
    }

    @GetMapping("/expert/dashboard")
    public String expertDashboard() {
        return "expert/dashboard";
    }

    @GetMapping("/officer/dashboard")
    public String officerDashboard() {
        return "officer/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
