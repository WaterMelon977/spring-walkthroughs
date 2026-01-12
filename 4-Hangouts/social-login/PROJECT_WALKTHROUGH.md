# Project Walkthrough: Stateless OAuth2 with JWT

Welcome! This guide is designed for Spring Security enthusiasts who want to understand how to bridge the gap between **OAuth2 Social Login** (which typically relies on sessions) and **Stateless JWT Authentication**.

## 🦅 The High-Level Picture

In a traditional Spring Security OAuth2 setup, the server uses an `HttpSession` (JSESSIONID) to remember the user after they log in with Google or GitHub.

**In this project, we disrupt that flow:**
1.  **Identity:** We use OAuth2 *only* to prove who the user is (Authentication).
2.  **Session:** We explicitly discard the server-side session immediately after login.
3.  **Token:** We issue a **JWT (JSON Web Token)** stored in a secure **HTTP-Only Cookie**.
4.  **Statelessness:** Every subsequent request is validated by checking that cookie, not by looking up a session in the server's memory.

---

## 🗺️ The Golden Path (How to Read This Code)

If you are trying to understand this project from scratch, do not read the files in alphabetical order. Follow this "Golden Path" to build your mental model layer by layer.

### 1. The Foundation
*   **Path:** `pom.xml`
*   **Why:** See the dependencies. Notice `spring-boot-starter-oauth2-client` (for the login flow) and `jjwt-api` (for generating tokens).

### 2. The Blueprint
*   **Path:** `src/main/resources/application.yaml`
*   **Why:** Understand the contract. We define OAuth2 client secrets here and, crucially, our **JWT configuration** (secret key, expiration, cookie name).

### 3. The Brain (Start Here for Logic)
*   **Path:** `src/main/java/com/example/social_login/config/SecurityConfig.java`
*   **What it does:** The central security configuration.
*   **Key Takeaways:**
    *   `sessionCreationPolicy(SessionCreationPolicy.STATELESS)`: Tells Spring "Don't depend on sessions."
    *   `.oauth2Login().successHandler(...)`: The critical hook where we hijack the standard flow.
    *   `.addFilterBefore(jwtAuthenticationFilter, ...)`: Injecting our custom validation logic.

### 4. The Handover (The Magic Moment)
*   **Path:** `src/main/java/com/example/social_login/security/oauth/OAuth2LoginSuccessHandler.java`
*   **What it does:** This runs *immediately* after Google/GitHub says "Yes, this user is valid."
*   **The Logic:**
    1.  Extract user email from the OAuth provider.
    2.  **Generate a JWT** for that user.
    3.  **Bake it into a Cookie.**
    4.  Redirect the user to the frontend.
    *   *Note:* This replaces the default Spring behavior of creating a JSESSIONID.

### 5. The Toolkit
*   **Path:** `src/main/java/com/example/social_login/security/jwt/JwtService.java`
    *   **Role:** The mechanic. It knows how to sign strings into JWTs and parse them back.
*   **Path:** `src/main/java/com/example/social_login/security/jwt/CookieUtils.java`
    *   **Role:** The courier. Creates the actual `ResponseCookie` object with strict security settings (`HttpOnly`, `Secure`, `SameSite=Lax`).

### 6. The Gatekeeper (Per-Request Check)
*   **Path:** `src/main/java/com/example/social_login/security/jwt/JwtAuthenticationFilter.java`
*   **What it does:** It runs on *every* single HTTP request.
*   **The Logic:**
    1.  Looks for the specific Cookie (e.g., `ACCESS_TOKEN`).
    2.  Validates the JWT inside it.
    3.  If valid, it manually populates the `SecurityContext`.
    4.  This "tricks" Spring into thinking the user is logged in for just this one request.

---

## 📂 Detailed File Breakdown

| File | Responsibility |
| :--- | :--- |
| **`SecurityConfig.java`** | Configures the `SecurityFilterChain`. Disables CSRF (because we use non-browser-accessible cookies), sets up CORS, and wires up the filter chain. |
| **`OAuth2LoginSuccessHandler.java`** | Extends `SimpleUrlAuthenticationSuccessHandler`. It's the bridge that converts an "OAuth2 User" into a "JWT Holder". |
| **`JwtService.java`** | Pure logic. Uses the `application.yaml` secret to sign and verify tokens. It doesn't know about HTTP or Spring Security; it just handles Strings. |
| **`CookieUtils.java`** | abstraction for Cookie attributes. Ensures we set `HttpOnly` (prevent XSS) and `SameSite` (mitigate CSRF) correctly. |
| **`JwtAuthenticationFilter.java`** | Extends `OncePerRequestFilter`. It intercepts traffic. If a valid JWT cookie is found, it sets `SecurityContextHolder.getContext().setAuthentication(...)`. |
| **`AuthController.java`** | Handles the `/logout` endpoint manually since we are managing our own cookies (we need to send a response that deletes the cookie). |
| **`UserController.java`** | A protected endpoint (`/api/me`) to test if the entire flow works. It returns the current authenticated user's details. |

## 🧠 "Pro Tip" for Enthusiasts

You might notice a `JSESSIONID` cookie appearing during the login phase even though we set the policy to `STATELESS`.

**Why?**
Spring Security's OAuth2 client needs to preserve "state" (like the `state` parameter and redirect URI) *during* the handshake with Google/GitHub to prevent CSRF attacks on the login flow itself. By default, it uses the `HttpSession` for this.

Once the `OAuth2LoginSuccessHandler` fires and we issue our JWT, that session is technically no longer needed for *authentication*, but it might linger unless explicitly cleared or replaced with a `CookieBasedAuthorizationRequestRepository` (an advanced optimization to go 100% stateless).
