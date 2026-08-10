package com.finquest.model;

/**
 * Status of a user's progress on a specific learning level.
 * LOCKED → UNLOCKED → IN_PROGRESS → COMPLETED.
 * The backend (LearningPathService) is the source of truth for status;
 * the frontend never decides whether a level is unlocked.
 */
public enum LevelStatus {
    LOCKED,
    UNLOCKED,
    IN_PROGRESS,
    COMPLETED
}
