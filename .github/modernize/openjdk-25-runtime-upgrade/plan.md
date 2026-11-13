# Modernization Plan: OpenJDK 25 LTS Runtime Upgrade

**Project**: Crop Disease Reporting and Advisory Network (`crop-disease-network`)

---

## Technical Framework

- **Language**: Java 21; target OpenJDK 25 LTS
- **Framework**: Spring Boot 4.1.2
- **Build Tool**: Maven Wrapper
- **Database**: MySQL; H2 for tests and the `h2` profile
- **Key Dependencies**: Spring MVC, Spring Security, Spring Data JPA, Thymeleaf

## Overview

Upgrade the application's Java runtime and build toolchain declarations from Java 21 to OpenJDK 25 LTS. The assessment identifies the current Java version declaration in `pom.xml` as not using the latest LTS. The change will:

- Align runtime and toolchain declarations with Java 25.
- Apply only compatibility fixes directly required for the Java 25 build and runtime.
- Preserve existing application behavior and avoid unrelated framework or dependency upgrades.

The tagged farmer HTML template is outside the scope and must remain unchanged unless a directly necessary compatibility issue is demonstrated.

## Migration Impact Summary

| Application | Original Service | New Azure Service | Authentication | Comments |
|-------------|------------------|-------------------|----------------|----------|
| crop-disease-network | Java 21 runtime | OpenJDK 25 LTS | N/A | Runtime/toolchain upgrade only |

No Azure service migration, infrastructure provisioning, containerization, or deployment is included.

## Open Questions & Questionnaire

- [x] Q: Should infrastructure be provisioned? → A: No; this is a code/toolchain-only upgrade (inferred from the request and repository).
- [x] Q: Should integration verification be included? → A: Yes, using mocked/test-local dependencies (default inferred because no external test infrastructure is specified).
- [x] Q: Should security/CVE remediation be included? → A: Yes (default plan setting).
- [x] Q: Is cloud deployment requested? → A: No; the request is limited to the runtime upgrade.
- [x] Q: Is containerization requested? → A: No; no deployment target was requested.
