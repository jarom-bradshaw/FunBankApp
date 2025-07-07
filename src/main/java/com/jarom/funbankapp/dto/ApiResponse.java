package com.jarom.funbankapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private Integer totalCount;
    private Integer page;
    private Integer size;
    private String error;
    private List<String> errors;

    // Success constructors
    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(String status, String message, T data, Integer totalCount, Integer page, Integer size) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.timestamp = LocalDateTime.now();
    }

    // Error constructor
    public ApiResponse(String status, String error, List<String> errors) {
        this.status = status;
        this.error = error;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    // Static factory methods for success responses
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> success(String message, T data, Integer totalCount, Integer page, Integer size) {
        return new ApiResponse<>("SUCCESS", message, data, totalCount, page, size);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", "Operation completed successfully", data);
    }

    // Static factory methods for error responses
    public static <T> ApiResponse<T> error(String error, List<String> errors) {
        return new ApiResponse<>("ERROR", error, errors);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>("ERROR", error, null);
    }

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
} 