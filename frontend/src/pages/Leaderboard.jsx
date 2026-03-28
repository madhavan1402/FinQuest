// Leaderboard.jsx
// Fetches all users from GET /api/users and ranks them by XP descending.
// Falls back to a static demo list if the backend is unreachable.

import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api         from '../services/api';

// Medal emojis for top 3
const MEDALS = ['🥇', '🥈', '🥉'];

export default function Leaderboard() {
  const { user }            = useAuth();
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/users')
      .then(({ data }) => {
        // Sort by XP descending, then by level as tiebreaker
        const sorted = [...data].sort((a, b) =>
          b.xp !== a.xp ? b.xp - a.xp : b.level - a.level
        );
        setPlayers(sorted);
      })
      .catch(() => {
        // Fallback demo data so the page always renders
        setPlayers([
          { id: 1, name: 'Maddy',   level: 5, xp: 420, financialScore: 85 },
          { id: 2, name: 'Arjun',   level: 4, xp: 310, financialScore: 72 },
          { id: 3, name: 'Priya',   level: 3, xp: 250, financialScore: 68 },
          { id: 4, name: 'Rahul',   level: 2, xp: 180, financialScore: 55 },
          { id: 5, name: 'Sneha',   level: 1, xp: 90,  financialScore: 40 },
        ]);
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="max-w-2xl mx-auto px-4 py-10">

      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white">🏆 Leaderboard</h1>
        <p className="text-slate-400 mt-1">Top players ranked by XP</p>
      </div>

      {loading && (
        <div className="flex flex-col items-center justify-center py-24 gap-4">
          <div className="w-10 h-10 rounded-full border-4 border-indigo-500
                          border-t-transparent animate-spin" />
          <p className="text-slate-400 text-sm">Loading leaderboard…</p>
        </div>
      )}

      {!loading && (
        <div className="flex flex-col gap-3">
          {players.map((p, idx) => {
            const isCurrentUser = p.id === (user?.userId ?? user?.id);
            return (
              <div key={p.id}
                className={`flex items-center gap-4 rounded-xl px-5 py-4 border transition-all
                  ${isCurrentUser
                    ? 'bg-indigo-950/60 border-indigo-500/60'   // highlight current user
                    : 'bg-slate-800 border-slate-700'}`}
              >
                {/* Rank */}
                <div className="text-2xl w-8 text-center flex-shrink-0">
                  {idx < 3 ? MEDALS[idx] : <span className="text-slate-500 text-base font-bold">#{idx + 1}</span>}
                </div>

                {/* Name + level */}
                <div className="flex-1 min-w-0">
                  <p className={`font-semibold truncate
                    ${isCurrentUser ? 'text-indigo-300' : 'text-white'}`}>
                    {p.name} {isCurrentUser && <span className="text-xs text-indigo-400">(you)</span>}
                  </p>
                  <p className="text-slate-500 text-xs">Level {p.level}</p>
                </div>

                {/* XP */}
                <div className="text-right flex-shrink-0">
                  <p className="text-indigo-400 font-bold text-sm">{p.xp} XP</p>
                  <p className="text-slate-500 text-xs">Score: {p.financialScore}</p>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
