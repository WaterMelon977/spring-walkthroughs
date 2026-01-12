# Social Login Project Context

## Project Overview

This is a **Spring Boot** application demonstrating **OAuth2 Social Login** combined with **Stateless JWT Authentication**. It serves as a backend service that allows users to log in via third-party providers (GitHub, Google) and issues a JWT for accessing protected resources.

### Key Technologies
*   **Language:** Java 25
*   **Framework:** Spring Boot 4.0.1
*   **Security:** Spring Security, OAuth2 Client, JJWT (Java JWT)
*   **Build Tool:** Maven

### Architecture
The application follows a standard layered architecture:
*   **Config:** Security and application-specific configurations (`SecurityConfig`, `AppProperties`).
*   **Controller:** REST endpoints for authentication and user data (`AuthController`, `UserController`).
*   **Security:**
    *   `oauth`: Handles OAuth2 login success logic.
    *   `jwt`: Manages JWT creation, parsing, and validation.
    *   `JwtAuthenticationFilter`: Intercepts requests to validate JWTs.

## Configuration

The application requires configuration for OAuth2 providers and JWT generation.

### `application.yaml`
A template is available at `src/main/resources/application.yaml.example`. You **must** create a `src/main/resources/application.yaml` with the following:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: <YOUR_GITHUB_CLIENT_ID>
            client-secret: <YOUR_GITHUB_CLIENT_SECRET>
          google:
            client-id: <YOUR_GOOGLE_CLIENT_ID>
            client-secret: <YOUR_GOOGLE_CLIENT_SECRET>

application:
  frontend-url: http://localhost:3000 # URL of the client application
  security:
    jwt:
      secret: <YOUR_JWT_SECRET> # Must be at least 32 chars
      expiration-ms: 900000     # Token validity duration
      cookie-name: ACCESS_TOKEN # Name of the cookie storing the JWT
```

## Building and Running

### Prerequisites
*   JDK 25 (defined in `pom.xml`)
*   Maven (wrapper included)

### Commands

**Build:**
```bash
./mvnw clean install
```

**Run:**
```bash
./mvnw spring-boot:run
```

**Run Tests:**
```bash
./mvnw test
```

## Development Conventions

*   **Security:** The application uses a stateless session policy. State is maintained via JWTs stored in HTTP-only cookies.
*   **CORS:** configured to allow requests from the `application.frontend-url`.
*   **Formatting:** Follows standard Java/Spring conventions.
*   **Testing:** `spring-boot-starter-test` and `spring-security-test` are available for unit and integration testing.

## Key Files
*   `src/main/java/com/example/social_login/config/SecurityConfig.java`: Central security definition (filter chain, CORS, CSRF).
*   `src/main/java/com/example/social_login/security/oauth/OAuth2LoginSuccessHandler.java`: Generates JWT upon successful OAuth2 login.
*   `src/main/java/com/example/social_login/security/jwt/JwtAuthenticationFilter.java`: Validates JWT on incoming requests.
*   `pom.xml`: Project dependencies and build configuration.
