import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();

  console.log('ProtectedRoute - checking auth state, isAuthenticated:', isAuthenticated());

  if (!isAuthenticated()) {
    console.log('ProtectedRoute - redirecting to login (not authenticated)');
    return <Navigate to="/login" replace />;
  }

  console.log('ProtectedRoute - rendering children (authenticated)');
  return children;
};

export default ProtectedRoute; 