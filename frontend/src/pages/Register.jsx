// Register page — POST /api/auth/register
// On success: saves user to AuthContext and redirects to /dashboard
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../api/endpoints';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const [form, setForm]       = useState({ name: '', email: '', password: '' });
  const [error, setError]     = useState('');
  const [loading, setLoading] = useState(false);
  const { saveUser }          = useAuth();
  const navigate              = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await register(form);
      if (data?.accessToken) {
        saveUser(data);
        // New users always need assessment first
        navigate('/assessment');
      } else {
        navigate('/login');
      }
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.error || 'Registration failed. Try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-md">

        <div className="text-center mb-8">
          <div className="text-5xl mb-3">🚀</div>
          <h1 className="text-3xl font-bold text-white">Join FinQuest</h1>
          <p className="text-slate-400 mt-1">Start your financial journey today</p>
        </div>

        <div className="glow-card bg-slate-800 border border-slate-700 rounded-2xl p-8">
          <h2 className="text-xl font-semibold text-white mb-6">Create account</h2>

          {error && (
            <div className="bg-red-900/40 border border-red-500/50 text-red-300
                            rounded-lg px-4 py-2.5 text-sm mb-4">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {[
              { key: 'name',     label: 'Full Name', type: 'text',     placeholder: 'Maddy' },
              { key: 'email',    label: 'Email',     type: 'email',    placeholder: 'you@example.com' },
              { key: 'password', label: 'Password',  type: 'password', placeholder: '••••••••' },
            ].map(({ key, label, type, placeholder }) => (
              <div key={key}>
                <label className="block text-slate-400 text-sm mb-1">{label}</label>
                <input
                  type={type}
                  required
                  value={form[key]}
                  onChange={(e) => setForm({ ...form, [key]: e.target.value })}
                  placeholder={placeholder}
                  className="w-full bg-slate-900 border border-slate-600 rounded-lg px-4 py-2.5
                             text-white placeholder-slate-500 focus:outline-none
                             focus:border-indigo-500 transition-colors"
                />
              </div>
            ))}

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50
                         text-white font-semibold py-2.5 rounded-lg transition-colors mt-2"
            >
              {loading ? 'Creating account…' : 'Register'}
            </button>
          </form>

          <p className="text-slate-400 text-sm text-center mt-5">
            Already have an account?{' '}
            <Link to="/login" className="text-indigo-400 hover:text-indigo-300">
              Login
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
