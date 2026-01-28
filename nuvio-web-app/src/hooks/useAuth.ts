import { useState, useEffect } from 'react';

interface UseAuthReturn {
  loading: boolean;
  isAuthenticated: boolean;
}

export const useAuth = (): UseAuthReturn => {
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const token = localStorage.getItem('token');
        
        if (!token) {
          setIsAuthenticated(false);
          return;
        }

        setIsAuthenticated(true);
        
      } catch (error) {
        console.error('Auth check failed:', error);
        setIsAuthenticated(false);
        localStorage.removeItem('token');
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  return { loading, isAuthenticated };
};