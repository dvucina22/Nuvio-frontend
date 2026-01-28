import React, { createContext, useState, useEffect, ReactNode } from 'react';
import api from '../services/api';
import storage from '../utils/storage';
import { PasswordHasher } from '../utils/passwordHasher';
import { User, AuthContextType, ApiResponse } from '../types';
import { LoginResponse } from '@/types/LoginResponse';

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = (): void => {
    try {
      const token = storage.getAuthToken();
      const userData = storage.getUserData<User>();
      
      if (token && userData) {
        setUser(userData);
      }
    } catch (err) {
      console.error('Error checking auth status:', err);
    } finally {
      setLoading(false);
    }
  };

  const login = async (email: string, password: string): Promise<LoginResponse> => {
    try {
      setError(null);
      setLoading(true);

      const hashedPassword = await PasswordHasher.transformPassword(password, email);

      const result = await api.login({ email, password: hashedPassword });

      if (!result.data?.token
      ) {
        setError('Login failed');
        return result.data || { token: '' };
      }

      storage.setAuthToken(result.token);
      
      return { success: true };
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Login failed';
      setError(errorMessage);
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  };


  const value: AuthContextType = {
    user,
    loading,
    error,
    login,
    isAuthenticated: !!user,
  };

  return React.createElement(
    AuthContext.Provider,
    { value },
    children
  );
};

export default AuthProvider;