package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setUserId(rs.getLong("user_id"));
        category.setName(rs.getString("name"));
        category.setType(rs.getString("type"));
        category.setParentId(rs.getLong("parent_id"));
        category.setColor(rs.getString("color"));
        category.setIcon(rs.getString("icon"));
        category.setIsSystem(rs.getBoolean("is_system"));
        category.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        category.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return category;
    };

    public CategoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Category> findByUserId(Long userId) {
        String sql = "SELECT * FROM categories WHERE user_id = ? ORDER BY name";
        return jdbcTemplate.query(sql, categoryRowMapper, userId);
    }

    @Override
    public List<Category> findByUserIdAndType(Long userId, String type) {
        String sql = "SELECT * FROM categories WHERE user_id = ? AND type = ? ORDER BY name";
        return jdbcTemplate.query(sql, categoryRowMapper, userId, type);
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        List<Category> categories = jdbcTemplate.query(sql, categoryRowMapper, id);
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    @Override
    public Optional<Category> findByIdAndUserId(Long id, Long userId) {
        String sql = "SELECT * FROM categories WHERE id = ? AND user_id = ?";
        List<Category> categories = jdbcTemplate.query(sql, categoryRowMapper, id, userId);
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            return insert(category);
        } else {
            return update(category);
        }
    }

    private Category insert(Category category) {
        String sql = "INSERT INTO categories (user_id, name, type, parent_id, color, icon, is_system, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, category.getUserId());
            ps.setString(2, category.getName());
            ps.setString(3, category.getType());
            ps.setObject(4, category.getParentId());
            ps.setString(5, category.getColor());
            ps.setString(6, category.getIcon());
            ps.setBoolean(7, category.getIsSystem() != null ? category.getIsSystem() : false);
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        
        category.setId(keyHolder.getKey().longValue());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        return category;
    }

    private Category update(Category category) {
        String sql = "UPDATE categories SET name = ?, type = ?, parent_id = ?, color = ?, icon = ?, updated_at = ? " +
                    "WHERE id = ? AND user_id = ?";
        
        int updatedRows = jdbcTemplate.update(sql,
                category.getName(),
                category.getType(),
                category.getParentId(),
                category.getColor(),
                category.getIcon(),
                LocalDateTime.now(),
                category.getId(),
                category.getUserId()
        );
        
        if (updatedRows == 0) {
            throw new RuntimeException("Category not found or not owned by user");
        }
        
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        String sql = "SELECT COUNT(*) FROM categories WHERE id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id, userId);
        return count != null && count > 0;
    }

    @Override
    public List<Category> findSystemCategories() {
        String sql = "SELECT * FROM categories WHERE is_system = true ORDER BY name";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }
} 