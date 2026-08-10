import '../pages/LearningPath.css';

export default function ModuleCard({ module, status, onClick }) {
  const isLocked = status === 'locked';
  const isCompleted = status === 'completed';
  const isCurrent = status === 'current';

  const statusLabel = {
    locked: 'Pending',
    current: 'In progress',
    completed: 'Completed',
    unlocked: 'Ready',
  }[status] || 'Ready';

  const ctaLabel = isLocked ? 'Locked' : isCompleted ? 'Review' : isCurrent ? 'Continue' : 'Start';

  const handleCardClick = () => {
    if (!isLocked) onClick?.();
  };

  const handleActionClick = (event) => {
    event.stopPropagation();
    if (!isLocked) onClick?.();
  };

  return (
    <article
      className={`module-card ${status}`}
      role="button"
      tabIndex={0}
      onClick={handleCardClick}
      onKeyDown={(event) => {
        if (event.key === 'Enter' || event.key === ' ') {
          event.preventDefault();
          handleCardClick();
        }
      }}
    >
      <div className={`module-node ${status}`} aria-hidden="true">
        {isCompleted ? '✓' : isCurrent ? module.icon : '○'}
      </div>

      <div className="module-copy">
        <div className="module-title-row">
          <div>
            <span className={`status-pill ${status}`}>{statusLabel}</span>
            <h3>{module.title}</h3>
          </div>
          <span className="xp-pill">+{module.xp} XP</span>
        </div>

        <div className="module-meta">
          <span>{module.difficulty}</span>
          <span>{module.duration}</span>
          <span>{module.focus}</span>
        </div>

        <p>{module.description}</p>

        <div className="module-footer">
          <span className="foot-pill">{module.day}</span>
          <span className="foot-pill">{module.badge}</span>
          <button type="button" className={`action-pill ${status}`} onClick={handleActionClick} disabled={isLocked}>
            {ctaLabel}
          </button>
        </div>
      </div>
    </article>
  );
}
