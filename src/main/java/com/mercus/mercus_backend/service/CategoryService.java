package com.mercus.mercus_backend.service;

import com.mercus.mercus_backend.model.Category;
import com.mercus.mercus_backend.payload.CategoryDTO;
import com.mercus.mercus_backend.payload.CategoryResponse;

import java.util.List;

public interface CategoryService
{
    CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO,Long categoryId);
}
