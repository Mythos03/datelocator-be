# AI Agent Guidelines for DateLocator Backend

## Project Overview

**DateLocator Backend** is a Spring Boot REST API (Kotlin + Gradle) enabling social date planning through venue matching. It uses Keycloak OAuth2/JWT authentication, PostgreSQL persistence, and integrates Google Maps services.

**Tech Stack**: Kotlin 1.9.25, Spring Boot 3.4.4, Java 21, PostgreSQL, Keycloak 26.5.5, JPA/Hibernate

## Architecture Patterns

### Module Structure: Feature-Based Organization

The codebase uses **domain-driven module layout** where each feature owns its complete stack:

```
src/main/kotlin/com/datelocator/datelocatorbe/
├── config/           # Security & Swagger configuration
├── security/         # JWT/OAuth2 filters & converters
├── user/             # User management (User entity, UserService, UserController)
├── venue/            # Venue management & preferences
├── review/           # Review functionality
├── votes/            # Venue preference voting system
├── preference/       # Date preference entities & matching
├── image/            # Image storage abstraction
└── [module]/models/  # DTOs & JPA entities (one per feature)
```

**Pattern**: Each module has 4 core files following this layout:
- `[Feature]Entity` or `[Feature]` (JPA `@Entity` in `models/`)
- `[Feature]Repository` (Spring Data JPA interface)
- `[Feature]Service` (`@Service` with business logic, `@Transactional`)
- `[Feature]Controller` (`@RestController` with `@RequestMapping("/api/[features]")`)
- `[Feature]Mapper` (`@Component` for Entity ↔ DTO conversion)

### Authentication & Security

**Keycloak Integration** (`config/SecurityConfig.kt`):
- JWT tokens validated via `spring.security.oauth2.resourceserver.jwt`
- Multi-issuer support: accepts issuers from env var `app.security.jwt.accepted-issuers` (comma-separated)
- `UserController` + `UserService.syncUser` handle user metadata sync from JWT claims
- `KeycloakJwtAuthenticationConverter` extracts authorities from JWT
- Controllers use `@AuthenticationPrincipal jwt: Jwt` to inject authenticated user's JWT

**Critical**: All user-sensitive endpoints require `jwt.subject` (Keycloak user ID as UUID string) from the JWT principal.

### Data Model: Entity Relationships

- **User** (keycloakId PK, unique username): has Preferences (ManyToMany), owns Reviews, owns Image
- **Venue** (UUID, location-based): has Preferences (votes-based), has OpeningHours, has Reviews, has createdBy User
- **Preference** (uuid): DateType like "dinner", "movie night" (shared across users)
- **VenuePreferenceVote**: Records individual user votes on venue+preference combos, aggregated by count with threshold (`DEFAULT_VOTE_THRESHOLD = 1`)
- **Review**: User's written review linked to Venue

**Convention**: All entity PKs are UUIDs except enum-backed fields use `EnumType.ORDINAL`

### Service Layer Patterns

Services have consistent patterns:

```kotlin
@Service
@Transactional  // Applied to class, enables auto-rollback on unchecked exceptions
class SomethingService(
    private val someRepository: SomeRepository,
    private val otherService: OtherService,
    private val mapper: SomeMapper
) {
    // Business logic with logging via LoggerFactory
    private val logger = LoggerFactory.getLogger(SomethingService::class.java)
    
    // DTOs returned to clients, entities kept internal
    fun doSomething(dto: SomeRequestDto): SomeResponseDto? {
        logger.info("Starting operation")
        val entity = repository.save(mapper.toEntity(dto))
        return mapper.toResponseDto(entity)
    }
}
```

Cross-service calls are allowed (e.g., VenueService calls UserService, PreferenceService).

### API Response Convention

Controllers wrap responses in `ResponseEntity`:
- Success: `ResponseEntity.ok(dto)` or `ResponseEntity.status(HttpStatus.CREATED).body(dto)`
- Errors: Log with `logger.error()`, return `ResponseEntity.status(errorCode).body(mapOf("error" to message))`
- Not found: `ResponseEntity.notFound().build()`

**Note**: VenueController explicitly logs request bodies at DEBUG level; follow this for complex operations.

### Mapper Pattern

Mappers convert between entities and DTOs, injecting nested mappers for compositions:

```kotlin
@Component
class SomeMapper(private val nestedMapper: NestedMapper) {
    fun toResponseDto(entity: Some): SomeResponseDto =
        SomeResponseDto(
            id = entity.id.toString(),
            nested = entity.nested.map { nestedMapper.toResponseDto(it) }.toSet()
        )
}
```

## Build & Deployment

### Local Development

```bash
# Start services (Docker: Postgres + Keycloak)
cd docker && docker compose up --build

# Build & run application
./gradlew bootRun

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

**Java Version**: Java 21 required (configured in `build.gradle.kts` via `toolchain`)

### Configuration

Application runs on `0.0.0.0:8080`, configured in `src/main/resources/application.properties`:
- PostgreSQL: `localhost:5433/datelocator-db` (via Docker, default user: `datelocator`/`datelocator`)
- Keycloak: `localhost:8081` (realm: `datelocator`)
- Accepted issuers: env vars `KEYCLOAK_ACCEPTED_ISSUERS`, `KEYCLOAK_ISSUER_URI`, `KEYCLOAK_JWK_SET_URI`
- Swagger UI: `http://localhost:8080/swagger-ui.html` (OAuth2 enabled via Keycloak)

**Spring JPA**: `ddl-auto=update` (auto-evolves schema), `show-sql=true` + `format_sql=true` for debugging

### Gradle Specifics

- **Kotlin Plugin**: 1.9.25 with Spring plugin (allOpen annotation processing for JPA entities)
- **Dependencies**: Spring Data JPA, Web, Security, OAuth2 Resource Server, Jackson Kotlin, Google Maps API, PostgreSQL, SpringDoc OpenAPI, Gson
- **Tests**: JUnit 5 with Kotlin test support (configured via `useJUnitPlatform()`)

## Common Developer Workflows

### Adding a New Feature Module

1. Create `src/main/kotlin/com/datelocator/datelocatorbe/[feature]/`
2. Add entity in `models/[Feature].kt` with JPA annotations (`@Entity`, `@Table`, relationships)
3. Create `[Feature]Repository` extending `JpaRepository<Entity, UUID>`
4. Implement `[Feature]Service` with `@Transactional` and inject repository
5. Add `[Feature]Mapper` for DTO conversion
6. Create `[Feature]Controller` with `@RestController` and `@RequestMapping` routes
7. Define request/response DTOs in `models/` (separate from JPA entities)

### Handling Authentication

Extract Keycloak UUID from `@AuthenticationPrincipal jwt: Jwt`:

```kotlin
@PostMapping
fun register(@AuthenticationPrincipal jwt: Jwt, @RequestBody dto: UserRequestDto) {
    val keycloakId = jwt.subject  // UUID string
    // Pass to service...
}
```

Services then call `userRepository.findByKeycloakId(UUID.fromString(keycloakId))` to load user context.

### Entity Composition

Use `@ManyToMany`, `@OneToMany`, `@OneToOne` with explicit `@JoinTable` or `mappedBy`. Lazy loading is default; load aggressively in service if needed. `cascade = [CascadeType.ALL]` enables cascading deletes (e.g., reviews deleted when user deleted).

### Error Handling

- Services throw `IllegalArgumentException` for validation; controllers catch and return 400
- Services throw `RuntimeException` for runtime failures; controllers log and return 500
- Always log at INFO for significant operations, DEBUG for request/response bodies

## Integration Points

### Google Maps Service

VenueService imports & uses Google Maps API (gradle dependency: `com.google.maps:google-maps-services:2.2.0`). Venue creation integrates geocoding; review VenueService and VenueRequestDto for coordinate format.

### PostgreSQL & Hibernate

JPA entities automatically mapped to `spring.jpa.hibernate.ddl-auto=update` schema. Relationships use foreign keys; inspect `User.kt` and `Venue.kt` for column naming conventions (snake_case in DB, camelCase in Kotlin).

### OpenAPI/Swagger

SpringDoc configured in `SwaggerConfig.kt` with OAuth2 scheme pointing to Keycloak. Swagger UI accessible at `/swagger-ui.html` and uses Keycloak login for token acquisition.

## Common Pitfalls & Conventions

- **UUID Handling**: Entities use `UUID` type; DTOs expose as `.toString()` strings in JSON. Always convert strings to UUID in service layer (`UUID.fromString(keycloakId)`).
- **Transactionality**: Never call `@Transactional` services from non-transactional contexts without explicit transaction management.
- **DTO Exposure**: Never return JPA entities directly from controllers; always map through mapper to DTO for API contract stability.
- **Lazy Loading**: Accessing unmapped collections outside service scope throws `LazyInitializationException`. Load in service via explicit queries or eager fetch strategies.
- **Logging Levels**: DEBUG for trace data (request/response bodies), INFO for business events, ERROR for exceptions. See VenueController example.

## Key Files to Understand the System

| File | Purpose |
|------|---------|
| `build.gradle.kts` | All dependencies, Java version, Gradle plugins |
| `src/main/resources/application.properties` | Database, Keycloak, server config |
| `config/SecurityConfig.kt` | OAuth2 JWT validation, issuer whitelist, bearer token flow |
| `user/UserController.kt` + `user/UserService.kt` | User synchronization from JWT claims (`/api/users/me`) |
| `user/UserService.kt` + `user/models/User.kt` | Core user entity and business logic |
| `venue/VenueService.kt` + `venue/VenueController.kt` | Venue creation, preferences, Google Maps integration |
| `votes/VenuePreferenceVote.kt` | Vote aggregation model linking users to venue+preference combos |
| `docker/docker-compose.yml` | Postgres + Keycloak setup |
| `docker/realm-export.json` | Keycloak realm configuration export |
