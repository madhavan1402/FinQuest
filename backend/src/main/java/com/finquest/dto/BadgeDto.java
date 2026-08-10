package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDto {
    private String badgeCode;
    private String badgeName;
    private String icon;
    private LocalDateTime unlockedAt;
}
