package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findByUserId(Long userId);
    List<Category> findByUserIdAndType(Long userId, String type);
    Optional<Category> findById(Long id);
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    Category save(Category category);
    void deleteById(Long id);
    boolean existsByIdAndUserId(Long id, Long userId);
    List<Category> findSystemCategories();
} 