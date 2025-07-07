package com.jarom.funbankapp.model;

import java.time.LocalDateTime;

public class Category {
    private Long id;
    private Long userId;
    private String name;
    private String type; // EXPENSE, INCOME
    private Long parentId;
    private String color;
    private String icon;
    private Boolean isSystem;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Category() {}

    // Constructor with required fields
    public Category(Long userId, String name, String type) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.isSystem = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", parentId=" + parentId +
                ", color='" + color + '\'' +
                ", icon='" + icon + '\'' +
                ", isSystem=" + isSystem +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 