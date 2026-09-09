// src/services/mentorProvider.js
// DeterministicMentorProvider — generates structured contextual dialogue & emotions.
// Designed with a clean provider interface so Phase 5 can swap it with AiFinanceBrainProvider
// without touching MentorContext, GlobalMentor, 3D Canvas, or page integration.

export class DeterministicMentorProvider {
  /**
   * Greeting tailored to user and context.
   */
  getGreeting(userName) {
    const name = userName ? userName.split(' ')[0] : 'friend';
    const greetings = [
      `Hello ${name}! Ready to build your financial intelligence today?`,
      `Welcome back, ${name}! Your financial quest awaits. Let's make progress!`,
      `Great to see you, ${name}! Remember: small consistent financial habits create massive wealth.`,
    ];
    // Deterministic selection based on length
    const idx = (name.length) % greetings.length;
    return {
      message: greetings[idx],
      emotion: 'happy',
    };
  }

  /**
   * Encouragement for ongoing learning or levels.
   */
  getEncouragement(levelTitle) {
    const title = levelTitle || 'this concept';
    return {
      message: `You've got this! Mastering ${title} is a key foundation for your financial independence.`,
      emotion: 'encouraging',
    };
  }

  /**
   * Thinking prompt when starting or pondering a question.
   */
  getThinking(topic) {
    const topicLabel = topic || 'this question';
    return {
      message: `Take your time to analyze ${topicLabel}. Consider the trade-off between risk, liquidity, and return.`,
      emotion: 'thinking',
    };
  }

  /**
   * Celebration on quiz pass or achievement.
   */
  getCelebration(rewardTitle, xp) {
    const xpStr = xp ? ` +${xp} XP earned!` : '';
    return {
      message: `Outstanding performance! You completed ${rewardTitle || 'the lesson'}!${xpStr} Keep this momentum going!`,
      emotion: 'celebrating',
    };
  }

  /**
   * Reaction to quiz failure or challenging question.
   */
  getReaction(emotion, message) {
    if (emotion === 'sad' && !message) {
      return {
        message: `Don't be discouraged! Financial mastery is built through practice and learning from mistakes. Let's try again!`,
        emotion: 'sad',
      };
    }
    return {
      message: message || 'Every step on this journey sharpens your financial decision-making.',
      emotion: emotion || 'idle',
    };
  }

  /**
   * Educational assessment guidance.
   */
  getAssessmentGuidance() {
    return {
      message: `Welcome to the Onboarding Assessment! There are no wrong answers here — this helps us customize your personalized starting point.`,
      emotion: 'encouraging',
    };
  }

  /**
   * Next level recommendation based on Phase 3 adaptive placement.
   */
  getRecommendation(levelNumber, title, reason) {
    return {
      message: `Recommended Next: Level ${levelNumber} (${title}). ${reason || 'Tailored to advance your current learning tier.'}`,
      emotion: 'encouraging',
    };
  }

  /**
   * Explanatory financial tip.
   */
  getFinancialTip(tip) {
    return {
      message: tip || 'Tip: Always ensure your emergency fund covers 3 to 6 months of essential living expenses before taking higher investment risks.',
      emotion: 'talking',
    };
  }
}

// Export singleton instance conforming to the MentorResponseProvider interface
export const mentorProvider = new DeterministicMentorProvider();
