package com.cdran.controller;

import com.cdran.service.NotificationService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/**
 * Adds "unreadCount" to the model of every page, so the navbar can show the
 * red badge on the notification bell. 0 when nobody is logged in.
 */
@ControllerAdvice
public class UnreadCountAdvice {

    private final NotificationService notificationService;

    public UnreadCountAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute("unreadCount")
    public long unreadCount(Principal principal) {
        return principal == null ? 0 : notificationService.countUnread(principal.getName());
    }
}
