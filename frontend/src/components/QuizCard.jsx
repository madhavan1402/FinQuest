// QuizCard — renders a single multiple-choice question.
// Props:
//   question    : QuizQuestion object { id, question, optionA–D }
//   selected    : currently selected answer letter ("A"/"B"/"C"/"D" or null)
//   onSelect    : callback(questionId, answerLetter)
//   revealed    : boolean — show correct/wrong colouring after submission
//   correct     : the correct answer letter (only used when revealed=true)
const OPTIONS = ['A', 'B', 'C', 'D'];

export default function QuizCard({ question, selected, onSelect, revealed, correct }) {
  const getOptionStyle = (letter) => {
    const base = 'w-full text-left px-4 py-2.5 rounded-lg border text-sm font-medium transition-all duration-200 ';
    if (!revealed) {
      return base + (selected === letter
        ? 'bg-indigo-600 border-indigo-400 text-white'
        : 'bg-slate-800 border-slate-600 text-slate-300 hover:border-indigo-500 hover:text-white');
    }
    // After submission: green = correct, red = wrong selection
    if (letter === correct)  return base + 'bg-emerald-700 border-emerald-400 text-white';
    if (letter === selected) return base + 'bg-red-700 border-red-400 text-white';
    return base + 'bg-slate-800 border-slate-700 text-slate-500';
  };

  return (
    <div className="bg-slate-800 border border-slate-700 rounded-xl p-5 glow-card">
      <p className="text-white font-medium mb-4 leading-relaxed">{question.question}</p>
      <div className="grid grid-cols-1 gap-2">
        {OPTIONS.map((letter) => (
          <button
            key={letter}
            disabled={revealed}
            onClick={() => onSelect(question.id, letter)}
            className={getOptionStyle(letter)}
          >
            <span className="text-slate-400 mr-2">{letter}.</span>
            {question[`option${letter}`]}
          </button>
        ))}
      </div>
    </div>
  );
}
