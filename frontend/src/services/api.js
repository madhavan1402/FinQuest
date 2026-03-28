// src/services/api.js
// Single Axios instance used by all pages and components.
// baseURL is '/api' — Vite proxies this to http://localhost:8080 during dev,
// so no CORS issues and no hardcoded port in every file.
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
});

export default api;
