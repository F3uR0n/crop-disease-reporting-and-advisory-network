package com.cdran.controller;

import com.cdran.dto.RegistrationForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.model.enums.Language;
import com.cdran.model.enums.Role;
import com.cdran.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Example of a thin MVC controller:
 * 1. bind + validate the form (@Valid),
 * 2. hand it to a service (business rules),
 * 3. choose the next view or redirect.
 * The login POST itself is handled by Spring Security (see SecurityConfig).
 */
@Controller
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form,
                           BindingResult result,
                           RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            registrationService.register(form);
        } catch (BusinessRuleException ex) {
            if (ex.getField() != null) {
                result.rejectValue(ex.getField(), ex.getMessageKey());
            } else {
                result.reject(ex.getMessageKey());
            }
            return "auth/register";
        }
        String messageKey = form.getAccountType() == Role.EXPERT ? "register.success.expert" : "register.success";
        redirect.addFlashAttribute("successKey", messageKey);
        return "redirect:/login";
    }

    /** Dropdown values for the registration form. */
    @ModelAttribute("accountTypes")
    public Role[] accountTypes() {
        return new Role[]{Role.FARMER, Role.EXPERT};
    }

    @ModelAttribute("languages")
    public Language[] languages() {
        return Language.values();
    }
}
