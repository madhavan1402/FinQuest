// Login page — POST /api/auth/login
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../api/endpoints';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [form, setForm]       = useState({ email: '', password: '' });
  const [error, setError]     = useState('');
  const [loading, setLoading] = useState(false);
  const { saveUser }          = useAuth();
  const navigate              = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await login(form);
      saveUser(data);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed. Check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
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
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
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
            </button>
          </form>

          <p className="text-slate-400 text-sm text-center mt-5">
            No account?{' '}
            <Link to="/register" className="text-indigo-400 hover:text-indigo-300 font-semibold transition-colors">
              Register here
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
