package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A single row in the leaderboard.
 * <p>
 * Contains only public, safe user fields — NEVER the password hash or email.
 * The rank is 1-based (position sorted by XP descending, then level as tiebreaker).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDto {
    private Long id;
    private String username;
    private String avatar;
    private int level;
    private int xp;
    private int financialScore;
    private int rank;
    private List<String> badges;
    private int streak;
}

