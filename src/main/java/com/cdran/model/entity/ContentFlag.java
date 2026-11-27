package com.cdran.model.entity;

import java.time.LocalDateTime;

import com.cdran.model.enums.FlagStatus;
import com.cdran.model.enums.FlagTarget;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "content_flags")
public class ContentFlag extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 32)
    private FlagTarget targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(nullable = false, length = 600)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FlagStatus status = FlagStatus.OPEN;

    @Column(name = "action_taken", length = 300)
    private String actionTaken;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public void resolve(User resolver, String action) {
        this.status = FlagStatus.ACTION_TAKEN;
        this.resolvedBy = resolver;
        this.actionTaken = action;
        this.resolvedAt = LocalDateTime.now();
    }

    public void dismiss(User resolver) {
        this.status = FlagStatus.DISMISSED;
        this.resolvedBy = resolver;
        this.resolvedAt = LocalDateTime.now();
    }

    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }

    public User getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(User resolvedBy) { this.resolvedBy = resolvedBy; }

    public FlagTarget getTargetType() { return targetType; }
    public void setTargetType(FlagTarget targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public FlagStatus getStatus() { return status; }
    public void setStatus(FlagStatus status) { this.status = status; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}