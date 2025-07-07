package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.dto.CategoryDTO;
import com.jarom.funbankapp.dto.CategoryRequest;
import com.jarom.funbankapp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Category management for transactions")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(
        summary = "Get user categories", 
        description = "Retrieves all categories for the authenticated user"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Categories retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getUserCategories(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<CategoryDTO> categories = categoryService.getUserCategories(username);
            return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve categories: " + e.getMessage()));
        }
    }

    @GetMapping("/by-type/{type}")
    @Operation(
        summary = "Get categories by type", 
        description = "Retrieves categories for the authenticated user filtered by type (EXPENSE or INCOME)"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Categories retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid type parameter"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategoriesByType(
            @Parameter(description = "Category type (EXPENSE or INCOME)", example = "EXPENSE")
            @PathVariable String type,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            List<CategoryDTO> categories = categoryService.getUserCategoriesByType(username, type);
            return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve categories: " + e.getMessage()));
        }
    }

    @PostMapping
    @Operation(
        summary = "Create category", 
        description = "Creates a new category for the authenticated user"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Category created successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid category data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CategoryDTO category = categoryService.createCategory(username, request);
            return ResponseEntity.ok(ApiResponse.success("Category created successfully", category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create category: " + e.getMessage()));
        }
    }

    @PutMapping("/{categoryId}")
    @Operation(
        summary = "Update category", 
        description = "Updates an existing category. Cannot update system categories."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Category updated successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid category data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Cannot update system category"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CategoryDTO category = categoryService.updateCategory(username, categoryId, request);
            return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update category: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{categoryId}")
    @Operation(
        summary = "Delete category", 
        description = "Deletes a category. Cannot delete system categories."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Category deleted successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Cannot delete system category"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<String>> deleteCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long categoryId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            categoryService.deleteCategory(username, categoryId);
            return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete category: " + e.getMessage()));
        }
    }

    @GetMapping("/{categoryId}")
    @Operation(
        summary = "Get category by ID", 
        description = "Retrieves a specific category by its ID"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Category retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long categoryId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CategoryDTO category = categoryService.getCategoryById(username, categoryId);
            return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve category: " + e.getMessage()));
        }
    }

    @GetMapping("/system")
    @Operation(
        summary = "Get system categories", 
        description = "Retrieves all system-defined categories"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "System categories retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getSystemCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getSystemCategories();
            return ResponseEntity.ok(ApiResponse.success("System categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve system categories: " + e.getMessage()));
        }
    }
} 