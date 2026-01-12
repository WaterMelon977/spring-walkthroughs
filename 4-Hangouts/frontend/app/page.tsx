'use client';

import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

export default function HomePage() {
  const router = useRouter();

  useEffect(() => {
    // Redirect to dashboard on root
    router.push('/dashboard');
  }, [router]);

  return (
    <div style={{ padding: '2rem' }}>
      <p>Redirecting...</p>
    </div>
  );
}
