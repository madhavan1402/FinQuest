// Login page — POST /api/auth/login
// On success: saves user to AuthContext and redirects to /dashboard
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../api/endpoints';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [form, setForm]     = useState({ email: '', password: '' });
  const [error, setError]   = useState('');
  const [loading, setLoading] = useState(false);
  const { saveUser }        = useAuth();
  const navigate            = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await login(form);
      saveUser(data);          // persist to context + localStorage
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed. Check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
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
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
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
            </button>
          </form>

          <p className="text-slate-400 text-sm text-center mt-5">
            No account?{' '}
            <Link to="/register" className="text-indigo-400 hover:text-indigo-300">
              Register here
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
