# Crop Disease Reporting & Advisory Network

## Project Status

This repository contains the code & documentation for a CSE470 Software Engineering project. 
Implementation has not started yet.

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

## Planned MVC Architecture

The main application must use a framework that visibly supports the
Model-View-Controller (MVC) architecture. The specific framework has not yet
been selected.

### AI/ML Service Boundary

AI disease detection is a planned feature not a completed component. If a
Python-based AI/ML service is needed, it will be kept separate from the primary
MVC application and connected through a clearly defined API or interface. The
main application will still use an approved MVC-oriented framework and the
team will document the data flow, service boundary, confidence handling and
limitations.

## Technology Stack

The technology stack has not been selected yet.

## Project Directory

This directory is the starting point for the Crop Disease Reporting & Advisory
Network project. Source code, tests, configuration and supporting
documentation will be added after the planning and approval stages.
