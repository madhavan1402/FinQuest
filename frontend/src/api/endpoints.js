// src/api/endpoints.js
<<<<<<< HEAD
=======
// All backend API calls in one place.
// Every function returns an Axios promise — use .then() or await in components.
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
import api from '../services/api';

// ── Auth ──────────────────────────────────────────────────────────────────────
export const register = (data)   => api.post('/auth/register', data);
export const login    = (data)   => api.post('/auth/login',    data);
<<<<<<< HEAD
export const refreshAccessToken = (data) => api.post('/auth/refresh', data);

// ── User profile ──────────────────────────────────────────────────────────────
// Phase 4: the backend derives the current user from the JWT principal, so we
// must NOT send a userId. The Axios interceptor attaches the Bearer token.
export const getUserProfile = () => api.get('/profile');
=======

// ── User profile ──────────────────────────────────────────────────────────────
// GET /api/user/profile?userId=1
// Returns fresh XP, level, financialScore from the DB
export const getUserProfile = (userId) => api.get('/user/profile', { params: { userId } });
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577

// ── Game / Levels ─────────────────────────────────────────────────────────────
export const getLevels       = ()       => api.get('/levels');
export const completeLevel   = (userId) => api.post(`/complete-level?userId=${userId}`);
export const getAchievements = (userId) => api.get('/achievements', { params: { userId } });

<<<<<<< HEAD
// ── Learning Path 2.0 ─────────────────────────────────────────────────────────
// These use the authenticated JWT principal (no userId param needed).
export const getLearningPath       = ()       => api.get('/learning-path');
export const getLearningPathProgress = ()    => api.get('/learning-path/progress');
export const getLevelDetail        = (levelNumber) => api.get(`/learning-path/levels/${levelNumber}`);
export const getLevelQuiz          = (levelNumber) => api.get(`/learning-path/levels/${levelNumber}/quiz`);
export const submitLevelQuiz       = (levelNumber, answers) =>
  api.post(`/learning-path/levels/${levelNumber}/submit`, { answers });
export const resetLearningPath     = ()       => api.post('/learning-path/reset');

// ── Quiz (secure) ─────────────────────────────────────────────────────────────
=======
// ── Quiz ──────────────────────────────────────────────────────────────────────
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
export const getQuizQuestions = (level)   => api.get(`/quiz/${level}`);
export const submitQuiz       = (payload) => api.post('/quiz/submit', payload);

// ── Simulation ────────────────────────────────────────────────────────────────
export const runBudget = (payload) => api.post('/simulation/budget', payload);
export const runStock  = (payload) => api.post('/simulation/stock',  payload);
export const runTax    = (payload) => api.post('/simulation/tax',    payload);

// ── AI ────────────────────────────────────────────────────────────────────────
<<<<<<< HEAD
// ── Gamification ──────────────────────────────────────────────────────────────
export const getGamificationSummary = () => api.get('/gamification/summary');
export const getGamificationAchievements = () => api.get('/gamification/achievements');
export const getGamificationBadges = () => api.get('/gamification/badges');
export const getGamificationStreak = () => api.get('/gamification/streak');
export const getGamificationProgress = () => api.get('/gamification/progress');
export const claimDailyReward = () => api.post('/gamification/daily-reward');
=======
export const getAiPrediction = (payload) => api.post('/ai/predict', payload);
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
