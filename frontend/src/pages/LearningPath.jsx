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
  const recommendedModule =
    allModules.find(m => m.levelNumber === path.recommendedLevelNumber) ||
    allModules.find(m => ['UNLOCKED', 'IN_PROGRESS'].includes((m.status ?? '').toUpperCase())) ||
    allModules[0];

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
    if (module && (module.status ?? '').toUpperCase() !== 'LOCKED') {
      navigate(`/quiz?level=${module.levelNumber}`);
    }
  };

  const startingLevel =
    user?.literacyLevel?.toUpperCase() === 'ADVANCED'
      ? 25
      : user?.literacyLevel?.toUpperCase() === 'INTERMEDIATE'
      ? 13
      : 1;

  const adaptiveDiff = path.adaptiveDifficulty ?? 'STANDARD';

  return (
    <main className="learning-path-page">
      {celebration && (
        <>
          <ConfettiAnimation />
          <AchievementPopup reward={celebration} onClose={() => setCelebration(null)} />
        </>
      )}

      <section className="learning-path-shell">
        <ProgressHeader {...summary} dailyFocus={path.recommendedModuleTitle || recommendedModule?.title || 'Your FinQuest journey'} />

        {/* Personalized Adaptive Recommendation Card */}
        <div
          style={{
            background: 'linear-gradient(135deg, rgba(30,41,59,0.95), rgba(15,23,42,0.95))',
            border: '1px solid rgba(99,102,241,0.4)',
            borderRadius: '24px',
            padding: '24px',
            margin: '20px 0',
            boxShadow: '0 12px 30px rgba(2,6,23,0.5)',
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px', marginBottom: '12px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span style={{ fontSize: '20px' }}>🎯</span>
              <span style={{ fontSize: '12px', fontWeight: 800, letterSpacing: '0.1em', textTransform: 'uppercase', color: '#a5b4fc' }}>
                Adaptive Learning Recommendation
              </span>
            </div>

            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
              <span style={{ fontSize: '11px', fontWeight: 700, padding: '4px 10px', borderRadius: '99px', background: 'rgba(99,102,241,0.2)', color: '#c7d2fe', border: '1px solid rgba(99,102,241,0.3)' }}>
                Baseline: {user?.literacyLevel ?? 'Beginner'} (Starts Lv. {startingLevel})
              </span>
              <span style={{ fontSize: '11px', fontWeight: 700, padding: '4px 10px', borderRadius: '99px', background: 'rgba(245,158,11,0.2)', color: '#fcd34d', border: '1px solid rgba(245,158,11,0.3)' }}>
                Risk: {user?.riskProfile ?? 'Moderate'}
              </span>
              <span
                style={{
                  fontSize: '11px',
                  fontWeight: 800,
                  padding: '4px 10px',
                  borderRadius: '99px',
                  background: adaptiveDiff === 'CHALLENGE' ? 'rgba(244,63,94,0.2)' : adaptiveDiff === 'REINFORCEMENT' ? 'rgba(56,189,248,0.2)' : 'rgba(16,185,129,0.2)',
                  color: adaptiveDiff === 'CHALLENGE' ? '#fda4af' : adaptiveDiff === 'REINFORCEMENT' ? '#bae6fd' : '#a7f3d0',
                  border: `1px solid ${adaptiveDiff === 'CHALLENGE' ? 'rgba(244,63,94,0.4)' : adaptiveDiff === 'REINFORCEMENT' ? 'rgba(56,189,248,0.4)' : 'rgba(16,185,129,0.4)'}`,
                }}
              >
                Adaptive Mode: {adaptiveDiff}
              </span>
            </div>
          </div>

          <h2 style={{ fontSize: '1.4rem', fontWeight: 800, color: '#fff', margin: '6px 0 8px' }}>
            {path.pathMastered ? '🏆 Primary Learning Track Mastered!' : `Next Focus: Level ${path.recommendedLevelNumber ?? recommendedModule?.levelNumber} — ${path.recommendedModuleTitle ?? recommendedModule?.title}`}
          </h2>

          <p style={{ color: '#cbd5e1', fontSize: '0.92rem', lineHeight: '1.5', margin: '0 0 18px' }}>
            {path.recommendationReason || 'Continue progressing through your adaptive curriculum.'}
          </p>

          <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
            <button
              className="continue-button"
              onClick={() => start(recommendedModule)}
              style={{
                background: 'linear-gradient(135deg, #6366f1, #8b5cf6)',
                padding: '12px 24px',
                borderRadius: '14px',
                fontWeight: 800,
                color: '#fff',
                border: 0,
                cursor: 'pointer',
              }}
            >
              {recommendedModule?.status === 'COMPLETED' ? 'Review Lesson' : 'Start Recommended Lesson →'}
            </button>
            <span style={{ fontSize: '12px', color: '#94a3b8' }}>
              {recommendedModule?.xpReward ?? 100} XP reward · {recommendedModule?.coinReward ?? 25} coins
            </span>
          </div>
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
                <LearningNode
                  key={module.id ?? module.levelNumber}
                  module={module}
                  index={index}
                  onClick={() => start(module)}
                />
              ))}
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}
