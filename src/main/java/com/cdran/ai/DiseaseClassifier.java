package com.cdran.ai;

/**
 * Contract for anything that can diagnose a crop image (Feature 2).
 * Services depend on this interface, never on a concrete provider (Strategy pattern):
 * swap the LLM provider, or plug in a real CNN service later, without touching services or controllers.
 *
 * TODO (Sprint 2): implement LlmVisionClassifier using Spring RestClient + AiProperties.
 */
public interface DiseaseClassifier {

    /**
     * @param image       raw image bytes (JPEG/PNG, already passed image-quality validation)
     * @param mimeType    e.g. "image/jpeg"
     * @param context     crop name, growth stage and symptom checklist, as plain text for the prompt
     * @return prediction with label + confidence; never null - throw AiServiceException on failure
     */
    Prediction classify(byte[] image, String mimeType, String context);
}
