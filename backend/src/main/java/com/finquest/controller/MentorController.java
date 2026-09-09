package com.finquest.controller;

import com.finquest.dto.MentorChatRequest;
import com.finquest.dto.MentorResponseDto;
import com.finquest.security.UserPrincipal;
import com.finquest.service.FinanceBrainService;
import com.finquest.service.MentorChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * MentorController — endpoints for the AI Finance Brain powering the 3D Finance Mentor.
 * All chat endpoints require JWT authentication.
 * User ID is strictly derived from the authenticated UserPrincipal — never from request parameters.
 */
@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
public class MentorController {

    private final MentorChatService mentorChatService;
    private final FinanceBrainService financeBrainService;

    /**
     * POST /api/mentor/chat
     * Receives an intent and optional context hints, builds authoritative context,
     * calls the AI Brain (or deterministic fallback), and returns structured response.
     */
    @PostMapping("/chat")
    public ResponseEntity<MentorResponseDto> chat(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody MentorChatRequest request
    ) {
        MentorResponseDto response = mentorChatService.processChat(principal.getId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/mentor/health
     * Reports configuration/availability state without invoking the external LLM.
     * Never exposes API keys or secrets.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "aiBrainEnabled", financeBrainService.isEnabled(),
                "provider", financeBrainService.getProviderName()
        ));
    }
}
