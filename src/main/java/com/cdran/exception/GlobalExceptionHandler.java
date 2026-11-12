package com.cdran.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Central error handling for all controllers, so individual controllers stay free of try/catch noise.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    /** Upload bigger than Spring's multipart limit: back to the case form with a message. */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleUploadTooLarge() {
        return "redirect:/farmer/cases/new?tooLarge";
    }
}
