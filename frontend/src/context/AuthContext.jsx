import React, { createContext, useState, useContext } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState(null);

  const login = (userInfo) => {
    console.log('AuthContext - login called for user:', userInfo.username);
    setIsLoggedIn(true);
    setUser(userInfo);
    console.log('AuthContext - login completed, user logged in');
  };

  const logout = () => {
    console.log('AuthContext - logout called');
    setIsLoggedIn(false);
    setUser(null);
  };

  const isAuthenticated = () => {
    return isLoggedIn;
  };

  return (
    <AuthContext.Provider value={{ 
      isLoggedIn, 
      user, 
      login, 
      logout, 
      isAuthenticated
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext); 