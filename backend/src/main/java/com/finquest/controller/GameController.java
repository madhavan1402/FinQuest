package com.finquest.controller;

import com.finquest.dto.LevelCompleteDto;
import com.finquest.dto.LevelInfoDto;
import com.finquest.model.Achievement;
import com.finquest.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {

    private final LevelService levelService;

    // GET /api/levels
    // Returns the level map: titles, descriptions, and XP thresholds for each milestone.
    // The frontend uses this to render the level progression screen.
    @GetMapping("/levels")
    public ResponseEntity<List<LevelInfoDto>> getLevels() {
        return ResponseEntity.ok(levelService.getLevelInfo());
    }

    // POST /api/complete-level?userId=1
    // Called when a user finishes a lesson or quiz.
    // Awards XP, checks for level-up, unlocks badges, and returns the updated state.
    @PostMapping("/complete-level")
    public ResponseEntity<LevelCompleteDto> completeLevel(@RequestParam Long userId) {
        return ResponseEntity.ok(levelService.completeLevel(userId));
    }

    // GET /api/achievements?userId=1
    // Returns all badges earned by a user — used to populate the achievements screen.
    @GetMapping("/achievements")
    public ResponseEntity<List<Achievement>> getAchievements(@RequestParam Long userId) {
        return ResponseEntity.ok(levelService.getUserAchievements(userId));
    }
}
