package com.cdran.ai;

/** AI provider failed (timeout, bad response, quota). Callers escalate the case to an expert instead. */
public class AiServiceException extends RuntimeException {

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiServiceException(String message) {
        super(message);
    }
}
