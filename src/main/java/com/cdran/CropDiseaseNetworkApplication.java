package com.cdran;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point.
 * <ul>
 *   <li>{@code @EnableScheduling} - turns on {@code @Scheduled} jobs in the {@code scheduler} package
 *       (the "Scheduler" system actor: reminders, escalation timeouts, outbreak checks, risk scores).</li>
 *   <li>{@code @EnableAsync} - lets the slow AI call run in the background with {@code @Async}.</li>
 * </ul>
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@ConfigurationPropertiesScan
public class CropDiseaseNetworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(CropDiseaseNetworkApplication.class, args);
	}

}
