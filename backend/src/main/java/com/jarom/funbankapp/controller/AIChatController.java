package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/ai")
@io.swagger.v3.oas.annotations.tags.Tag(name = "AI Chat", description = "AI-powered financial advice and chat")
public class AIChatController {

    private final UserDAO userDAO;

    public AIChatController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PostMapping("/chat")
    @Operation(summary = "Chat with AI", description = "Send a message to the AI for financial advice.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "AI response generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<?> chatWithAI(@RequestBody ChatRequest request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        // Simple AI response logic (placeholder for OpenAI integration)
        String response = generateAIResponse(request.getMessage(), user.getId());
        
        ChatResponse chatResponse = new ChatResponse(response, System.currentTimeMillis());
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get AI recommendations", description = "Get personalized financial recommendations from AI.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully")
    })
    public ResponseEntity<?> getRecommendations() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        // Generate personalized recommendations (placeholder)
        Map<String, String> recommendations = generateRecommendations(user.getId());
        
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/spending-patterns")
    @Operation(summary = "Analyze spending patterns", description = "Get AI analysis of spending patterns.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Spending analysis retrieved successfully")
    })
    public ResponseEntity<?> analyzeSpendingPatterns() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        // Analyze spending patterns (placeholder)
        Map<String, Object> analysis = analyzeSpending(user.getId());
        
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/anomaly-detection")
    @Operation(summary = "Detect anomalies", description = "Get AI analysis for unusual spending patterns.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anomaly detection completed successfully")
    })
    public ResponseEntity<?> detectAnomalies() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        // Detect anomalies (placeholder)
        Map<String, Object> anomalies = detectAnomalies(user.getId());
        
        return ResponseEntity.ok(anomalies);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Placeholder methods for AI functionality
    private String generateAIResponse(String message, Long userId) {
        // This would integrate with OpenAI API in a real implementation
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("budget") || lowerMessage.contains("spending")) {
            return "Based on your spending patterns, I recommend setting up a monthly budget and tracking your expenses. Consider using the 50/30/20 rule: 50% for needs, 30% for wants, and 20% for savings.";
        } else if (lowerMessage.contains("save") || lowerMessage.contains("savings")) {
            return "Great question about savings! I suggest starting with an emergency fund of 3-6 months of expenses. Then focus on saving 20% of your income for long-term goals.";
        } else if (lowerMessage.contains("invest") || lowerMessage.contains("investment")) {
            return "For investments, consider starting with a diversified portfolio. Look into index funds or ETFs for broad market exposure. Remember to consider your risk tolerance and time horizon.";
        } else if (lowerMessage.contains("debt") || lowerMessage.contains("loan")) {
            return "When dealing with debt, prioritize high-interest debt first (like credit cards). Consider the debt snowball or avalanche method for repayment strategies.";
        } else {
            return "I'm here to help with your financial questions! You can ask me about budgeting, saving, investing, debt management, or any other financial topics. What specific area would you like to discuss?";
        }
    }

    private Map<String, String> generateRecommendations(Long userId) {
        Map<String, String> recommendations = new HashMap<>();
        recommendations.put("budget", "Consider creating a monthly budget to track your spending");
        recommendations.put("savings", "Aim to save 20% of your income for future goals");
        recommendations.put("emergency_fund", "Build an emergency fund of 3-6 months of expenses");
        recommendations.put("investments", "Start investing early to take advantage of compound interest");
        return recommendations;
    }

    private Map<String, Object> analyzeSpending(Long userId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("total_spent", 0.0);
        analysis.put("top_categories", new String[]{"Food", "Transportation", "Entertainment"});
        analysis.put("trend", "Your spending has been consistent this month");
        analysis.put("savings_potential", "You could save $200 more by reducing dining out");
        return analysis;
    }

    private Map<String, Object> detectAnomalies(Long userId) {
        Map<String, Object> anomalies = new HashMap<>();
        anomalies.put("detected", false);
        anomalies.put("message", "No unusual spending patterns detected");
        anomalies.put("last_check", System.currentTimeMillis());
        return anomalies;
    }

    // Request and Response classes
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
} 