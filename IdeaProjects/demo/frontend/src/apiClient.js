import axios from 'axios';

const createApiClient = (token) => {
  const apiClient = axios.create({
    baseURL: 'http://localhost:5051/api',
  });

  // Set up an interceptor to add the Authorization token before each request
  apiClient.interceptors.request.use(config => {
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  }, error => {
    return Promise.reject(error);
  });

  return apiClient;
};

export default createApiClient;