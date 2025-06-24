interface LoginResponse {
  token: string;
  user: {
    role: 'customer' | 'employee';
    isApproved: boolean;
  };
}

export const apiService = {
  async login({ email, password }: { email: string; password: string }): Promise<LoginResponse> {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
      throw new Error('Login failed');
    }

    return response.json();
  }
}; 