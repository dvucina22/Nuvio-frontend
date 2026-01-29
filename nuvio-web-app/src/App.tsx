import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/auth/LoginPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import ProtectedRoute from './components/shared/ProtectedRoute';
import { useAuth } from './hooks/useAuth';
import Navbar from './components/navbar/Navbar';
import UsersPage from './pages/users/UsersPage';

const RootRedirect: React.FC = () => {
  const { isAuthenticated } = useAuth();
  return <Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />;
};

const App: React.FC = () => {
  return (
    <div 
      className="min-h-screen bg-cover bg-center bg-fixed flex justify-center w-full"
      style={{ backgroundImage: 'url(/bg_light.jpg)' }}
    >
      <div className="absolute inset-0 bg-black/20 -z-10"></div>
      
      <BrowserRouter>
          <Routes>
            <Route path="/" element={<RootRedirect />} />
            <Route path="/login" element={<LoginPage />} />
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <Navbar />
                  <DashboardPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/users"
              element={
                <ProtectedRoute>
                  <Navbar />
                  <UsersPage />
                </ProtectedRoute>
              }
            />
          </Routes>
      </BrowserRouter>
    </div>
  );
};

export default App;