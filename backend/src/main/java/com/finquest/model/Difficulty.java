package com.finquest.model;

/**
 * Difficulty level of a learning module or quiz question.
 * Progression: Beginner=EASY, Intermediate=EASY→MEDIUM, Advanced=MEDIUM→HARD.
 * Advanced difficulty comes from calculations, scenarios, decision making,
 * comparison and financial reasoning — not just vocabulary.
 */
public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
}
