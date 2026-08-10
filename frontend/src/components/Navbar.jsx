<<<<<<< HEAD
// Navbar.jsx — Premium FinQuest navigation
import { useState, useEffect } from 'react';
=======
// Navbar.jsx
// Sticky top navigation bar shown on all authenticated pages.
// Uses React Router <Link> — no page reloads.

>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NAV_LINKS = [
<<<<<<< HEAD
  { to: '/dashboard',    label: 'Dashboard',    icon: '🏠' },
  { to: '/levels',       label: 'Levels',        icon: '🗺️' },
  { to: '/learning-path',label: 'Learning',      icon: '🧭' },
  { to: '/simulate',     label: 'Simulate',      icon: '🧮' },
  { to: '/leaderboard',  label: 'Leaderboard',   icon: '🏆' },
];

// Voice preference persisted in localStorage
const VOICE_KEY = 'fq_voice_enabled';

=======
  { to: '/dashboard',   label: '🏠 Dashboard'   },
  { to: '/levels',      label: '🗺️ Levels'       },  // entry point for quizzes
  { to: '/simulate',    label: '🧮 Simulate'     },
  { to: '/leaderboard', label: '🏆 Leaderboard'  },
];

>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate         = useNavigate();
  const { pathname }     = useLocation();
<<<<<<< HEAD
  const [menuOpen, setMenuOpen] = useState(false);
  const [voiceOn, setVoiceOn]   = useState(() => {
    try { return localStorage.getItem(VOICE_KEY) !== 'false'; } catch { return true; }
  });
=======
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

<<<<<<< HEAD
  const toggleVoice = () => {
    const next = !voiceOn;
    setVoiceOn(next);
    localStorage.setItem(VOICE_KEY, String(next));
    if (!next && window.speechSynthesis) window.speechSynthesis.cancel();
  };

  // Close mobile menu on route change
  useEffect(() => { setMenuOpen(false); }, [pathname]);

  const isActive = (to) =>
    pathname === to ||
    (to === '/levels' && pathname === '/quiz') ||
    (to === '/learning-path' && pathname.startsWith('/learning'));

  return (
    <nav
      className="sticky top-0 z-50 border-b border-slate-700/80"
      style={{ background: 'rgba(15,23,42,0.97)', backdropFilter: 'blur(12px)' }}
    >
      <div className="max-w-7xl mx-auto px-4 flex items-center justify-between h-16 gap-4">

        {/* Logo */}
        <Link
          to="/dashboard"
          className="text-xl font-bold tracking-wide flex-shrink-0 flex items-center gap-2"
          style={{ color: '#818cf8' }}
        >
          <span className="text-2xl">💰</span>
          <span className="hidden sm:inline">FinQuest</span>
        </Link>

        {/* Desktop nav links */}
        <div className="hidden md:flex items-center gap-1 flex-1 justify-center">
          {NAV_LINKS.map(({ to, label, icon }) => {
            const active = isActive(to);
            return (
              <Link
                key={to}
                to={to}
                className={`relative px-3 py-1.5 rounded-lg text-sm font-medium transition-all duration-200
                  flex items-center gap-1.5
                  ${active
                    ? 'text-white'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800/70'}`}
              >
                <span className="text-base">{icon}</span>
                {label}
                {active && (
                  <span
                    className="absolute bottom-0 left-2 right-2 h-0.5 rounded-full"
                    style={{
                      background: 'linear-gradient(90deg, #6366f1, #8b5cf6)',
                      animation: 'fadeIn 0.2s ease',
                    }}
                  />
                )}
              </Link>
            );
          })}
        </div>

        {/* Right side */}
        {user && (
          <div className="flex items-center gap-2 flex-shrink-0">
            {/* XP pill */}
            <div className="hidden sm:flex items-center gap-2 bg-slate-800/80 border border-slate-700/60 px-3 py-1.5 rounded-full text-sm">
              <span className="text-amber-400 font-bold">Lv.{user.level ?? 1}</span>
              <span className="text-slate-600">|</span>
              <span className="text-indigo-300 font-semibold">{user.xp ?? 0} XP</span>
            </div>

            {/* Voice toggle */}
            <button
              onClick={toggleVoice}
              title={voiceOn ? 'Voice ON — click to mute' : 'Voice OFF — click to enable'}
              aria-label={voiceOn ? 'Mute voice' : 'Enable voice'}
              className="w-8 h-8 rounded-lg flex items-center justify-center text-base
                         transition-all duration-200 border
                         hover:bg-slate-700/60"
              style={{
                background: voiceOn ? 'rgba(99,102,241,0.15)' : 'rgba(51,65,85,0.4)',
                borderColor: voiceOn ? 'rgba(99,102,241,0.4)' : 'rgba(71,85,105,0.4)',
                color: voiceOn ? '#818cf8' : '#475569',
              }}
            >
              {voiceOn ? '🔊' : '🔇'}
            </button>

            {/* Logout */}
            <button
              onClick={handleLogout}
              className="hidden sm:block text-sm text-slate-400 hover:text-red-400 transition-colors px-2 py-1 rounded-lg hover:bg-red-950/30"
            >
              Logout
            </button>

            {/* Mobile hamburger */}
            <button
              onClick={() => setMenuOpen(m => !m)}
              className="md:hidden w-8 h-8 flex flex-col items-center justify-center gap-1.5 rounded-lg hover:bg-slate-800 transition-colors"
              aria-label="Toggle menu"
              aria-expanded={menuOpen}
            >
              <span className={`block w-5 h-0.5 bg-slate-300 rounded transition-all duration-200 ${menuOpen ? 'rotate-45 translate-y-2' : ''}`} />
              <span className={`block w-5 h-0.5 bg-slate-300 rounded transition-all duration-200 ${menuOpen ? 'opacity-0' : ''}`} />
              <span className={`block w-5 h-0.5 bg-slate-300 rounded transition-all duration-200 ${menuOpen ? '-rotate-45 -translate-y-2' : ''}`} />
            </button>
=======
  return (
    <nav className="bg-slate-900 border-b border-slate-700 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 flex items-center justify-between h-16">

        {/* Logo */}
        <Link to="/dashboard" className="text-xl font-bold text-indigo-400 tracking-wide flex-shrink-0">
          💰 FinQuest
        </Link>

        {/* Nav links — hidden on small screens */}
        <div className="hidden md:flex gap-1">
          {NAV_LINKS.map(({ to, label }) => (
            <Link key={to} to={to}
              className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors
                ${pathname === to || (to === '/levels' && pathname === '/quiz')
                  ? 'bg-indigo-600 text-white'
                  : 'text-slate-400 hover:text-white hover:bg-slate-800'}`}>
              {label}
            </Link>
          ))}
        </div>

        {/* Right side: XP pill + logout */}
        {user && (
          <div className="flex items-center gap-3 flex-shrink-0">
            <div className="flex items-center gap-2 bg-slate-800 px-3 py-1 rounded-full text-sm">
              <span className="text-amber-400 font-bold">Lv.{user.level ?? 1}</span>
              <span className="text-slate-600">|</span>
              <span className="text-indigo-300">{user.xp ?? 0} XP</span>
            </div>
            <button onClick={handleLogout}
              className="text-sm text-slate-400 hover:text-red-400 transition-colors">
              Logout
            </button>
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
          </div>
        )}
      </div>

<<<<<<< HEAD
      {/* Mobile menu */}
      {menuOpen && (
        <div
          className="md:hidden border-t border-slate-700/60 px-4 py-3 flex flex-col gap-1"
          style={{ background: 'rgba(15,23,42,0.98)', animation: 'fadeSlideUp 0.2s ease' }}
        >
          {/* XP pill mobile */}
          {user && (
            <div className="flex items-center gap-2 bg-slate-800/60 px-3 py-2 rounded-lg text-sm mb-2">
              <span className="text-amber-400 font-bold">Lv.{user.level ?? 1}</span>
              <span className="text-slate-600">|</span>
              <span className="text-indigo-300">{user.xp ?? 0} XP</span>
            </div>
          )}
          {NAV_LINKS.map(({ to, label, icon }) => (
            <Link
              key={to}
              to={to}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors
                ${isActive(to)
                  ? 'bg-indigo-600/20 text-indigo-300 border border-indigo-500/30'
                  : 'text-slate-400 hover:text-white hover:bg-slate-800/60'}`}
            >
              <span className="text-base">{icon}</span>
              {label}
            </Link>
          ))}
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-red-400 hover:bg-red-950/30 transition-colors mt-1"
          >
            <span>🚪</span> Logout
          </button>
        </div>
      )}
=======
      {/* Mobile nav — shown below md breakpoint */}
      <div className="md:hidden flex gap-1 px-4 pb-2 overflow-x-auto">
        {NAV_LINKS.map(({ to, label }) => (
          <Link key={to} to={to}
            className={`flex-shrink-0 px-3 py-1 rounded-lg text-xs font-medium transition-colors
              ${pathname === to
                ? 'bg-indigo-600 text-white'
                : 'text-slate-400 hover:text-white bg-slate-800'}`}>
            {label}
          </Link>
        ))}
      </div>
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
    </nav>
  );
}
