'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { getCurrentUser, logout, getPublicMessage } from '@/lib/api';

interface UserInfo {
    email?: string;
    username?: string;
    name?: string;
}

interface PublicMessage {
    message?: string;
}

export default function DashboardPage() {
    const [user, setUser] = useState<UserInfo | null>(null);
    const [publicData, setPublicData] = useState<PublicMessage | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const router = useRouter();

    useEffect(() => {
        async function fetchData() {
            try {
                // Fetch public message first (no auth needed)
                const publicMsg = await getPublicMessage();
                setPublicData(publicMsg);

                // Then fetch authenticated user
                const userData = await getCurrentUser();
                setUser(userData);
                setError(null);
            } catch (err) {
                setError('Not authenticated');
                // Redirect to login if not authenticated
                router.push('/login');
            } finally {
                setLoading(false);
            }
        }

        fetchData();
    }, [router]);

    const handleLogout = async () => {
        try {
            await logout();
            router.push('/login');
        } catch (err) {
            setError('Logout failed');
        }
    };

    if (loading) {
        return (
            <div style={{ padding: '2rem' }}>
                <p>Loading...</p>
            </div>
        );
    }

    if (error || !user) {
        return (
            <div style={{ padding: '2rem' }}>
                <p>Error: {error}</p>
            </div>
        );
    }

    return (
        <div style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto' }}>
            <h1>Dashboard</h1>

            {/* Public Endpoint Test */}
            <div style={{ marginTop: '2rem', padding: '1rem', border: '1px solid #4caf50', borderRadius: '4px', backgroundColor: '#e8f5e9' }}>
                <h2>✅ Public Endpoint Test</h2>
                <p><strong>GET /api/public:</strong> {publicData?.message || 'N/A'}</p>
            </div>

            {/* Authenticated User Info */}
            <div style={{ marginTop: '1rem', padding: '1rem', border: '1px solid #2196f3', borderRadius: '4px', backgroundColor: '#e3f2fd' }}>
                <h2>🔐 Authenticated User</h2>
                <div style={{ marginTop: '0.5rem' }}>
                    {user.email && <p><strong>Email:</strong> {user.email}</p>}
                    {user.username && <p><strong>Username:</strong> {user.username}</p>}
                    {user.name && <p><strong>Name:</strong> {user.name}</p>}
                </div>
            </div>

            <button
                onClick={handleLogout}
                style={{
                    marginTop: '2rem',
                    padding: '12px 24px',
                    fontSize: '16px',
                    cursor: 'pointer',
                    border: '1px solid #f44336',
                    borderRadius: '4px',
                    backgroundColor: '#ffebee',
                    color: '#c62828',
                }}
            >
                Logout
            </button>
        </div>
    );
}
