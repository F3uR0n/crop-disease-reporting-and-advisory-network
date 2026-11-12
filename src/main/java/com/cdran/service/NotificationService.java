package com.cdran.service;

import com.cdran.exception.ResourceNotFoundException;
import com.cdran.model.entity.Notification;
import com.cdran.model.entity.User;
import com.cdran.model.enums.NotificationType;
import com.cdran.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Creates and reads in-app notifications (Feature 4).
 * Other features (expert review, outbreak alerts, follow-ups...) also call notify().
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void notify(User user, NotificationType type, String messageKey, String messageArg, String link) {
        notificationRepository.save(new Notification(user, type, messageKey, messageArg, link));
    }

    @Transactional(readOnly = true)
    public List<Notification> listFor(String phone) {
        return notificationRepository.findByUserPhoneOrderByCreatedAtDesc(phone);
    }

    @Transactional(readOnly = true)
    public long countUnread(String phone) {
        return notificationRepository.countByUserPhoneAndReadAtIsNull(phone);
    }

    /** Marks one notification as read and returns the page it points to. */
    @Transactional
    public String open(Long id, String phone) {
        Notification notification = notificationRepository.findByIdAndUserPhone(id, phone)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        notification.markRead();
        return notification.getLink() != null ? notification.getLink() : "/notifications";
    }

    @Transactional
    public void markAllRead(String phone) {
        notificationRepository.findByUserPhoneAndReadAtIsNull(phone).forEach(Notification::markRead);
    }
}
