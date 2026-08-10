export default function LearningNode({ module, index, onClick }) {
  const locked = module.status === 'locked';
  const statusText = module.status === 'completed' ? 'Completed' : locked ? 'Locked' : module.status === 'current' ? 'Start' : 'Available';
  return <div className={`learning-node-wrap ${index % 2 ? 'right' : 'left'} ${module.status}`}>
    <button className="learning-node" disabled={locked} onClick={onClick} aria-label={`${module.title}: ${statusText}`}>
      <span>{module.status === 'completed' ? '✓' : locked ? '🔒' : module.level}</span>
    </button>
    <article className="node-label"><span className="node-status">{statusText}</span><h2>{module.title}</h2><p>{module.xp} XP · {module.coins} coins</p>{!locked && <button onClick={onClick}>{module.status === 'completed' ? 'Review' : 'Start quiz'}</button>}</article>
  </div>;
}
