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

import com.cdran.dto.EscalationForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.service.LowConfidenceEscalationService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cases/{caseId}/escalations")
@PreAuthorize("isAuthenticated()")
public class LowConfidenceEscalationController {

    private final LowConfidenceEscalationService escalationService;

    public LowConfidenceEscalationController(LowConfidenceEscalationService escalationService) {
        this.escalationService = escalationService;
    }

    @GetMapping
    public String showForm(@PathVariable Long caseId, Principal principal, Model model) {
        model.addAttribute("caseId", caseId);
        model.addAttribute("threshold", escalationService.getConfidenceThreshold());
        model.addAttribute("escalations", escalationService.listForCase(caseId, principal.getName()));
        model.addAttribute("escalationForm", new EscalationForm());
        return "escalations/form";
    }

    @PostMapping
    public String escalate(
            @PathVariable Long caseId,
            @Valid @ModelAttribute("escalationForm") EscalationForm form,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("caseId", caseId);
            model.addAttribute("threshold", escalationService.getConfidenceThreshold());
            model.addAttribute("escalations", escalationService.listForCase(caseId, principal.getName()));
            return "escalations/form";
        }
        try {
            escalationService.escalateExplicitly(caseId, form, principal.getName());
        } catch (BusinessRuleException ex) {
            bindingResult.rejectValue(ex.getField(), ex.getMessageKey());
            model.addAttribute("caseId", caseId);
            model.addAttribute("threshold", escalationService.getConfidenceThreshold());
            model.addAttribute("escalations", escalationService.listForCase(caseId, principal.getName()));
            return "escalations/form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "escalation.created");
        return "redirect:/cases/" + caseId + "/escalations";
    }
}