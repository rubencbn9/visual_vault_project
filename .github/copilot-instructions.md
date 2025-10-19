# Copilot Instructions for visual_vault_project

## Project Overview
This is a Spring Boot application for managing video vaults, lists, users, categories, and tags. The codebase follows a layered architecture with clear separation between controllers, services, repositories, DTOs, and entities.

## Key Components
- **Controllers** (`src/main/java/com/visualvault/visual_vault_project/controllers/`): Handle HTTP requests and responses. Each resource (e.g., `Usuario`, `Lista`, `Video`) has its own controller.
- **Services** (`src/main/java/com/visualvault/visual_vault_project/services/`): Business logic. Service interfaces and implementations are separated (e.g., `UsuarioService`, `UsuarioServiceImplement`).
- **Repositories** (`src/main/java/com/visualvault/visual_vault_project/repository/`): Spring Data JPA repositories for data access.
- **Entities** (`src/main/java/com/visualvault/visual_vault_project/entity/`): JPA entities representing domain models (e.g., `Usuario`, `Video`, `Categoria`).
- **DTOs** (`src/main/java/com/visualvault/visual_vault_project/dto/`): Data Transfer Objects for request/response payloads.
- **Configuration** (`src/main/java/com/visualvault/visual_vault_project/configuration/`): App-wide config (e.g., CORS).

## Build & Run
- **Build:** Use Maven wrapper: `./mvnw clean package` (Linux/macOS) or `mvnw.cmd clean package` (Windows).
- **Run:** `./mvnw spring-boot:run` or `mvnw.cmd spring-boot:run`.
- **Tests:** `./mvnw test` or `mvnw.cmd test`. Main test class: `VisualVaultProjectApplicationTests`.

## Patterns & Conventions
- **Lombok** is used for reducing boilerplate in entities and DTOs.
- **Service Implementation Naming:** Implementations use `*Implement` suffix (e.g., `UsuarioServiceImplement`).
- **Security:** Security config is present but commented out in `pom.xml`. If enabled, configs are in `config_security/`.
- **Validation:** Uses `spring-boot-starter-validation` for request validation.
- **Database:** Uses H2 for runtime (see `application.properties`).
- **Actuator:** Enabled for monitoring endpoints.

## Integration Points
- **Spring Data JPA** for persistence.
- **H2 Database** for local development.
- **Spring Boot Actuator** for health/metrics.
- **Lombok** for code generation.

## Example: Adding a New Resource
1. Create Entity in `entity/`.
2. Create Repository in `repository/`.
3. Create Service interface and implementation in `services/`.
4. Create Controller in `controllers/`.
5. Add DTOs if needed in `dto/`.

## Useful Files
- `pom.xml`: Dependency and build config.
- `application.properties`: App settings.
- `VisualVaultProjectApplication.java`: Main entry point.

---
_If any section is unclear or missing, please provide feedback to improve these instructions._
