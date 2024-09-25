package com.user.auth.security.services;

import com.user.auth.payload.CategoryDTO;
import com.user.auth.security.response.CategoryResponse;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,
                                      String sortBy,
                                      String sortOrder);

    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
