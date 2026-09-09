import '../pages/LearningPath.css';

export default function ProgressHeader({ completionPercent, xp, totalXp, level, currentLevel, coins, streak, completedModules, remainingModules, dailyFocus }) {
  const earnedXp = xp ?? totalXp ?? 0; const activeLevel = level ?? currentLevel ?? 1;
  return (
    <div className="progress-shell">
      <div className="progress-top">
        <div>
          <p className="eyebrow">Daily learning streak</p>
          <h2>Learning Path</h2>
          <p className="hero-subtitle">Today&apos;s focus: <strong>{dailyFocus}</strong></p>
        </div>
        <div className="progress-badges">
          <span className="badge-pill">⭐ {completionPercent}% complete</span>
          <span className="badge-pill accent">⚡ {earnedXp} XP</span>
        </div>
      </div>

      <div className="progress-track" aria-label="Learning progress">
        <div className="progress-fill" style={{ width: `${completionPercent}%` }} />
      </div>

      <div className="progress-stats">
        <div className="stat-box">
          <span className="stat-label">Overall completion</span>
          <strong>{completionPercent}%</strong>
        </div>
        <div className="stat-box">
          <span className="stat-label">Total XP</span>
          <strong>{earnedXp}</strong>
        </div>
        <div className="stat-box">
          <span className="stat-label">Current level</span>
          <strong>{activeLevel}</strong>
        </div>
        <div className="stat-box">
          <span className="stat-label">Coins</span>
          <strong>{coins}</strong>
        </div>
        <div className="stat-box">
          <span className="stat-label">Streak</span>
          <strong>{streak} days</strong>
        </div>
        <div className="stat-box"><span className="stat-label">Modules</span><strong>{completedModules ?? 0} done · {remainingModules ?? 0} left</strong></div>
      </div>
    </div>
  );
}
