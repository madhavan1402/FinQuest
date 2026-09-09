package com.finquest.controller;

import com.finquest.common.ApiResponse;
import com.finquest.dto.AssessmentQuestionDto;
import com.finquest.dto.AssessmentResultDto;
import com.finquest.dto.AssessmentSubmitRequest;
import com.finquest.exception.UnauthorizedException;
import com.finquest.security.UserPrincipal;
import com.finquest.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<AssessmentQuestionDto>>> getQuestions() {
        List<AssessmentQuestionDto> questions = assessmentService.getQuestions();
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<AssessmentResultDto>> submitAssessment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AssessmentSubmitRequest request
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        AssessmentResultDto result = assessmentService.submitAssessment(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Assessment completed successfully", result));
    }

    @GetMapping("/result")
    public ResponseEntity<ApiResponse<AssessmentResultDto>> getLatestResult(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        AssessmentResultDto result = assessmentService.getLatestResult(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
