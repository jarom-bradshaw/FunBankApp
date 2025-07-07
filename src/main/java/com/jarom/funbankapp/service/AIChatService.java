package com.jarom.funbankapp.service;

import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AIChatService {

    private final UserRepository userRepository;

    public AIChatService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Process chat message and generate AI response
     */
    public ChatResponse chatWithAI(ChatRequest request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate and sanitize the message
        String sanitizedMessage = sanitizeMessage(request.getMessage());
        
        // Generate AI response (placeholder for OpenAI integration)
        String response = generateAIResponse(sanitizedMessage, user.getId());
        
        return new ChatResponse(response, System.currentTimeMillis());
    }

    /**
     * Get chat history for the user
     */
    public ChatHistory getChatHistory() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // TODO: Implement chat history retrieval from database
        // For now, return empty history
        return new ChatHistory(user.getId(), new HashMap<>());
    }

    /**
     * Save chat message to history
     */
    public void saveChatMessage(String message, String response) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // TODO: Implement chat history saving to database
        // This would store the message, response, and timestamp
    }

    // Private helper methods

    private String sanitizeMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        
        String sanitized = message.trim();
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000);
        }
        
        // Basic content filtering
        sanitized = sanitized.replaceAll("<script.*?</script>", ""); // Remove script tags
        sanitized = sanitized.replaceAll("javascript:", ""); // Remove javascript protocol
        
        return sanitized;
    }

    private String generateAIResponse(String message, Long userId) {
        // Placeholder for OpenAI integration
        // In a real implementation, this would call the OpenAI API
        
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("budget") || lowerMessage.contains("spending")) {
            return "I can help you with budgeting! Based on your recent transactions, I recommend setting up spending limits for different categories. Would you like me to analyze your spending patterns?";
        } else if (lowerMessage.contains("save") || lowerMessage.contains("investment")) {
            return "Great question about saving! I suggest starting with an emergency fund of 3-6 months of expenses, then consider investing in a diversified portfolio. Would you like me to help you set up a savings goal?";
        } else if (lowerMessage.contains("debt") || lowerMessage.contains("loan")) {
            return "Managing debt is important! I recommend paying off high-interest debt first, then focusing on building savings. Would you like me to help you create a debt repayment plan?";
        } else if (lowerMessage.contains("goal") || lowerMessage.contains("target")) {
            return "Setting financial goals is a great way to stay motivated! I can help you track your progress towards savings goals, debt payoff, or investment targets. What type of goal would you like to set?";
        } else {
            return "I'm here to help with your financial questions! I can assist with budgeting, saving, investing, debt management, and goal setting. What would you like to know more about?";
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Inner classes for request/response data

    public static class ChatRequest {
        private String message;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ChatResponse {
        private String response;
        private long timestamp;

        public ChatResponse(String response, long timestamp) {
            this.response = response;
            this.timestamp = timestamp;
        }

        public String getResponse() { return response; }
        public long getTimestamp() { return timestamp; }
    }

    public static class ChatHistory {
        private final Long userId;
        private final Map<LocalDateTime, ChatMessage> messages;

        public ChatHistory(Long userId, Map<LocalDateTime, ChatMessage> messages) {
            this.userId = userId;
            this.messages = messages;
        }

        public Long getUserId() { return userId; }
        public Map<LocalDateTime, ChatMessage> getMessages() { return messages; }
    }

    public static class ChatMessage {
        private final String userMessage;
        private final String aiResponse;
        private final LocalDateTime timestamp;

        public ChatMessage(String userMessage, String aiResponse, LocalDateTime timestamp) {
            this.userMessage = userMessage;
            this.aiResponse = aiResponse;
            this.timestamp = timestamp;
        }

        public String getUserMessage() { return userMessage; }
        public String getAiResponse() { return aiResponse; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
} 