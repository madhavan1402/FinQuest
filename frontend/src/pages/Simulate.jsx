// Simulate.jsx
// Three financial simulations: Budget, Stock, Tax.
// Each calls the corresponding backend API and displays the result + XP earned.

import { useState } from 'react';
import { useAuth }  from '../context/AuthContext';
import { runBudget, runStock, runTax } from '../api/endpoints';

// Tab definitions
const TABS = ['Budget', 'Stock', 'Tax'];

export default function Simulate() {
  const { user, saveUser } = useAuth();
  const [activeTab, setActiveTab] = useState('Budget');
  const [result,    setResult]    = useState(null);
  const [loading,   setLoading]   = useState(false);
  const [error,     setError]     = useState('');

  // ── Budget form state ───────────────────────────────────────────────────────
  const [budget, setBudget] = useState({ income: '', expenses: '' });

  // ── Stock form state ────────────────────────────────────────────────────────
  // decisions is a comma-separated string like "BUY,BUY,SELL"
  const [stockDecisions, setStockDecisions] = useState('BUY,BUY,SELL');

  // ── Tax form state ──────────────────────────────────────────────────────────
  const [salary, setSalary] = useState('');

  const userId = user?.userId ?? user?.id;

  const handleRun = async () => {
    setLoading(true);
    setError('');
    setResult(null);

    try {
      let res;
      if (activeTab === 'Budget') {
        res = await runBudget({ userId, income: Number(budget.income), expenses: Number(budget.expenses) });
      } else if (activeTab === 'Stock') {
        const decisions = stockDecisions.split(',').map(d => d.trim().toUpperCase());
        res = await runStock({ userId, decisions });
      } else {
        res = await runTax({ userId, salary: Number(salary) });
      }

      setResult(res.data);
      // Sync XP + level into Navbar pill
      if (res.data.xp !== undefined) {
        saveUser({ ...user, xp: res.data.xp, level: res.data.level });
      }
    } catch {
      setError('Simulation failed. Make sure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto px-4 py-10">

      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white">🧮 Simulations</h1>
        <p className="text-slate-400 mt-1">Run financial simulations and earn XP</p>
      </div>

      {/* Tab switcher */}
      <div className="flex gap-2 mb-6">
        {TABS.map(tab => (
          <button key={tab} onClick={() => { setActiveTab(tab); setResult(null); setError(''); }}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors
              ${activeTab === tab
                ? 'bg-indigo-600 text-white'
                : 'bg-slate-800 text-slate-400 hover:text-white hover:bg-slate-700'}`}>
            {tab === 'Budget' ? '💰' : tab === 'Stock' ? '📈' : '🧾'} {tab}
          </button>
        ))}
      </div>

      {/* Form card */}
      <div className="bg-slate-800 border border-slate-700 rounded-xl p-6 mb-6">

        {/* Budget form */}
        {activeTab === 'Budget' && (
          <div className="space-y-4">
            <p className="text-slate-300 text-sm mb-2">
              Enter your monthly income and expenses to calculate savings rate.
            </p>
            {[
              { label: 'Monthly Income (₹)', key: 'income', placeholder: '50000' },
              { label: 'Monthly Expenses (₹)', key: 'expenses', placeholder: '35000' },
            ].map(({ label, key, placeholder }) => (
              <div key={key}>
                <label className="block text-slate-400 text-sm mb-1">{label}</label>
                <input type="number" placeholder={placeholder} value={budget[key]}
                  onChange={e => setBudget({ ...budget, [key]: e.target.value })}
                  className="w-full bg-slate-900 border border-slate-600 rounded-lg px-4 py-2.5
                             text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500" />
              </div>
            ))}
          </div>
        )}

        {/* Stock form */}
        {activeTab === 'Stock' && (
          <div>
            <p className="text-slate-300 text-sm mb-4">
              Enter BUY/SELL decisions separated by commas. Start with ₹1,00,000 virtual money.
            </p>
            <label className="block text-slate-400 text-sm mb-1">
              Decisions (e.g. BUY,BUY,SELL,BUY,SELL)
            </label>
            <input type="text" value={stockDecisions}
              onChange={e => setStockDecisions(e.target.value)}
              className="w-full bg-slate-900 border border-slate-600 rounded-lg px-4 py-2.5
                         text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500" />
            <p className="text-slate-500 text-xs mt-2">
              BUY = spend ₹10,000 for 10 units · SELL = sell all units at ₹1,000/unit
            </p>
          </div>
        )}

        {/* Tax form */}
        {activeTab === 'Tax' && (
          <div>
            <p className="text-slate-300 text-sm mb-4">
              Enter your annual salary to calculate income tax under the old regime.
            </p>
            <label className="block text-slate-400 text-sm mb-1">Annual Salary (₹)</label>
            <input type="number" placeholder="800000" value={salary}
              onChange={e => setSalary(e.target.value)}
              className="w-full bg-slate-900 border border-slate-600 rounded-lg px-4 py-2.5
                         text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500" />
          </div>
        )}

        {error && <p className="text-red-400 text-sm mt-4">{error}</p>}

        <button onClick={handleRun} disabled={loading}
          className="mt-6 w-full bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50
                     text-white font-semibold py-3 rounded-xl transition-colors">
          {loading ? (
            <span className="flex items-center justify-center gap-2">
              <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              Running…
            </span>
          ) : `Run ${activeTab} Simulation`}
        </button>
      </div>

      {/* Result card */}
      {result && (
        <div className="bg-slate-800 border border-emerald-500/40 rounded-xl p-6">
          <h2 className="text-white font-semibold mb-4">📊 Result</h2>
          <p className="text-slate-300 text-sm mb-4 leading-relaxed">{result.result}</p>
          <div className="flex gap-3 flex-wrap">
            <div className="bg-slate-900 rounded-lg px-4 py-2.5">
              <p className="text-slate-400 text-xs mb-0.5">Score</p>
              <p className="text-emerald-400 font-bold">{result.score} / 100</p>
            </div>
            {result.xpEarned !== undefined && (
              <div className="bg-slate-900 rounded-lg px-4 py-2.5">
                <p className="text-slate-400 text-xs mb-0.5">XP Earned</p>
                <p className="text-indigo-400 font-bold">+{result.xpEarned}</p>
              </div>
            )}
            {result.level !== undefined && (
              <div className="bg-slate-900 rounded-lg px-4 py-2.5">
                <p className="text-slate-400 text-xs mb-0.5">Level</p>
                <p className="text-amber-400 font-bold">Lv. {result.level}</p>
              </div>
            )}
          </div>
          {result.leveledUp && (
            <div className="mt-4 bg-amber-900/40 border border-amber-500/40
                            text-amber-300 text-sm font-semibold rounded-lg px-4 py-2">
              🎊 Level Up! You are now Level {result.level}!
            </div>
          )}
        </div>
      )}
    </div>
  );
}
