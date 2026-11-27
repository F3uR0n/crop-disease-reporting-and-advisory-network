package com.cdran.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdran.dto.DiagnosisFeedbackForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.model.enums.FollowUpStatus;
import com.cdran.model.enums.RecoveryOutcome;
import com.cdran.model.enums.ReviewDecision;
import com.cdran.service.DiagnosisFeedbackService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cases/{caseId}/feedback")
@PreAuthorize("isAuthenticated()")
public class DiagnosisFeedbackController {

    private final DiagnosisFeedbackService feedbackService;

    public DiagnosisFeedbackController(DiagnosisFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public String showForm(@PathVariable Long caseId, Principal principal, Model model) {
        model.addAttribute("caseId", caseId);
        model.addAttribute("feedbackForm", feedbackService.loadFormForCase(caseId, principal.getName()));
        populateOptions(model);
        return "feedback/form";
    }

    @PostMapping
    public String submit(
            @PathVariable Long caseId,
            @Valid @ModelAttribute("feedbackForm") DiagnosisFeedbackForm form,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("caseId", caseId);
            populateOptions(model);
            return "feedback/form";
        }
        try {
            feedbackService.submitOrUpdate(caseId, form, principal.getName());
        } catch (BusinessRuleException ex) {
            bindingResult.rejectValue(ex.getField(), ex.getMessageKey());
            model.addAttribute("caseId", caseId);
            populateOptions(model);
            return "feedback/form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "feedback.saved");
        return "redirect:/cases/" + caseId + "/feedback";
    }

    private void populateOptions(Model model) {
        model.addAttribute("decisions", ReviewDecision.values());
        model.addAttribute("outcomes", RecoveryOutcome.values());
        model.addAttribute("followUpStatuses", FollowUpStatus.values());
    }
}