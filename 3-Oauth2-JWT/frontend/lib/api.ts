const API_BASE_URL = 'http://localhost:8080';

/**
 * Fetch wrapper that always includes credentials (HttpOnly cookies)
 */
export async function apiFetch(endpoint: string, options: RequestInit = {}) {
    const url = `${API_BASE_URL}${endpoint}`;

    const response = await fetch(url, {
        ...options,
        credentials: 'include', // Always send HttpOnly cookies
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
    });

    return response;
}

/**
 * Fetch current user info from /api/me
 */
export async function getCurrentUser() {
    const response = await apiFetch('/api/me');

    if (!response.ok) {
        throw new Error('Not authenticated');
    }

    return response.json();
}

/**
 * Logout - calls POST /logout
 */
export async function logout() {
    const response = await apiFetch('/logout', {
        method: 'POST',
    });

    if (!response.ok) {
        throw new Error('Logout failed');
    }

    return response;
}

/**
 * Fetch public message from /api/public (no auth required)
 */
export async function getPublicMessage() {
    const response = await apiFetch('/api/public');

    if (!response.ok) {
        throw new Error('Failed to fetch public message');
    }

    return response.json();
}
