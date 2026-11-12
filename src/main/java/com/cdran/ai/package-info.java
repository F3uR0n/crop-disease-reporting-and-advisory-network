/**
 * <b>Integration with the external LLM provider.</b> {@code DiseaseClassifier} (image diagnosis) and the chatbot client sit behind interfaces so services do not depend on a specific provider (Strategy pattern). All HTTP calls use Spring {@code RestClient}.
 */
package com.cdran.ai;
