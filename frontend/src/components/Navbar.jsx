// Navbar.jsx
// Sticky top navigation bar shown on all authenticated pages.
// Uses React Router <Link> — no page reloads.

import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NAV_LINKS = [
  { to: '/dashboard',   label: '🏠 Dashboard'   },
  { to: '/levels',      label: '🗺️ Levels'       },  // entry point for quizzes
  { to: '/simulate',    label: '🧮 Simulate'     },
  { to: '/leaderboard', label: '🏆 Leaderboard'  },
];

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate         = useNavigate();
  const { pathname }     = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

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
          </div>
        )}
      </div>

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
    </nav>
  );
}
