// src/api/endpoints.js
// All backend API calls in one place.
// Every function returns an Axios promise — use .then() or await in components.
import api from '../services/api';

// ── Auth ──────────────────────────────────────────────────────────────────────
export const register = (data)   => api.post('/auth/register', data);
export const login    = (data)   => api.post('/auth/login',    data);

// ── User profile ──────────────────────────────────────────────────────────────
// GET /api/user/profile?userId=1
// Returns fresh XP, level, financialScore from the DB
export const getUserProfile = (userId) => api.get('/user/profile', { params: { userId } });

// ── Game / Levels ─────────────────────────────────────────────────────────────
export const getLevels       = ()       => api.get('/levels');
export const completeLevel   = (userId) => api.post(`/complete-level?userId=${userId}`);
export const getAchievements = (userId) => api.get('/achievements', { params: { userId } });

// ── Quiz ──────────────────────────────────────────────────────────────────────
export const getQuizQuestions = (level)   => api.get(`/quiz/${level}`);
export const submitQuiz       = (payload) => api.post('/quiz/submit', payload);

// ── Simulation ────────────────────────────────────────────────────────────────
export const runBudget = (payload) => api.post('/simulation/budget', payload);
export const runStock  = (payload) => api.post('/simulation/stock',  payload);
export const runTax    = (payload) => api.post('/simulation/tax',    payload);

// ── AI ────────────────────────────────────────────────────────────────────────
export const getAiPrediction = (payload) => api.post('/ai/predict', payload);
