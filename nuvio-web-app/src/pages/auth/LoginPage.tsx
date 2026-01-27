import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import LoginForm from '../../components/auth/LoginForm';
import { ApiResponse } from '../../types';

const LoginPage: React.FC = () => {
  const { login, loading } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (email: string, password: string): Promise<ApiResponse> => {
    const result = await login(email, password);
    
    if (result.success) {
      navigate('/dashboard');
    }
    
    return result;
  };

  const handleForgotPassword = (): void => {
    alert('Forgot password feature coming soon');
  };

  return (
    <div className="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full bg-white items-center flex flex-col rounded-xl shadow-lg p-8 sm:p-10">
        <div className="min-h-[100px] min-w-[245px] bg-no-repeat bg-contain" style={{
          backgroundImage: "url(/logo.png)"
        }}/>
        <div className="text-center mb-2 mt-4 w-full">
          <p className="text-base text-gray-600">
            Sign in to continue
          </p>
        </div>

        <LoginForm onSubmit={handleLogin} loading={loading} />

        <div className="mt-6 text-center">
          <button
            type="button"
            onClick={handleForgotPassword}
            className="text-sm text-blue-600 hover:text-blue-700 hover:underline focus:outline-none"
          >
            Forgot Password?
          </button>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;