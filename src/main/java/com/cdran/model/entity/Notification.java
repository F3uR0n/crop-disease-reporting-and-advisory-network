package com.cdran.model.entity;

import com.cdran.model.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * In-app notification for one user.
 * The text is stored as an i18n message key + one argument (e.g. "notification.case.SUBMITTED", "12"),
 * so the same notification is shown in English or Bangla depending on the reader's language.
 */
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false, length = 100)
    private String messageKey;

    @Column(length = 100)
    private String messageArg;

    /** Page to open when the notification is clicked, e.g. "/farmer/cases/12". */
    @Column(length = 255)
    private String link;

    /** Null while unread. */
    private LocalDateTime readAt;

    protected Notification() {
    }

    public Notification(User user, NotificationType type, String messageKey, String messageArg, String link) {
        this.user = user;
        this.type = type;
        this.messageKey = messageKey;
        this.messageArg = messageArg;
        this.link = link;
    }

    public boolean isRead() {
        return readAt != null;
    }

    public void markRead() {
        if (readAt == null) {
            readAt = LocalDateTime.now();
        }
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getMessageArg() {
        return messageArg;
    }

    public String getLink() {
        return link;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }
}
