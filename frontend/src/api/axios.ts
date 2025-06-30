import axios from 'axios';

// Use hardcoded URL for now since .env might not be working
const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Important: sends cookies with requests
});

console.log('Axios - configured with baseURL:', API_URL, 'and withCredentials: true');

// Add a request interceptor for logging (no more Authorization headers needed)
api.interceptors.request.use(
  (config) => {
    console.log('Axios request interceptor - making request to:', config.url);
    console.log('Axios request interceptor - withCredentials:', config.withCredentials);
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
    
    // Handle 401 errors (authentication failed)
    if (error.response?.status === 401) {
      console.log('Axios response interceptor - 401 error, authentication failed');
      // Redirect to login page
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api; 