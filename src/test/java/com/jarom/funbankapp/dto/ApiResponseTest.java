package com.jarom.funbankapp.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class ApiResponseTest {

    @Test
    void testSuccessResponse_WithMessageAndData() {
        String testData = "test data";
        ApiResponse<String> response = ApiResponse.success("Operation successful", testData);
        
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Operation successful", response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
        assertNull(response.getError());
        assertNull(response.getErrors());
    }

    @Test
    void testSuccessResponse_WithDataOnly() {
        Integer testData = 42;
        ApiResponse<Integer> response = ApiResponse.success(testData);
        
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Operation completed successfully", response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
        assertNull(response.getError());
        assertNull(response.getErrors());
    }

    @Test
    void testSuccessResponse_WithPagination() {
        String testData = "test data";
        ApiResponse<String> response = ApiResponse.success("Operation successful", testData, 100, 0, 10);
        
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Operation successful", response.getMessage());
        assertEquals(testData, response.getData());
        assertEquals(100, response.getTotalCount());
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testErrorResponse_WithErrorOnly() {
        ApiResponse<String> response = ApiResponse.error("Something went wrong");
        
        assertEquals("ERROR", response.getStatus());
        assertEquals("Something went wrong", response.getError());
        assertNull(response.getData());
        assertNull(response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testErrorResponse_WithErrorAndDetails() {
        java.util.List<String> errors = java.util.List.of("Field 1 is required", "Field 2 is invalid");
        ApiResponse<String> response = ApiResponse.error("Validation failed", errors);
        
        assertEquals("ERROR", response.getStatus());
        assertEquals("Validation failed", response.getError());
        assertEquals(errors, response.getErrors());
        assertNull(response.getData());
        assertNull(response.getMessage());
        assertNotNull(response.getTimestamp());
    }
} 