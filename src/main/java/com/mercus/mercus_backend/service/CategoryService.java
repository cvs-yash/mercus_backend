package com.mercus.mercus_backend.service;

import com.mercus.mercus_backend.model.Category;
import com.mercus.mercus_backend.payload.CategoryResponse;

import java.util.List;

public interface CategoryService
{
    CategoryResponse getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);
    Category updateCategory(Category category,Long categoryId);
}
