// Levels.jsx
// Fetches level data from GET /api/levels, merges with local definitions,
// and renders clickable cards. Unlocked cards navigate to /quiz?level=N.

import { useState, useEffect } from 'react';
import { useNavigate }  from 'react-router-dom';
import api              from '../services/api';
import { useAuth }      from '../context/AuthContext';
import LevelCard        from '../components/LevelCard';

const LOCAL_LEVELS = [
  { level: 1,  title: 'Money Basics',       description: 'Learn what money is and how it works in daily life.'           },
  { level: 2,  title: 'Budgeting 101',       description: 'Track income and expenses to build your first budget.'         },
  { level: 3,  title: 'Smart Saving',        description: 'Discover saving strategies and build an emergency fund.'       },
  { level: 4,  title: 'Debt & Credit',       description: 'Understand credit scores, loans, and managing debt wisely.'    },
  { level: 5,  title: 'Investing Basics',    description: 'Introduction to stocks, bonds, and mutual funds.'              },
  { level: 6,  title: 'Stock Market',        description: 'How the stock market works and how to read charts.'            },
  { level: 7,  title: 'Tax Planning',        description: 'Learn income tax slabs, deductions, and how to file returns.'  },
  { level: 8,  title: 'Mutual Funds',        description: 'SIPs, NAV, and how to pick the right mutual fund.'            },
  { level: 9,  title: 'Retirement Planning', description: 'Plan for the future with PPF, NPS, and pension schemes.'      },
  { level: 10, title: 'Financial Freedom',   description: 'Build passive income streams and achieve financial independence.' },
];

export default function Levels() {
  const { user }    = useAuth();
  const navigate    = useNavigate();

  const [levels,  setLevels]  = useState([]);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState('');

  // A level is unlocked when the user's current level >= that level number
  const currentLevel = user?.level ?? 1;

  useEffect(() => {
    api.get('/levels')
      .then(({ data }) => {
        // Build xpRequired lookup from backend response
        const xpMap = {};
        data.forEach(e => { xpMap[e.level] = e.xpRequired; });

        setLevels(LOCAL_LEVELS.map(l => ({
          ...l,
          xpRequired: xpMap[l.level] ?? l.level * 100,
          unlocked:   l.level <= currentLevel,
        })));
      })
      .catch(() => {
        // Backend unreachable — use local fallback so page still renders
        setLevels(LOCAL_LEVELS.map(l => ({
          ...l,
          xpRequired: l.level * 100,
          unlocked:   l.level <= currentLevel,
        })));
        setError('Backend unreachable — showing local level data.');
      })
      .finally(() => setLoading(false));
  }, [currentLevel]);

  // Navigate to the quiz for the selected level
  const handleLevelClick = (levelNumber) => {
    navigate(`/quiz?level=${levelNumber}`);
  };

  const unlockedCount = levels.filter(l => l.unlocked).length;

  return (
    <div className="max-w-6xl mx-auto px-4 py-10">

      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white">🗺️ Level Map</h1>
        <p className="text-slate-400 mt-1">
          Click an unlocked level to start its quiz
        </p>
      </div>

      {/* Soft error banner */}
      {error && (
        <div className="bg-amber-950/50 border border-amber-500/40 text-amber-300
                        text-sm rounded-xl px-4 py-3 mb-6 flex items-center gap-2">
          <span>⚠️</span><span>{error}</span>
        </div>
      )}

      {/* Loading */}
      {loading && (
        <div className="flex flex-col items-center justify-center py-24 gap-4">
          <div className="w-10 h-10 rounded-full border-4 border-indigo-500
                          border-t-transparent animate-spin" />
          <p className="text-slate-400 text-sm">Loading levels…</p>
        </div>
      )}

      {!loading && (
        <>
          {/* Summary bar */}
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-5 mb-8
                          flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <span className="text-white font-semibold">Your Level: </span>
              <span className="text-emerald-400 font-bold text-lg">Level {currentLevel}</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="bg-emerald-900/60 border border-emerald-500/40
                               text-emerald-300 text-sm px-3 py-1 rounded-full">
                ✅ {unlockedCount} Unlocked
              </span>
              <span className="bg-slate-700 border border-slate-600
                               text-slate-400 text-sm px-3 py-1 rounded-full">
                🔒 {levels.length - unlockedCount} Locked
              </span>
            </div>
          </div>

          {/* Level cards grid */}
          <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-5 gap-4">
            {levels.map(lvl => (
              <LevelCard
                key={lvl.level}
                level={lvl.level}
                title={lvl.title}
                description={lvl.description}
                xpRequired={lvl.xpRequired}
                unlocked={lvl.unlocked}
                // Pass click handler — LevelCard only calls it when unlocked
                onClick={() => handleLevelClick(lvl.level)}
              />
            ))}
          </div>
        </>
      )}
    </div>
  );
}
