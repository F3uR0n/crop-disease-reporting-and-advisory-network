# Crop Disease Reporting & Advisory Network

## Project Status

This repository contains the code & documentation for a CSE470 Software Engineering project.
Sprint 0 (foundation) is complete: project skeleton, MVC layout, role-based login, registration
and Bangla/English switching. Implemented so far: **F1 Disease Case Submission** and **F4 Case Tracking**
(status history, dashboard, notifications). Case photos are saved as files in `uploads/` (git-ignored);
the database stores only their paths. See [docs/roadmap.md](docs/roadmap.md) for the full plan and
[docs/class-diagram.md](docs/class-diagram.md) for the domain model.

## Project Overview

The Crop Disease Reporting & Advisory Network is a proposed system for reporting
crop disease cases and coordinating diagnosis, expert review, treatment advice,
follow-up, and regional disease awareness.

The system is intended to connect farmers with disease-reporting and advisory
workflows. A farmer will be able to submit a crop case with relevant crop
information, symptoms, photographs, and location. The proposed workflow can use
an AI-assisted analysis to identify a likely disease, while cases with
insufficient confidence can be escalated to an expert. The system will also
support knowledge sharing, preventive advisories, outbreak monitoring, and
follow-up reporting.

The project is being designed as a substantial software engineering system rather
than a collection of independent CRUD screens. Each proposed feature represents a
meaningful workflow or system capability and will require requirements,
acceptance criteria, validation, and testing before implementation.

## Target Users and Roles

The planned system includes the following user groups:

- **Farmers:** Submit and track crop disease cases, manage farm information,
  receive advisories and alerts and report treatment outcomes.
- **Agricultural experts:** Review escalated cases, confirm or correct diagnoses,
  prescribe treatments and maintain knowledge-base content.
- **Field officers:** Verify on-ground reports and support moderation in
  high-risk areas.
- **Administrators:** Manage expert approval, flagged content, duplicate or fake
  reports and other administrative workflows.

The final roles, permissions, and approval rules will be documented in the SRS
before implementation.

## Proposed Features

The following 20 features are planned for the project.

1. **Disease Case Submission:** Submit crop photographs, crop type, growth stage,
   symptoms, and GPS location; validate image quality and store the case.
2. **AI Disease Detection:** Analyze a leaf image through a planned AI/ML service
   and return a likely disease with a confidence score.
3. **Low-Confidence Escalation:** Route cases below a defined confidence
   threshold to the expert queue instead of presenting an unreliable result.
4. **Case Tracking:** Show case status, history, and notifications through stages
   such as submitted, AI-analyzed, under review, and resolved.
5. **Farm and Crop Profile Management:** Manage multiple fields, crops, planting
   dates, and soil type, with each case linked to a field.
6. **Expert Review Queue:** Prioritize cases using severity, confidence, and
   waiting time; allow an expert to claim, confirm, or correct a diagnosis.
7. **Treatment Prescription:** Attach a treatment plan containing product or
   organic options, dosage, schedule, and precautions.
8. **Diagnosis Feedback Loop:** Store expert corrections as labeled data for
   later AI accuracy measurement and model retraining.
9. **Expert Workload Balancing:** Assign cases by crop specialty and current
   workload, with reassignment when an expert does not respond in time.
10. **Follow-up and Recovery Check:** Schedule a reminder to record whether a
    treatment worked and store the reported outcome.
11. **Outbreak Detection:** Flag a possible outbreak when confirmed cases of the
    same disease in an area exceed a threshold within a time window.
12. **Regional Outbreak Map:** Display disease clusters, severity, and spread over
    time, with crop and disease filters.
13. **Location-Based Alerts:** Alert farmers growing an affected crop within a
    defined radius and provide preventive steps.
14. **Seasonal Risk Forecast:** Calculate a rule-based or basic statistical risk
    score for a region and crop using outbreak history and season.
15. **Analytics Dashboard:** Present trends by disease, region, crop, and month,
    together with AI accuracy and expert response time.
16. **Disease and Treatment Knowledge Base:** Provide a searchable library of
    symptoms, causes, prevention, and treatments managed by experts.
17. **Preventive Advisory Engine:** Generate crop-specific tips using a farmer's
    crops, growth stage, and current regional risk.
18. **Community Discussion Board:** Allow farmers to ask questions and experts
    to provide answers that may be marked as verified.
19. **Multilingual Support:** Plan the interface and advisories for Bangla and
    English, with content stored per language.
20. **Field Officer Verification and Moderation:** Support on-ground report
    verification and administrative handling of expert approval, flagged
    content, and duplicate or fake reports.

21. **Farmer Advisory Chatbot:** Answer farmers' questions in Bangla or English
    using their crops, their case and the knowledge base as context.

## MVC Architecture

Spring MVC with server-rendered Thymeleaf views:

- **Model:** JPA entities (`model/entity`), repositories (`repository`) and
  services holding the business rules (`service`).
- **View:** Thymeleaf templates (`src/main/resources/templates`), display only.
- **Controller:** `@Controller` classes (`controller`) that validate input, call a
  service and choose the view.

Details, request trace and conventions: [docs/roadmap.md](docs/roadmap.md).

### AI Service Boundary

Instead of training a CNN, the application calls an external LLM API (vision
model for diagnosis, chat model for the chatbot) directly from Java through the
`DiseaseClassifier` interface. There is no Python service. Low-confidence or
failed AI results are escalated to an expert.

## Technology Stack

| Concern | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 (Spring MVC, Spring Security, Spring Data JPA) |
| Views | Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js |
| Database | MySQL 8, Flyway migrations (H2 in-memory for tests / quick start) |
| AI | External LLM API via Spring `RestClient` |
| Build / tests | Maven wrapper, JUnit 5, Mockito, MockMvc |

## Getting Started

Requires JDK 21+. Maven is not needed (wrapper included).

```bash
# Quick start without MySQL (in-memory DB, admin login 01700000000 / admin12345)
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2      # Windows: mvnw.cmd ...

# With MySQL: copy .env.example to .env, fill in values, then
./mvnw spring-boot:run

# Run tests
./mvnw test
```

Open http://localhost:8080.
