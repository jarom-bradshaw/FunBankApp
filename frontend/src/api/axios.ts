import axios from 'axios';

// Use hardcoded URL for now since .env might not be working
const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

console.log('Axios - configured with baseURL:', API_URL);

// Add a request interceptor to include the JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    console.log('Axios request interceptor - token:', token ? 'exists' : 'null', 'for URL:', config.url);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('Axios request interceptor - added Authorization header');
    } else {
      console.log('Axios request interceptor - no token available');
    }
    return config;
  },
  (error) => {
    console.log('Axios request interceptor - error:', error);
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle 401 errors
api.interceptors.response.use(
  (response) => {
    console.log('Axios response interceptor - success:', response.status, 'for URL:', response.config.url);
    return response;
  },
  (error) => {
    console.log('Axios response interceptor - error:', error.response?.status, error.response?.data, 'for URL:', error.config?.url);
    
    // Only handle 401 errors for API calls, not for login/register
    if (error.response?.status === 401 && 
        !error.config.url?.includes('/users/login') && 
        !error.config.url?.includes('/users/register')) {
      console.log('Axios response interceptor - 401 error on protected endpoint, checking if token exists');
      
      const currentToken = localStorage.getItem('token');
      if (currentToken) {
        console.log('Axios response interceptor - token exists but request failed, likely expired or invalid');
        console.log('Axios response interceptor - NOT clearing token automatically - letting component handle it');
        // Don't clear token or redirect automatically - let the component handle it
      } else {
        console.log('Axios response interceptor - no token found, user already logged out');
      }
    }
    return Promise.reject(error);
  }
);

export default api; 