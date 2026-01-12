# Postman Testing Guide for Social Login Backend

This guide details how to test the endpoints of the Social Login Spring Boot application using Postman.

## Prerequisites

1.  **Application Running:** Ensure the Spring Boot application is running locally (default: `http://localhost:8080`).
2.  **OAuth2 Configured:** You must have valid `client-id` and `client-secret` for GitHub or Google in your `src/main/resources/application.yaml`.
3.  **Postman Installed:** Download and install [Postman](https://www.postman.com/downloads/).

## Base Configuration

It is recommended to set up a **Environment** in Postman:

1.  Click **Environments** -> **+** (Create new).
2.  Name it `Social Login Local`.
3.  Add a variable:
    *   **Variable:** `baseUrl`
    *   **Initial Value:** `http://localhost:8080`
    *   **Current Value:** `http://localhost:8080`
4.  Select this environment from the top-right dropdown in Postman.

---

## 1. Public Endpoints

These endpoints do not require authentication.

### Get Public Message
*   **Method:** `GET`
*   **URL:** `{{baseUrl}}/api/public`
*   **Expected Result:** `200 OK`
    ```json
    {
        "message": "This is a public endpoint"
    }
    ```

---

## 2. Authentication (OAuth2 Flow)

Since this application uses **OAuth2** with **HttpOnly Cookies**, you cannot perform the full login flow strictly inside Postman (because Postman cannot easily handle the external GitHub/Google login pages and redirects).

**The Workflow:**
1.  Perform the login in your **Web Browser**.
2.  Extract the `ACCESS_TOKEN` cookie.
3.  Use that cookie in Postman.

### Step 2.1: Login via Browser
1.  Open your browser (Chrome, Firefox, etc.).
2.  Navigate to one of the following URLs to initiate login:
    *   GitHub: `http://localhost:8080/oauth2/authorization/github`
    *   Google: `http://localhost:8080/oauth2/authorization/google`
3.  Complete the login process with the provider.
4.  You will be redirected to the frontend URL (e.g., `localhost:3000`) upon success.

### Step 2.2: Extract the Cookie
1.  In the browser tab where you successfully logged in, open **Developer Tools** (F12).
2.  Go to the **Application** tab (Chrome/Edge) or **Storage** tab (Firefox).
3.  Expand **Cookies** and select the entry for `http://localhost:8080` (or `localhost`).
4.  Find the cookie named `ACCESS_TOKEN`.
5.  **Copy the Value** of this cookie.

---

## 3. Protected Endpoints

To test these, you must include the cookie from Step 2.

### Get Current User Profile
*   **Method:** `GET`
*   **URL:** `{{baseUrl}}/api/me`
*   **Headers:**
    *   Key: `Cookie`
    *   Value: `ACCESS_TOKEN=<paste_token_value_here>`
    *   *(Alternatively, you can add the cookie to Postman's "Cookies" manager for the domain `localhost`)*
*   **Expected Result:** `200 OK`
    ```json
    {
        "email": "your-email@example.com"
    }
    ```
*   **Error Case:** If the token is missing or expired, you will receive `403 Forbidden`.

---

## 4. Logout

### Logout User
*   **Method:** `POST`
*   **URL:** `{{baseUrl}}/logout`
*   **Headers:**
    *   (Optional) `Cookie`: `ACCESS_TOKEN=<your_token>`

### FAQ for Logout
**1. Does it need the cookie?**
*   **Technically, No:** The `/logout` endpoint is configured as `permitAll()` in `SecurityConfig`. You can send this POST request without any headers, and it will still return a success response.
*   **Functionally:** It works by ignoring what you send and simply responding with a "clear cookie" command.

**2. Does it need a CSRF Token?**
*   **No:** CSRF protection is explicitly disabled (`.csrf(csrf -> csrf.disable())`) in this project's configuration because it relies on stateless JWTs and SameSite cookie policies. You do not need to include any `X-XSRF-TOKEN` or `_csrf` fields.

**3. How it works (The Mechanics):**
*   Since the architecture is **Stateless**, the server does not track active sessions. It cannot "delete" a session on the backend.
*   Instead, the server sends a response header: `Set-Cookie: ACCESS_TOKEN=; Max-Age=0; Path=/; HttpOnly`.
*   This instructs your browser (or Postman) to strictly **delete/expire** the cookie immediately.

**Expected Result:** `200 OK`
*   **Body:**
    ```json
    {
        "message": "Logged out successfully",
        "status": "success"
    }
    ```
*   **Response Headers:** Look for `Set-Cookie` with `Max-Age=0`.