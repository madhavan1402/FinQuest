package com.finquest.model;

/**
 * The roles a FinQuest user may hold.
 * <p>
 * Only the two roles required by the task are defined:
 * <ul>
 *   <li>{@code USER}  — a regular authenticated member.</li>
 *   <li>{@code ADMIN} — a privileged member with access to admin-only endpoints.</li>
 * </ul>
 * Stored in the database as the enum name string (e.g. {@code "USER"}).
 */
public enum Role {
    USER,
    ADMIN
}

