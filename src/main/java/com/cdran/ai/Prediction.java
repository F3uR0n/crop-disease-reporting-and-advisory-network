package com.cdran.ai;

/**
 * Result returned by a {@link DiseaseClassifier}. Immutable value object.
 *
 * @param label       disease name as returned by the model (matched against the Disease table later)
 * @param confidence  0.0 - 1.0. For an LLM this is self-reported, not calibrated, so the
 *                    escalation threshold is tuned from measured accuracy (Feature 8)
 * @param reasoning   short explanation shown to the expert reviewer
 * @param modelName   which model produced it (stored for accuracy analytics)
 * @param rawResponse full provider response, stored for debugging/audit
 */
public record Prediction(String label, double confidence, String reasoning, String modelName, String rawResponse) {

    public Prediction {
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence must be between 0 and 1, was " + confidence);
        }
    }

    public boolean isBelow(double threshold) {
        return confidence < threshold;
    }
}
