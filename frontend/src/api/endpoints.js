// src/api/endpoints.js
import api from '../services/api';

// ── Auth ──────────────────────────────────────────────────────────────────────
export const register = (data)   => api.post('/auth/register', data);
export const login    = (data)   => api.post('/auth/login',    data);
export const refreshAccessToken = (data) => api.post('/auth/refresh', data);

// ── User profile ──────────────────────────────────────────────────────────────
// Phase 4: the backend derives the current user from the JWT principal, so we
// must NOT send a userId. The Axios interceptor attaches the Bearer token.
export const getUserProfile = () => api.get('/profile');

// ── Game / Levels ─────────────────────────────────────────────────────────────
export const getLevels       = ()       => api.get('/levels');
export const completeLevel   = (userId) => api.post(`/complete-level?userId=${userId}`);
export const getAchievements = (userId) => api.get('/achievements', { params: { userId } });

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
export const getQuizQuestions = (level)   => api.get(`/quiz/${level}`);
export const submitQuiz       = (payload) => api.post('/quiz/submit', payload);

// ── Simulation ────────────────────────────────────────────────────────────────
export const runBudget = (payload) => api.post('/simulation/budget', payload);
export const runStock  = (payload) => api.post('/simulation/stock',  payload);
export const runTax    = (payload) => api.post('/simulation/tax',    payload);

// ── AI ────────────────────────────────────────────────────────────────────────
// ── Gamification ──────────────────────────────────────────────────────────────
export const getGamificationSummary = () => api.get('/gamification/summary');
export const getGamificationAchievements = () => api.get('/gamification/achievements');
export const getGamificationBadges = () => api.get('/gamification/badges');
export const getGamificationStreak = () => api.get('/gamification/streak');
export const getGamificationProgress = () => api.get('/gamification/progress');
export const claimDailyReward = () => api.post('/gamification/daily-reward');
