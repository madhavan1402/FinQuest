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

// Attach the JWT access token to every request.
// After login the AuthContext stores the full AuthResponse (which includes
// accessToken) under the localStorage key 'fq_user'. Read it back here and
// set the Authorization: Bearer header so protected endpoints (which now
// require JWT authentication) accept the request.
const setAuthHeader = (headers, token) => {
  if (!headers) return;
  if (typeof headers.set === 'function') {
    headers.set('Authorization', `Bearer ${token}`);
  } else {
    headers.Authorization = `Bearer ${token}`;
    headers['Authorization'] = `Bearer ${token}`;
  }
};

api.interceptors.request.use(
  (config) => {
    try {
      const stored = localStorage.getItem('fq_user');
      const token = stored ? JSON.parse(stored).accessToken : null;
      if (token) {
        config.headers = config.headers ?? {};
        setAuthHeader(config.headers, token);
      }
    } catch {
      // ignore malformed stored session
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ── 401/403 handling: refresh the expired access token using the EXISTING
// backend /api/auth/refresh mechanism, then retry the original request once.
// This is NOT a second auth system — it simply rotates the stored JWT pair
// using the backend's own refresh-token rotation endpoint. Requests to the
// auth endpoints themselves are never retried (avoids loops).
let isRefreshing = false;
let pendingQueue = [];

const onRefreshed = (newToken) => {
  pendingQueue.forEach((cb) => cb(newToken));
  pendingQueue = [];
};

const refreshStoredTokens = async () => {
  const stored = JSON.parse(localStorage.getItem('fq_user') || 'null');
  if (!stored?.refreshToken) return null;
  const { data } = await axios.post('/api/auth/refresh', {
    refreshToken: stored.refreshToken,
  });
  return data; // full AuthResponse with new accessToken + rotated refreshToken
};

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    // Attempt refresh on 401/403 for authenticated, non-auth endpoints,
    // and never more than once per request.
    const isAuthError = error.response?.status === 401 || error.response?.status === 403;
    if (
      isAuthError &&
      original &&
      !original._retry &&
      !original.url.includes('/auth/login') &&
      !original.url.includes('/auth/refresh')
    ) {
      original._retry = true;
      try {
        if (!isRefreshing) {
          // This request is the leader — it performs the refresh and then
          // retries itself with the fresh token.
          isRefreshing = true;
          try {
            const refreshed = await refreshStoredTokens();
            if (!refreshed?.accessToken) {
              pendingQueue = [];
              return Promise.reject(error);
            }
            // Persist the rotated token pair (same storage shape as login).
            const updated = { ...JSON.parse(localStorage.getItem('fq_user') || '{}'), ...refreshed };
            localStorage.setItem('fq_user', JSON.stringify(updated));
            onRefreshed(refreshed.accessToken);
            setAuthHeader(original.headers, refreshed.accessToken);
            return api(original);
          } finally {
            isRefreshing = false;
          }
        }

        // Another request is already refreshing — wait for its token.
        const newToken = await new Promise((resolve) => {
          pendingQueue.push(resolve);
        });
        setAuthHeader(original.headers, newToken);
        return api(original);
      } catch (refreshErr) {
        // Refresh failed (expired refresh token / revoked). Clear the session
        // so the UI redirects to login. Do NOT silently clobber a valid JWT.
        localStorage.removeItem('fq_user');
        pendingQueue = [];
        window.dispatchEvent(new Event('fq_session_expired'));
        return Promise.reject(refreshErr);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
