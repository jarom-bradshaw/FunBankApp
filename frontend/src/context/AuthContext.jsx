import React, { createContext, useState, useEffect, useContext } from 'react';

const AuthContext = createContext();

// Helper function to log to both console and localStorage
const persistentLog = (message) => {
  console.log(message);
  const timestamp = new Date().toISOString();
  const logEntry = `${timestamp}: ${message}`;
  
  // Get existing logs
  const existingLogs = localStorage.getItem('auth_logs') || '';
  const newLogs = existingLogs + '\n' + logEntry;
  
  // Keep only last 50 log entries to avoid localStorage getting too large
  const logLines = newLogs.split('\n').slice(-50);
  localStorage.setItem('auth_logs', logLines.join('\n'));
};

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => {
    const storedToken = localStorage.getItem('token');
    persistentLog(`AuthContext - initializing with token: ${storedToken ? 'exists' : 'null'}`);
    return storedToken;
  });
  const [user, setUser] = useState(null);
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    persistentLog(`AuthContext - token changed: ${token ? 'exists' : 'null'}`);
    if (token) {
      localStorage.setItem('token', token);
      persistentLog('AuthContext - token saved to localStorage');
    } else {
      localStorage.removeItem('token');
      persistentLog('AuthContext - token removed from localStorage');
    }
    setIsInitialized(true);
  }, [token]);

  const login = (jwt, userInfo) => {
    persistentLog(`AuthContext - login called with token: ${jwt.substring(0, 20)}...`);
    setToken(jwt);
    setUser(userInfo);
    persistentLog('AuthContext - login completed, token set');
  };

  const logout = () => {
    persistentLog('AuthContext - logout called');
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
  };

  const clearAuth = () => {
    persistentLog('AuthContext - clearAuth called');
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
  };

  const isAuthenticated = () => {
    return token !== null && token !== undefined;
  };

  const getAuthLogs = () => {
    return localStorage.getItem('auth_logs') || 'No logs found';
  };

  const clearAuthLogs = () => {
    localStorage.removeItem('auth_logs');
  };

  return (
    <AuthContext.Provider value={{ 
      token, 
      user, 
      login, 
      logout, 
      clearAuth, 
      isAuthenticated,
      isInitialized,
      getAuthLogs,
      clearAuthLogs
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext); 