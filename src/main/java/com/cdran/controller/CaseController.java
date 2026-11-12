package com.cdran.controller;

import com.cdran.dto.CaseSubmissionForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.model.entity.DiseaseCase;
import com.cdran.model.enums.CaseStatus;
import com.cdran.model.enums.GrowthStage;
import com.cdran.service.CaseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

/**
 * Feature 1 (submit a case) and Feature 4 (track cases).
 * Farmers use /farmer/cases/**, admins get a read-only overview at /admin/cases/**.
 * The URL prefix is protected by SecurityConfig; the ownership check is in CaseService.
 */
@Controller
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    // ---------------------------------------------------------------- Farmer

    @GetMapping("/farmer/cases/new")
    public String showForm(Model model) {
        model.addAttribute("form", new CaseSubmissionForm());
        addFormOptions(model);
        return "cases/new";
    }

    @PostMapping("/farmer/cases")
    public String submit(@Valid @ModelAttribute("form") CaseSubmissionForm form, BindingResult result,
                         Principal principal, Model model) {
        if (result.hasErrors()) {
            addFormOptions(model);
            return "cases/new";
        }
        try {
            DiseaseCase saved = caseService.submit(form, principal.getName());
            return "redirect:/farmer/cases/" + saved.getId(); // Post/Redirect/Get: refresh won't resubmit
        } catch (BusinessRuleException ex) {
            result.rejectValue(ex.getField(), ex.getMessageKey());
            addFormOptions(model);
            return "cases/new";
        }
    }

    @GetMapping("/farmer/cases")
    public String myCases(Principal principal, Model model) {
        model.addAttribute("cases", caseService.listForFarmer(principal.getName()));
        model.addAttribute("baseUrl", "/farmer/cases");
        return "cases/list";
    }

    @GetMapping("/farmer/cases/{id}")
    public String myCase(@PathVariable Long id, Principal principal, Model model) {
        model.addAttribute("diseaseCase", caseService.getForFarmer(id, principal.getName()));
        model.addAttribute("mainStages", CaseStatus.MAIN_STAGES);
        model.addAttribute("backUrl", "/farmer/cases");
        return "cases/detail";
    }

    // ---------------------------------------------------------------- Admin

    @GetMapping("/admin/cases")
    public String allCases(Model model) {
        model.addAttribute("cases", caseService.listAll());
        model.addAttribute("baseUrl", "/admin/cases");
        model.addAttribute("showFarmer", true);
        return "cases/list";
    }

    @GetMapping("/admin/cases/{id}")
    public String anyCase(@PathVariable Long id, Model model) {
        model.addAttribute("diseaseCase", caseService.getAny(id));
        model.addAttribute("mainStages", CaseStatus.MAIN_STAGES);
        model.addAttribute("backUrl", "/admin/cases");
        return "cases/detail";
    }

    /** Dropdown and checklist values for the submission form. */
    private void addFormOptions(Model model) {
        model.addAttribute("crops", caseService.allCrops());
        model.addAttribute("symptoms", caseService.allSymptoms());
        model.addAttribute("growthStages", GrowthStage.values());
    }
}
