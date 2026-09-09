// src/context/AuthContext.jsx
// Global state for the logged-in user and the last quiz result.
// Persisted to localStorage so both survive a page refresh.
import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {

  // ── User session ────────────────────────────────────────────────────────────
  const [user, setUser] = useState(() => {
    try {
      const stored = localStorage.getItem('fq_user');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });

  // Normalise the user object so user.userId is always set,
  // and ensure accessToken and refreshToken are preserved across partial updates.
  const saveUser = (userData) => {
    if (!userData) {
      setUser(null);
      localStorage.removeItem('fq_user');
      return;
    }
    let current = {};
    try {
      current = JSON.parse(localStorage.getItem('fq_user') || '{}');
    } catch {
      current = {};
    }
    const normalised = {
      ...current,
      ...userData,
      userId: userData.userId ?? userData.id ?? current.userId ?? null,
      accessToken: userData.accessToken ?? current.accessToken ?? null,
      refreshToken: userData.refreshToken ?? current.refreshToken ?? null,
    };
    setUser(normalised);
    localStorage.setItem('fq_user', JSON.stringify(normalised));
  };

  const logout = () => {
    setUser(null);
    setLastQuizResult(null);
    localStorage.removeItem('fq_user');
    localStorage.removeItem('fq_last_quiz');
  };

  useEffect(() => {
    const handleExpired = () => {
      logout();
    };
    window.addEventListener('fq_session_expired', handleExpired);
    return () => window.removeEventListener('fq_session_expired', handleExpired);
  }, []);

  // ── Last quiz result ────────────────────────────────────────────────────────
  // Written by Quiz.jsx after every submission.
  // Dashboard.jsx watches this to show a toast and re-fetch the profile.
  const [lastQuizResult, setLastQuizResult] = useState(() => {
    try {
      const stored = localStorage.getItem('fq_last_quiz');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });

  const saveQuizResult = (result) => {
    setLastQuizResult(result);
    localStorage.setItem('fq_last_quiz', JSON.stringify(result));
  };

  return (
    <AuthContext.Provider value={{ user, saveUser, logout, lastQuizResult, saveQuizResult }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
