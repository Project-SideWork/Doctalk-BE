package com.capstone.domain.AI.controller;

import com.capstone.docs.AIControllerDocs;
import com.capstone.domain.AI.dto.AIRequest;
import com.capstone.domain.AI.dto.AIResponse;
import com.capstone.domain.AI.dto.AIReviseRequest;
import com.capstone.domain.AI.service.AIService;
import com.capstone.global.ratelimit.annotation.RateLimit;
import com.capstone.global.ratelimit.enums.RateLimitKeyType;
import com.capstone.global.response.ApiResponse;
import com.capstone.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/text")
@CrossOrigin("*")
@Slf4j
public class AIController implements AIControllerDocs
{
    private final AIService aiService;

    @PostMapping("/correct")
    @RateLimit(limit = 30, duration = 3600, keyType = RateLimitKeyType.USER)
    public ResponseEntity<ApiResponse<AIResponse>>ModifyGrammar (@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AIRequest aiRequest)
    {
        String response= aiService.correctGrammar(aiRequest,userDetails);
        AIResponse aiResponse = new AIResponse(response);

        return ResponseEntity.ok(ApiResponse.onSuccess(aiResponse));
    }
    @PostMapping("/summarize")
    @RateLimit(limit = 30, duration = 3600, keyType = RateLimitKeyType.USER)
    public ResponseEntity<ApiResponse<AIResponse>>ModifyDocument (@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody AIRequest aiRequest)
    {
        String response= aiService.sumUpDocument(aiRequest,userDetails);
        AIResponse aiResponse = new AIResponse(response);
        return ResponseEntity.ok(ApiResponse.onSuccess(aiResponse));
    }
    @GetMapping("/preview")
    @RateLimit(limit = 30, duration = 3600, keyType = RateLimitKeyType.USER)
    public ResponseEntity<ApiResponse<AIResponse>> previewSummary(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestParam AIRequest aiRequest)
    {
        String response = aiService.sumUpDocument(aiRequest,userDetails);
        AIResponse aiResponse = new AIResponse(response);
        return ResponseEntity.ok(ApiResponse.onSuccess(aiResponse));
    }
    @PostMapping("/revise")
    @RateLimit(limit = 30, duration = 3600, keyType = RateLimitKeyType.USER)
    public ResponseEntity<ApiResponse<AIResponse>> reviseSummary(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AIReviseRequest request)
    {
        String response = aiService.reviseSummary(request,userDetails);
        AIResponse aiResponse = new AIResponse(response);
        return ResponseEntity.ok(ApiResponse.onSuccess(aiResponse));
    }
}
