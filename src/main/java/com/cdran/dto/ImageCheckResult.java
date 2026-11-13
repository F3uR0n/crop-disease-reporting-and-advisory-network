package com.cdran.dto;

/** Measurements of an image that passed the quality check; saved on CaseImage. */
public record ImageCheckResult(int width, int height, double sharpness) {
}
