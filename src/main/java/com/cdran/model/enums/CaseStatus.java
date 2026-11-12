package com.cdran.model.enums;

import java.util.List;

/**
 * Disease case lifecycle (Feature 4).
 * The allowed moves (State pattern) are written once here and checked by DiseaseCase.transitionTo().
 */
public enum CaseStatus {
    SUBMITTED, AI_ANALYZED, UNDER_REVIEW, RESOLVED, CLOSED, REJECTED;

    /** The four main stages a farmer follows on the dashboard and the case page. */
    public static final List<CaseStatus> MAIN_STAGES = List.of(SUBMITTED, AI_ANALYZED, UNDER_REVIEW, RESOLVED);

    /** True if a case in this status may move to {@code next}. See docs/class-diagram.md. */
    public boolean canMoveTo(CaseStatus next) {
        return switch (this) {
            case SUBMITTED -> next == AI_ANALYZED || next == UNDER_REVIEW || next == REJECTED;
            case AI_ANALYZED -> next == RESOLVED || next == UNDER_REVIEW;
            case UNDER_REVIEW -> next == RESOLVED || next == REJECTED;
            case RESOLVED -> next == CLOSED;
            case CLOSED, REJECTED -> false;
        };
    }
}
