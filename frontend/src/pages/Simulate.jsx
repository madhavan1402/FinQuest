// Simulate.jsx — Premium Financial Simulation UI
import { useState } from 'react';
import { useAuth }  from '../context/AuthContext';
import { runBudget, runStock, runTax } from '../api/endpoints';
import CountUp from '../components/CountUp';

const TABS = [
  { id: 'Budget', icon: '💰', label: 'Budget', desc: 'Calculate your savings rate' },
  { id: 'Stock',  icon: '📈', label: 'Stock',  desc: 'Virtual stock trading' },
  { id: 'Tax',    icon: '🧾', label: 'Tax',    desc: 'Income tax calculator' },
];

export default function Simulate() {
  const { user, saveUser } = useAuth();
  const [activeTab, setActiveTab] = useState('Budget');
  const [result,    setResult]    = useState(null);
  const [loading,   setLoading]   = useState(false);
  const [error,     setError]     = useState('');
  const [budget, setBudget]       = useState({ income: '', expenses: '' });
  const [stockDecisions, setStockDecisions] = useState('BUY,BUY,SELL');
  const [salary, setSalary]       = useState('');
  const userId = user?.userId ?? user?.id;

  const handleRun = async () => {
    setLoading(true); setError(''); setResult(null);
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
      if (res.data.xp !== undefined) saveUser({ ...user, xp: res.data.xp, level: res.data.level });
    } catch {
      setError('Simulation failed. Make sure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  const scoreColor = (score) => {
    if (score >= 80) return 'text-emerald-400';
    if (score >= 60) return 'text-amber-400';
    return 'text-red-400';
  };

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">

      <div className="mb-6 fade-slide-up">
        <h1 className="text-3xl font-bold text-white">🧮 Simulations</h1>
        <p className="text-slate-400 mt-1">Run financial simulations and earn XP</p>
      </div>

      {/* Tab switcher */}
      <div className="grid grid-cols-3 gap-2 mb-6 fade-slide-up">
        {TABS.map(tab => (
          <button
            key={tab.id}
            onClick={() => { setActiveTab(tab.id); setResult(null); setError(''); }}
            className={`rounded-xl p-3 text-left border transition-all duration-200
              ${activeTab === tab.id
                ? 'border-indigo-500/60 text-white'
                : 'bg-slate-800/60 border-slate-700/60 text-slate-400 hover:border-slate-600 hover:text-slate-300'}`}
            style={activeTab === tab.id ? {
              background: 'linear-gradient(135deg, rgba(79,70,229,0.2), rgba(139,92,246,0.12))',
            } : {}}
          >
            <div className="text-xl mb-1">{tab.icon}</div>
            <div className="font-bold text-sm">{tab.label}</div>
            <div className="text-xs opacity-60 mt-0.5">{tab.desc}</div>
          </button>
        ))}
      </div>

      {/* Form card */}
      <div className="bg-slate-800/90 border border-slate-700/80 rounded-2xl p-6 mb-6 fade-slide-up">

        {activeTab === 'Budget' && (
          <div className="space-y-4">
            <p className="text-slate-300 text-sm">Enter your monthly income and expenses to calculate your savings rate.</p>
            {[
              { label: 'Monthly Income (₹)', key: 'income', placeholder: '50000', icon: '💵' },
              { label: 'Monthly Expenses (₹)', key: 'expenses', placeholder: '35000', icon: '🛒' },
            ].map(({ label, key, placeholder, icon }) => (
              <div key={key}>
                <label className="block text-slate-400 text-sm mb-1.5 font-medium">{icon} {label}</label>
                <input
                  type="number" placeholder={placeholder} value={budget[key]}
                  onChange={e => setBudget({ ...budget, [key]: e.target.value })}
                  className="w-full bg-slate-900/80 border border-slate-600/80 rounded-xl px-4 py-3
                             text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500
                             transition-colors text-sm"
                />
              </div>
            ))}
          </div>
        )}

        {activeTab === 'Stock' && (
          <div>
            <p className="text-slate-300 text-sm mb-4">
              Enter BUY/SELL decisions separated by commas. Start with ₹1,00,000 virtual money.
            </p>
            <div className="bg-slate-900/60 border border-slate-700/60 rounded-xl p-4 mb-4 text-xs text-slate-400 space-y-1">
              <p>📈 <strong className="text-slate-300">BUY</strong> — spend ₹10,000 for 10 units</p>
              <p>📉 <strong className="text-slate-300">SELL</strong> — sell all units at ₹1,000/unit</p>
            </div>
            <label className="block text-slate-400 text-sm mb-1.5 font-medium">📋 Decisions</label>
            <input
              type="text" value={stockDecisions}
              onChange={e => setStockDecisions(e.target.value)}
              placeholder="BUY,BUY,SELL,BUY,SELL"
              className="w-full bg-slate-900/80 border border-slate-600/80 rounded-xl px-4 py-3
                         text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500
                         transition-colors text-sm font-mono"
            />
          </div>
        )}

        {activeTab === 'Tax' && (
          <div>
            <p className="text-slate-300 text-sm mb-4">Enter your annual salary to calculate income tax under the old regime.</p>
            <label className="block text-slate-400 text-sm mb-1.5 font-medium">💼 Annual Salary (₹)</label>
            <input
              type="number" placeholder="800000" value={salary}
              onChange={e => setSalary(e.target.value)}
              className="w-full bg-slate-900/80 border border-slate-600/80 rounded-xl px-4 py-3
                         text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500
                         transition-colors text-sm"
            />
          </div>
        )}

        {error && (
          <div className="mt-4 bg-red-950/50 border border-red-500/40 text-red-300 text-sm rounded-xl px-4 py-3 flex items-center gap-2">
            <span>⚠️</span><span>{error}</span>
          </div>
        )}

        <button
          onClick={handleRun}
          disabled={loading}
          className="btn-primary mt-6 w-full py-3.5 rounded-xl text-base"
        >
          {loading ? (
            <span className="flex items-center justify-center gap-2">
              <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              Running simulation…
            </span>
          ) : `▶ Run ${activeTab} Simulation`}
        </button>
      </div>

      {/* Result card */}
      {result && (
        <div
          className="bg-slate-800/90 border border-emerald-500/40 rounded-2xl p-6"
          style={{ animation: 'fadeSlideUp 0.4s ease' }}
        >
          <div className="flex items-center gap-2 mb-4">
            <span className="text-xl">📊</span>
            <h2 className="text-white font-bold text-lg">Simulation Result</h2>
          </div>

          <p className="text-slate-300 text-sm mb-5 leading-relaxed bg-slate-900/40 rounded-xl p-4 border border-slate-700/40">
            {result.result}
          </p>

          <div className="grid grid-cols-3 gap-3">
            <div className="bg-slate-900/60 rounded-xl p-4 text-center border border-slate-700/40">
              <p className="text-slate-400 text-xs mb-1">Score</p>
              <CountUp from={0} to={result.score ?? 0} duration={700} className={`font-black text-xl ${scoreColor(result.score ?? 0)}`} />
              <p className="text-slate-500 text-xs">/ 100</p>
            </div>
            {result.xpEarned !== undefined && (
              <div className="bg-slate-900/60 rounded-xl p-4 text-center border border-slate-700/40">
                <p className="text-slate-400 text-xs mb-1">XP Earned</p>
                <CountUp from={0} to={result.xpEarned} duration={600} className="text-indigo-400 font-black text-xl" suffix=" XP" />
              </div>
            )}
            {result.level !== undefined && (
              <div className="bg-slate-900/60 rounded-xl p-4 text-center border border-slate-700/40">
                <p className="text-slate-400 text-xs mb-1">Level</p>
                <p className="text-amber-400 font-black text-xl">Lv. {result.level}</p>
              </div>
            )}
          </div>

          {result.leveledUp && (
            <div className="mt-4 bg-amber-900/40 border border-amber-500/40 text-amber-300 text-sm font-bold rounded-xl px-4 py-2.5 text-center"
              style={{ animation: 'badge-pop 0.5s ease' }}>
              🎊 Level Up! You are now Level {result.level}!
            </div>
          )}
        </div>
      )}
    </div>
  );
}
