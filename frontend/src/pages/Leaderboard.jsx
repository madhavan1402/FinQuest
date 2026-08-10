<<<<<<< HEAD
// Leaderboard.jsx — Premium Leaderboard
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api         from '../services/api';
import { SkeletonList } from '../components/SkeletonCard';

const MEDALS = ['🥇', '🥈', '🥉'];
const PODIUM_STYLES = ['podium-1', 'podium-2', 'podium-3'];

function Avatar({ name, size = 'w-10 h-10', textSize = 'text-sm' }) {
  return (
    <div className={`${size} rounded-full bg-indigo-900/70 border border-indigo-500/40
                     flex items-center justify-center font-bold text-indigo-300 flex-shrink-0 ${textSize}`}>
      {(name ?? '?').charAt(0).toUpperCase()}
    </div>
  );
}

export default function Leaderboard() {
  const { user }              = useAuth();
=======
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
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
<<<<<<< HEAD
    api.get('/user/leaderboard')
      .then(({ data }) => {
        const sorted = [...data].sort((a, b) => b.xp !== a.xp ? b.xp - a.xp : b.level - a.level);
        setPlayers(sorted);
      })
      .catch(() => {
        setPlayers([
          { id: 1, name: 'Maddy',  level: 5, xp: 420, financialScore: 85, streak: 3 },
          { id: 2, name: 'Arjun',  level: 4, xp: 310, financialScore: 72, streak: 2 },
          { id: 3, name: 'Priya',  level: 3, xp: 250, financialScore: 68, streak: 1 },
          { id: 4, name: 'Rahul',  level: 2, xp: 180, financialScore: 55, streak: 1 },
          { id: 5, name: 'Sneha',  level: 1, xp: 90,  financialScore: 40, streak: 0 },
=======
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
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
        ]);
      })
      .finally(() => setLoading(false));
  }, []);

<<<<<<< HEAD
  const currentUserId = user?.userId ?? user?.id;
  const top3    = players.slice(0, 3);
  const rest    = players.slice(3);

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">

      <div className="mb-6 fade-slide-up">
=======
  return (
    <div className="max-w-2xl mx-auto px-4 py-10">

      <div className="mb-8">
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
        <h1 className="text-3xl font-bold text-white">🏆 Leaderboard</h1>
        <p className="text-slate-400 mt-1">Top players ranked by XP</p>
      </div>

<<<<<<< HEAD
      {loading && <SkeletonList rows={5} />}

      {!loading && (
        <>
          {/* ── Podium top 3 ─────────────────────────────────────────────── */}
          {top3.length > 0 && (
            <div className="grid grid-cols-3 gap-3 mb-6">
              {top3.map((p, idx) => {
                const isMe = p.id === currentUserId;
                return (
                  <div
                    key={p.id}
                    className={`rounded-2xl border p-4 text-center transition-all duration-300
                      hover:-translate-y-1 hover:shadow-xl fade-slide-up ${PODIUM_STYLES[idx]}
                      ${isMe ? 'ring-2 ring-indigo-500/60' : ''}`}
                    style={{ animationDelay: `${idx * 80}ms` }}
                  >
                    <div className="text-3xl mb-2">{MEDALS[idx]}</div>
                    <Avatar name={p.username ?? p.name} size="w-12 h-12 mx-auto mb-2" textSize="text-base" />
                    <p className={`font-bold text-sm truncate ${isMe ? 'text-indigo-300' : 'text-white'}`}>
                      {p.username ?? p.name}
                      {isMe && <span className="block text-xs text-indigo-400 font-normal">YOU</span>}
                    </p>
                    <p className="text-slate-400 text-xs mt-1">Level {p.level}</p>
                    <p className="text-indigo-400 font-bold text-sm mt-1">{p.xp} XP</p>
                    {(p.streak ?? 0) > 0 && (
                      <p className="text-orange-400 text-xs mt-1">🔥 {p.streak}d</p>
                    )}
                  </div>
                );
              })}
            </div>
          )}

          {/* ── Rest of leaderboard ───────────────────────────────────────── */}
          {rest.length > 0 && (
            <div className="flex flex-col gap-2">
              {rest.map((p, idx) => {
                const rank = idx + 4;
                const isMe = p.id === currentUserId;
                return (
                  <div
                    key={p.id}
                    className={`flex items-center gap-4 rounded-xl px-5 py-3.5 border transition-all duration-200
                      hover:-translate-y-0.5 hover:shadow-lg fade-slide-up
                      ${isMe
                        ? 'bg-indigo-950/60 border-indigo-500/50'
                        : 'bg-slate-800/80 border-slate-700/60 hover:border-slate-600'}`}
                    style={{ animationDelay: `${(idx + 3) * 60}ms` }}
                  >
                    <div className="w-8 text-center flex-shrink-0">
                      <span className="text-slate-500 text-sm font-bold">#{rank}</span>
                    </div>
                    <Avatar name={p.username ?? p.name} />
                    <div className="flex-1 min-w-0">
                      <p className={`font-semibold text-sm truncate ${isMe ? 'text-indigo-300' : 'text-white'}`}>
                        {p.username ?? p.name}
                        {isMe && <span className="ml-2 text-xs text-indigo-400">(you)</span>}
                      </p>
                      <p className="text-slate-500 text-xs">
                        Level {p.level} {(p.streak ?? 0) > 0 && `· 🔥 ${p.streak}d`}
                      </p>
                    </div>
                    <div className="text-right flex-shrink-0">
                      <p className="text-indigo-400 font-bold text-sm">{p.xp} XP</p>
                      <p className="text-slate-500 text-xs">Score: {p.financialScore}</p>
                    </div>
                  </div>
                );
              })}
            </div>
          )}

          {/* Current user not in list */}
          {players.length > 0 && !players.find(p => p.id === currentUserId) && (
            <div className="mt-4 bg-indigo-950/40 border border-indigo-500/30 rounded-xl px-5 py-3.5 flex items-center gap-4">
              <div className="w-8 text-center text-slate-500 text-sm font-bold flex-shrink-0">—</div>
              <Avatar name={user?.name} />
              <div className="flex-1">
                <p className="text-indigo-300 font-semibold text-sm">{user?.name} <span className="text-xs text-indigo-400">(you)</span></p>
                <p className="text-slate-500 text-xs">Level {user?.level ?? 1}</p>
              </div>
              <div className="text-right">
                <p className="text-indigo-400 font-bold text-sm">{user?.xp ?? 0} XP</p>
              </div>
            </div>
          )}
        </>
=======
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
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
      )}
    </div>
  );
}
