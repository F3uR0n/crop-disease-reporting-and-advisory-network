package com.cdran.dto;

import com.cdran.model.enums.FlagTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ContentFlagForm {

    @NotNull(message = "{forum.validation.flagTarget.required}")
    private FlagTarget targetType;

    @NotNull(message = "{forum.validation.flagTargetId.required}")
    private Long targetId;

    @NotBlank(message = "{forum.validation.flagReason.required}")
    @Size(max = 600, message = "{forum.validation.flagReason.size}")
    private String reason;

    public FlagTarget getTargetType() { return targetType; }
    public void setTargetType(FlagTarget targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}