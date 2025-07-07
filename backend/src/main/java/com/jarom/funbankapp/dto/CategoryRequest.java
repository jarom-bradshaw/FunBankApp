package com.jarom.funbankapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    
    @NotBlank(message = "Category type is required")
    @Pattern(regexp = "^(EXPENSE|INCOME)$", message = "Type must be either EXPENSE or INCOME")
    private String type;
    
    private Long parentId;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color (e.g., #FF0000)")
    private String color;
    
    private String icon;

    // Default constructor
    public CategoryRequest() {}

    // Constructor with required fields
    public CategoryRequest(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Getters and Setters
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
} 