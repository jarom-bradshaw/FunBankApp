package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-chat")
public class AIChatController {

    private final UserRepository userRepository;

    public AIChatController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<Map<String, String>>> chat(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Message is required"));
            }

            // Simple AI response logic (placeholder)
            String aiResponse = "Hello " + user.getFirstName() + "! I understand you said: " + message + 
                              ". This is a placeholder AI response. In a real implementation, this would connect to an AI service.";

            Map<String, String> response = new HashMap<>();
            response.put("message", aiResponse);
            response.put("userMessage", message);

            return ResponseEntity.ok(ApiResponse.success("AI response generated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to process chat request: " + e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> getChatStatus(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(ApiResponse.success("AI chat is available", "AI chat service is running"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get chat status: " + e.getMessage()));
        }
    }
} 