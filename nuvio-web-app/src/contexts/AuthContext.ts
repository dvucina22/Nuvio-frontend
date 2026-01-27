import React, { createContext, useState, useEffect, ReactNode } from 'react';
import api from '../services/api';
import storage from '../utils/storage';
import { PasswordHasher } from '../utils/passwordHasher';
import { User, AuthContextType, ApiResponse } from '../types';

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

  const login = async (email: string, password: string): Promise<ApiResponse> => {
    try {
      setError(null);
      setLoading(true);

      const hashedPassword = await PasswordHasher.transformPassword(password, email);

      const result = await api.login({ email, password: hashedPassword });

      if (!result.success || !result.data) {
        setError(result.error || 'Login failed');
        return result;
      }

      storage.setAuthToken(result.data.token);
      storage.setUserData(result.data.user);
      
      setUser(result.data.user);
      return { success: true };
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Login failed';
      setError(errorMessage);
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  };

  const logout = async (): Promise<void> => {
    try {
      setLoading(true);
      storage.clearAuth();
      setUser(null);
      setError(null);
    } catch (err) {
      console.error('Error logging out:', err);
      storage.clearAuth();
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const updateUser = async (userData: Partial<User>): Promise<ApiResponse> => {
    try {
      const result = await api.updateUserProfile(userData);
      
      if (result.success && result.data) {
        storage.setUserData(result.data.user);
        setUser(result.data.user);
        return { success: true };
      }
      
      return result;
    } catch (err) {
      return { 
        success: false, 
        error: err instanceof Error ? err.message : 'Update failed' 
      };
    }
  };

  const value: AuthContextType = {
    user,
    loading,
    error,
    login,
    logout,
    updateUser,
    isAuthenticated: !!user,
  };

  return React.createElement(
    AuthContext.Provider,
    { value },
    children
  );
};

export default AuthProvider;