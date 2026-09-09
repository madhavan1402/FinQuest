// src/services/AiFinanceBrainProvider.js
// AiFinanceBrainProvider — powers the 3D Finance Mentor with the backend AI Finance Brain.
// Implements the identical public contract as DeterministicMentorProvider:
//   getGreeting, getEncouragement, getThinking, getCelebration, getReaction,
//   getAssessmentGuidance, getRecommendation, getFinancialTip, plus chat.
// Silently degrades to DeterministicMentorProvider on network failure, timeout, or backend fallback.

import { mentorChat } from '../api/endpoints';
import { DeterministicMentorProvider } from './mentorProvider';

export class AiFinanceBrainProvider {
  constructor() {
    this.fallback = new DeterministicMentorProvider();
  }

  /**
   * Helper to invoke the backend AI mentor endpoint safely.
   */
  async _request(intent, userInput = null, pageContext = null, topicHint = null, fallbackFn = null) {
    try {
      const payload = { intent };
      if (userInput) payload.userInput = userInput;
      if (pageContext) payload.pageContext = pageContext;
      if (topicHint) payload.topicHint = topicHint;

      const res = await mentorChat(payload);
      if (res && res.data && res.data.message) {
        return {
          message: res.data.message,
          emotion: res.data.emotion || 'talking',
        };
      }
    } catch {
      // Graceful degradation: network error, server down, or unauthenticated fallback
    }

    if (typeof fallbackFn === 'function') {
      return fallbackFn();
    }
    return {
      message: 'Take your time to analyze this financial step carefully.',
      emotion: 'talking',
    };
  }

  async getGreeting(userName) {
    return this._request(
      'greet',
      null,
      'dashboard',
      userName,
      () => this.fallback.getGreeting(userName)
    );
  }

  async getEncouragement(levelTitle) {
    return this._request(
      'encourage',
      null,
      'learning-path',
      levelTitle,
      () => this.fallback.getEncouragement(levelTitle)
    );
  }

  async getThinking(topic) {
    return this._request(
      'think',
      null,
      'quiz',
      topic,
      () => this.fallback.getThinking(topic)
    );
  }

  async getCelebration(rewardTitle, xp) {
    const hint = rewardTitle ? `${rewardTitle}${xp ? ` (+${xp} XP)` : ''}` : null;
    return this._request(
      'celebrate',
      null,
      'quiz',
      hint,
      () => this.fallback.getCelebration(rewardTitle, xp)
    );
  }

  async getReaction(emotion, message) {
    const intent = (emotion === 'sad') ? 'react_sad' : 'chat';
    return this._request(
      intent,
      message,
      'quiz',
      null,
      () => this.fallback.getReaction(emotion, message)
    );
  }

  async getAssessmentGuidance() {
    return this._request(
      'assessment_guidance',
      null,
      'assessment',
      'onboarding',
      () => this.fallback.getAssessmentGuidance()
    );
  }

  async getRecommendation(levelNumber, title, reason) {
    const hint = `Level ${levelNumber}: ${title} (${reason || 'Next step'})`;
    return this._request(
      'recommend',
      null,
      'learning-path',
      hint,
      () => this.fallback.getRecommendation(levelNumber, title, reason)
    );
  }

  async getFinancialTip(tip) {
    return this._request(
      'explain',
      null,
      'general',
      tip,
      () => this.fallback.getFinancialTip(tip)
    );
  }

  async chat(userMessage, pageContext) {
    return this._request(
      'chat',
      userMessage,
      pageContext,
      null,
      () => ({
        message: 'Every thoughtful financial decision moves you closer to long-term wealth building.',
        emotion: 'talking',
      })
    );
  }
}

export const aiFinanceBrainProvider = new AiFinanceBrainProvider();
