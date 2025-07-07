package com.jarom.funbankapp.service;

import com.jarom.funbankapp.dto.CategoryDTO;
import com.jarom.funbankapp.dto.CategoryRequest;
import com.jarom.funbankapp.model.Category;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.CategoryRepository;
import com.jarom.funbankapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<CategoryDTO> getUserCategories(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Category> categories = categoryRepository.findByUserId(user.getId());
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getUserCategoriesByType(String username, String type) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Category> categories = categoryRepository.findByUserIdAndType(user.getId(), type);
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO createCategory(String username, CategoryRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = new Category();
        category.setUserId(user.getId());
        category.setName(request.getName());
        category.setType(request.getType());
        category.setParentId(request.getParentId());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        category.setIsSystem(false);
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    public CategoryDTO updateCategory(String username, Long categoryId, CategoryRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Don't allow updating system categories
        if (category.getIsSystem()) {
            throw new RuntimeException("Cannot update system categories");
        }
        
        category.setName(request.getName());
        category.setType(request.getType());
        category.setParentId(request.getParentId());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        
        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    public void deleteCategory(String username, Long categoryId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Don't allow deleting system categories
        if (category.getIsSystem()) {
            throw new RuntimeException("Cannot delete system categories");
        }
        
        categoryRepository.deleteById(categoryId);
    }

    public CategoryDTO getCategoryById(String username, Long categoryId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        return convertToDTO(category);
    }

    public List<CategoryDTO> getSystemCategories() {
        List<Category> categories = categoryRepository.findSystemCategories();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setType(category.getType());
        dto.setParentId(category.getParentId());
        dto.setColor(category.getColor());
        dto.setIcon(category.getIcon());
        dto.setIsSystem(category.getIsSystem());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
} 