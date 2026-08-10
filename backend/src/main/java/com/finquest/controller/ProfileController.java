package com.finquest.controller;

import com.finquest.dto.ChangePasswordRequest;
import com.finquest.dto.MessageResponse;
import com.finquest.dto.ProfileDto;
import com.finquest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Profile API for the currently authenticated user.
 * <p>
 * Unlike {@code UserController#getProfile}, which fetches by {@code ?userId=},
 * these endpoints read the user id from the security context (the JWT), so the
 * caller can only ever access their own profile.
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    // GET /api/profile — current user's full profile
    @GetMapping
    public ResponseEntity<ProfileDto> getCurrentProfile() {
        Long userId = currentUserId();
        return ResponseEntity.ok(userService.getCurrentUserProfile(userId));
    }

    // PUT /api/profile — update name (and optionally other editable fields)
    @PutMapping
    public ResponseEntity<ProfileDto> updateProfile(@RequestBody ProfileDto request) {
        Long userId = currentUserId();
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    // PUT /api/profile/password — change password (verify old first)
    @PutMapping("/password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = currentUserId();
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof com.finquest.security.UserPrincipal principal)) {
            throw new com.finquest.exception.UnauthorizedException("Not authenticated");
        }
        return principal.getId();
    }
}
