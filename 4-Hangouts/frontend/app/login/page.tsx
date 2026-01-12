'use client';

export default function LoginPage() {
    const handleGoogleLogin = () => {
        window.location.href = 'http://localhost:8080/oauth2/authorization/google';
    };

    const handleGitHubLogin = () => {
        window.location.href = 'http://localhost:8080/oauth2/authorization/github';
    };

    return (
        <div style={{ padding: '2rem', maxWidth: '400px', margin: '0 auto' }}>
            <h1>Login</h1>
            <p>Choose a provider to sign in:</p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '2rem' }}>
                <button
                    onClick={handleGoogleLogin}
                    style={{
                        padding: '12px 24px',
                        fontSize: '16px',
                        cursor: 'pointer',
                        border: '1px solid #ddd',
                        borderRadius: '4px',
                        backgroundColor: '#fff',
                    }}
                >
                    Login with Google
                </button>

                <button
                    onClick={handleGitHubLogin}
                    style={{
                        padding: '12px 24px',
                        fontSize: '16px',
                        cursor: 'pointer',
                        border: '1px solid #ddd',
                        borderRadius: '4px',
                        backgroundColor: '#fff',
                    }}
                >
                    Login with GitHub
                </button>
            </div>
        </div>
    );
}
