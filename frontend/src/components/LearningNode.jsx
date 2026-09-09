export default function LearningNode({ module, index, onClick }) {
  const statusUpper = (module.status ?? 'LOCKED').toUpperCase();
  const locked = statusUpper === 'LOCKED';
  const completed = statusUpper === 'COMPLETED';
  const inProgress = statusUpper === 'IN_PROGRESS';
  const isRecommended = Boolean(module.recommended);

  const statusText = completed
    ? 'Completed'
    : locked
    ? 'Locked'
    : isRecommended
    ? 'Recommended'
    : inProgress
    ? 'In Progress'
    : 'Available';

  const nodeWrapClass = `learning-node-wrap ${index % 2 ? 'right' : 'left'} ${statusUpper.toLowerCase()}`;
  const levelNum = module.levelNumber ?? module.level ?? (index + 1);
  const xp = module.xpReward ?? module.xp ?? 100;
  const coins = module.coinReward ?? module.coins ?? 25;
  const adaptDiff = module.adaptiveDifficulty ?? 'STANDARD';

  return (
    <div className={nodeWrapClass}>
      <button
        className="learning-node"
        disabled={locked}
        onClick={onClick}
        aria-label={`${module.title}: ${statusText}`}
        style={isRecommended ? { outline: '3px solid #f59e0b', outlineOffset: '3px', boxShadow: '0 0 15px rgba(245,158,11,0.5)' } : {}}
      >
        <span>{completed ? '✓' : locked ? '🔒' : isRecommended ? '⭐' : levelNum}</span>
      </button>
      <article className="node-label">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
          <span className="node-status" style={isRecommended ? { color: '#f59e0b', fontWeight: 800 } : {}}>
            {isRecommended ? '⭐ Recommended' : statusText}
          </span>
          <span
            style={{
              fontSize: '10px',
              textTransform: 'uppercase',
              letterSpacing: '0.06em',
              fontWeight: 700,
              padding: '1px 6px',
              borderRadius: '99px',
              background: adaptDiff === 'CHALLENGE' ? 'rgba(244,63,94,0.2)' : adaptDiff === 'REINFORCEMENT' ? 'rgba(56,189,248,0.2)' : 'rgba(99,102,241,0.2)',
              color: adaptDiff === 'CHALLENGE' ? '#fb7185' : adaptDiff === 'REINFORCEMENT' ? '#7dd3fc' : '#a5b4fc',
              border: adaptDiff === 'CHALLENGE' ? '1px solid rgba(244,63,94,0.4)' : adaptDiff === 'REINFORCEMENT' ? '1px solid rgba(56,189,248,0.4)' : '1px solid rgba(99,102,241,0.4)',
            }}
          >
            {adaptDiff}
          </span>
        </div>
        <h2>{module.title}</h2>
        <p>{xp} XP · {coins} coins</p>
        {!locked && (
          <button
            onClick={onClick}
            style={isRecommended ? { background: 'linear-gradient(135deg, #f59e0b, #d97706)', boxShadow: '0 4px 12px rgba(245,158,11,0.4)' } : {}}
          >
            {completed ? 'Review' : isRecommended ? 'Start Focus' : 'Start quiz'}
          </button>
        )}
      </article>
    </div>
  );
}
