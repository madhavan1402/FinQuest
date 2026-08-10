import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getLearningPath } from '../api/endpoints';
import ProgressHeader from '../components/ProgressHeader';
import LearningNode from '../components/LearningNode';
import AchievementPopup from '../components/AchievementPopup';
import ConfettiAnimation from '../components/ConfettiAnimation';
import './LearningPath.css';

export default function LearningPath() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [path, setPath] = useState(null);
  const [error, setError] = useState('');
  const [celebration, setCelebration] = useState(() => location.state?.reward ?? null);

  useEffect(() => {
    if (!user) { navigate('/login'); return; }
    getLearningPath()
      .then(({ data }) => setPath(data))
      .catch(() => setError('We could not load your saved learning path. Start the backend and try again.'));
  }, [user, navigate]);

  if (error) return <main className="learning-path-page"><div className="path-error">{error}</div></main>;
  if (!path) return <main className="learning-path-page"><div className="path-loading">Building your FinQuest journey…</div></main>;

  // Flatten all modules across tiers for the road view
  const allModules = (path.tiers ?? []).flatMap(t => t.levels ?? []);
  const currentModule = allModules.find(m =>
    ['UNLOCKED', 'IN_PROGRESS'].includes((m.status ?? '').toUpperCase())) || allModules[0];
  const completedCount = allModules.filter(m =>
    (m.status ?? '').toUpperCase() === 'COMPLETED').length;

  const summary = path.summary ?? {
    completionPercent: allModules.length ? Math.round(completedCount * 100 / allModules.length) : 0,
    xp: user?.xp ?? 0,
    level: user?.level ?? 1,
    coins: user?.coins ?? 0,
    streak: user?.learningStreak ?? 0,
    completedModules: completedCount,
    remainingModules: allModules.length - completedCount,
  };

  const start = (module) => {
    if (module.status !== 'LOCKED') navigate(`/quiz?level=${module.levelNumber}`);
  };

  return <main className="learning-path-page">
    {celebration && <><ConfettiAnimation /><AchievementPopup reward={celebration} onClose={() => setCelebration(null)} /></>}
    <section className="learning-path-shell">
      <ProgressHeader {...summary} dailyFocus={currentModule?.title || 'Your FinQuest journey'} />
      <div className="path-intro">
        <div>
          <span className="eyebrow">Your money map</span>
          <h1>Build financial confidence, one win at a time.</h1>
        </div>
        <button className="continue-button" onClick={() => start(currentModule)}>Continue learning</button>
      </div>

      {/* Tier sections */}
      <div className="learning-road" aria-label="FinQuest learning path">
        <div className="road-line" />
        {(path.tiers ?? []).map(tier => (
          <div key={tier.tier}>
            <div className="tier-heading">
              {tier.icon && <span>{tier.icon}</span>}
              <h2>{tier.title}</h2>
              <span className="tier-count">{tier.levels.length} levels</span>
            </div>
            {(tier.levels ?? []).map((module, index) => (
              <LearningNode key={module.id ?? module.levelNumber} module={module} index={index} onClick={() => start(module)} />
            ))}
          </div>
        ))}
      </div>
    </section>
  </main>;
}
