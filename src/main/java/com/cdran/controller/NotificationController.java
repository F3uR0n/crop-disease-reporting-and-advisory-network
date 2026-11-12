package com.cdran.controller;

import com.cdran.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

/** Notifications page for any logged-in user (Feature 4). */
@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String list(Principal principal, Model model) {
        model.addAttribute("notifications", notificationService.listFor(principal.getName()));
        return "notifications";
    }

    /** Clicking a notification marks it read and opens the page it is about. */
    @PostMapping("/notifications/{id}/open")
    public String open(@PathVariable Long id, Principal principal) {
        return "redirect:" + notificationService.open(id, principal.getName());
    }

    @PostMapping("/notifications/read-all")
    public String markAllRead(Principal principal) {
        notificationService.markAllRead(principal.getName());
        return "redirect:/notifications";
    }
}
