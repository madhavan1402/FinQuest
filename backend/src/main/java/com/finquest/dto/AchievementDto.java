package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDto {
    private String code;
    private String name;
    private String description;
    private String icon;
    private int xpReward;
    private int coinReward;
    private LocalDateTime unlockedAt;
}
