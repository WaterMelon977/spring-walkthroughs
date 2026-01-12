# OAuth2 JWT Authentication - Next.js Frontend

This is a minimal Next.js frontend for testing the Spring Boot OAuth2 JWT authentication backend.

## Features

- **Login Page** (`/login`): Buttons to initiate OAuth2 flow with Google and GitHub
- **Protected Dashboard** (`/dashboard`): Displays user info from backend JWT
- **Logout**: Clears HttpOnly cookie and redirects to login
- **Cookie-based Auth**: Uses HttpOnly cookies, no tokens in localStorage

## Prerequisites

- Spring Boot backend running on `http://localhost:8080`
- Backend CORS configured to allow credentials from frontend origin
- Node.js 18+ installed

## Installation

```bash
npm install
```

## Running the Application

```bash
npm run dev
```

The frontend will be available at `http://localhost:3000`

## Usage Flow

1. Navigate to `http://localhost:3000` (automatically redirects to `/dashboard`)
2. Since you're not authenticated, you'll be redirected to `/login`
3. Click "Login with Google" or "Login with GitHub"
4. Browser navigates to Spring Boot OAuth2 endpoint
5. After successful authentication, backend:
   - Sets HttpOnly cookie with JWT
   - Redirects to `http://localhost:3000/` 
6. Frontend redirects to `/dashboard`
7. Dashboard fetches user info from `/api/me` using the cookie
8. Click "Logout" to clear session and return to login

## Project Structure

```
frontend/
├── app/
│   ├── login/
│   │   └── page.tsx          # Login page with OAuth2 buttons
│   ├── dashboard/
│   │   └── page.tsx          # Protected dashboard
│   ├── page.tsx              # Root page (redirects to dashboard)
│   └── layout.tsx            # Root layout
├── lib/
│   └── api.ts                # API utilities with credentials support
└── package.json
```

## API Endpoints Used

- `GET /api/me` - Fetch current user info (requires auth)
- `POST /logout` - Logout and clear cookie
- `/oauth2/authorization/google` - Initiate Google OAuth2
- `/oauth2/authorization/github` - Initiate GitHub OAuth2

## Security Notes

- JWT is stored in HttpOnly cookie (not accessible via JavaScript)
- All API requests use `credentials: 'include'`
- No token parsing or storage on frontend
- Frontend relies entirely on backend for authentication state
