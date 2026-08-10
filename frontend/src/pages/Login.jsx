// Login page — POST /api/auth/login
<<<<<<< HEAD
=======
// On success: saves user to AuthContext and redirects to /dashboard
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../api/endpoints';
import { useAuth } from '../context/AuthContext';

export default function Login() {
<<<<<<< HEAD
  const [form, setForm]       = useState({ email: '', password: '' });
  const [error, setError]     = useState('');
  const [loading, setLoading] = useState(false);
  const { saveUser }          = useAuth();
  const navigate              = useNavigate();
=======
  const [form, setForm]     = useState({ email: '', password: '' });
  const [error, setError]   = useState('');
  const [loading, setLoading] = useState(false);
  const { saveUser }        = useAuth();
  const navigate            = useNavigate();
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await login(form);
<<<<<<< HEAD
      saveUser(data);
=======
      saveUser(data);          // persist to context + localStorage
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed. Check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
<<<<<<< HEAD
    <div
      className="min-h-screen flex items-center justify-center px-4"
      style={{
        background: 'radial-gradient(circle at 20% 20%, rgba(79,70,229,0.12) 0%, transparent 50%), radial-gradient(circle at 80% 80%, rgba(139,92,246,0.08) 0%, transparent 50%), #0f172a',
      }}
    >
      <div className="w-full max-w-md" style={{ animation: 'fadeSlideUp 0.4s ease' }}>

        {/* Header */}
        <div className="text-center mb-8">
          <div className="text-6xl mb-3" style={{ animation: 'bounceIn 0.6s ease' }}>💰</div>
          <h1 className="text-4xl font-black text-white tracking-tight">FinQuest</h1>
          <p className="text-slate-400 mt-2">Master your financial future</p>
        </div>

        {/* Card */}
        <div className="bg-slate-800/90 border border-slate-700/80 rounded-2xl p-8 shadow-2xl"
          style={{ boxShadow: '0 0 40px rgba(99,102,241,0.1), 0 20px 60px rgba(0,0,0,0.5)' }}>
          <h2 className="text-xl font-bold text-white mb-6">Welcome back</h2>

          {error && (
            <div className="bg-red-900/40 border border-red-500/50 text-red-300 rounded-xl px-4 py-3 text-sm mb-5 flex items-center gap-2">
              <span>⚠️</span><span>{error}</span>
=======
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-md">

        {/* Header */}
        <div className="text-center mb-8">
          <div className="text-5xl mb-3">💰</div>
          <h1 className="text-3xl font-bold text-white">FinQuest</h1>
          <p className="text-slate-400 mt-1">Master your financial future</p>
        </div>

        {/* Card */}
        <div className="glow-card bg-slate-800 border border-slate-700 rounded-2xl p-8">
          <h2 className="text-xl font-semibold text-white mb-6">Welcome back</h2>

          {error && (
            <div className="bg-red-900/40 border border-red-500/50 text-red-300
                            rounded-lg px-4 py-2.5 text-sm mb-4">
              {error}
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
<<<<<<< HEAD
              <label className="block text-slate-400 text-sm mb-1.5 font-medium">Email</label>
              <input
                type="email" required value={form.email}
                onChange={e => setForm({ ...form, email: e.target.value })}
                className="w-full bg-slate-900/80 border border-slate-600/80 rounded-xl px-4 py-3
                           text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500
                           transition-colors text-sm"
                placeholder="you@example.com"
              />
            </div>
            <div>
              <label className="block text-slate-400 text-sm mb-1.5 font-medium">Password</label>
              <input
                type="password" required value={form.password}
                onChange={e => setForm({ ...form, password: e.target.value })}
                className="w-full bg-slate-900/80 border border-slate-600/80 rounded-xl px-4 py-3
                           text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500
                           transition-colors text-sm"
                placeholder="••••••••"
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full py-3 rounded-xl text-base mt-2"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Logging in…
                </span>
              ) : 'Login'}
=======
              <label className="block text-slate-400 text-sm mb-1">Email</label>
              <input
                type="email"
                required
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                className="w-full bg-slate-900 border border-slate-600 rounded-lg px-4 py-2.5
                           text-white placeholder-slate-500 focus:outline-none
                           focus:border-indigo-500 transition-colors"
                placeholder="you@example.com"
              />
            </div>

            <div>
              <label className="block text-slate-400 text-sm mb-1">Password</label>
              <input
                type="password"
                required
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                className="w-full bg-slate-900 border border-slate-600 rounded-lg px-4 py-2.5
                           text-white placeholder-slate-500 focus:outline-none
                           focus:border-indigo-500 transition-colors"
                placeholder="••••••••"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50
                         text-white font-semibold py-2.5 rounded-lg transition-colors mt-2"
            >
              {loading ? 'Logging in…' : 'Login'}
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
            </button>
          </form>

          <p className="text-slate-400 text-sm text-center mt-5">
            No account?{' '}
<<<<<<< HEAD
            <Link to="/register" className="text-indigo-400 hover:text-indigo-300 font-semibold transition-colors">
=======
            <Link to="/register" className="text-indigo-400 hover:text-indigo-300">
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
              Register here
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
