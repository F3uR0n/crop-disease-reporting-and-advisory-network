package com.cdran.exception;

/**
 * Thrown by services when a business rule is broken (e.g. duplicate phone, illegal case status change).
 * {@code field} lets a controller attach the error to a specific form field; null means a global error.
 * {@code messageKey} is an i18n key from messages_*.properties.
 */
public class BusinessRuleException extends RuntimeException {

    private final String field;
    private final String messageKey;

    public BusinessRuleException(String field, String messageKey) {
        super(messageKey);
        this.field = field;
        this.messageKey = messageKey;
    }

    public String getField() {
        return field;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
