// Axios instance shared across all API calls.
// baseURL is empty because Vite proxies /api → http://localhost:8080
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

export default api;
