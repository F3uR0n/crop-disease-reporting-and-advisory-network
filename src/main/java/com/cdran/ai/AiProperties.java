package com.cdran.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed binding of the app.ai.* settings in application.properties.
 * The API key comes from the AI_API_KEY environment variable / .env - never hard-code it.
 */
@ConfigurationProperties(prefix = "app.ai")
public record AiProperties(String baseUrl, String apiKey, String visionModel, String chatModel, int timeoutSeconds) {

    public boolean isConfigured() {
        return baseUrl != null && !baseUrl.isBlank() && apiKey != null && !apiKey.isBlank();
    }
}
