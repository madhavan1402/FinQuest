// src/App.jsx
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar        from './components/Navbar';
import Login         from './pages/Login';
import Register      from './pages/Register';
import Dashboard     from './pages/Dashboard';
import Levels        from './pages/Levels';
import LearningPath  from './pages/LearningPath';
import Quiz          from './pages/Quiz';
import Simulate      from './pages/Simulate';
import Leaderboard   from './pages/Leaderboard';
import Rewards       from './pages/Rewards';

// Page transition wrapper — fade + slide up on route change
function PageTransition({ children }) {
  const { pathname } = useLocation();
  return (
    <div
      key={pathname}
      style={{ animation: 'fadeSlideUp 0.25s ease both' }}
    >
      {children}
    </div>
  );
}

// Redirects to /login if not logged in, otherwise renders Navbar + page
function ProtectedRoute({ children }) {
  const { user } = useAuth();
  if (!user || !user.accessToken) return <Navigate to="/login" replace />;
  return (
    <>
      <Navbar />
      <main>
        <PageTransition>{children}</PageTransition>
      </main>
    </>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public */}
          <Route path="/login"    element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Protected */}
          <Route path="/dashboard"    element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
          <Route path="/levels"       element={<ProtectedRoute><Levels /></ProtectedRoute>} />
          <Route path="/learning-path" element={<ProtectedRoute><LearningPath /></ProtectedRoute>} />
          <Route path="/quiz"         element={<ProtectedRoute><Quiz /></ProtectedRoute>} />
          <Route path="/simulate"     element={<ProtectedRoute><Simulate /></ProtectedRoute>} />
          <Route path="/leaderboard"  element={<ProtectedRoute><Leaderboard /></ProtectedRoute>} />
          <Route path="/rewards"      element={<ProtectedRoute><Rewards /></ProtectedRoute>} />

          {/* Redirects */}
          <Route path="/"  element={<Navigate to="/dashboard" replace />} />
          <Route path="*"  element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
