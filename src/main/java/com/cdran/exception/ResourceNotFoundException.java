package com.cdran.exception;

/** Thrown when a requested record (case, field, article, ...) does not exist. Rendered as a 404 page. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String what, Object id) {
        super(what + " not found: " + id);
    }
}
