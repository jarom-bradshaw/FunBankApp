import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, isInitialized } = useAuth();

  console.log('ProtectedRoute - checking auth state, isInitialized:', isInitialized, 'isAuthenticated:', isAuthenticated());

  // Wait for authentication to be initialized
  if (!isInitialized) {
    console.log('ProtectedRoute - waiting for auth initialization');
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
  }

  if (!isAuthenticated()) {
    console.log('ProtectedRoute - redirecting to login (not authenticated)');
    return <Navigate to="/login" replace />;
  }

  console.log('ProtectedRoute - rendering children (authenticated)');
  return children;
};

export default ProtectedRoute; 