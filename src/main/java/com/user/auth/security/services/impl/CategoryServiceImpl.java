package com.user.auth.security.services.impl;

import com.user.auth.exceptions.APIException;
import com.user.auth.exceptions.ResourceNotFoundException;
import com.user.auth.model.Category;
import com.user.auth.payload.CategoryDTO;
import com.user.auth.repositorys.CategoryRepository;
import com.user.auth.security.response.CategoryResponse;
import com.user.auth.security.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.TypeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category=modelMapper.map(categoryDTO,Category.class);
       Category categoryFromDb= categoryRepository.findByCategoryName(category.getCategoryName());
       if(categoryFromDb!=null)
         throw  new APIException("Category with the name" +category.getCategoryName()+"  already exists !!!");
      Category saveCategory=  categoryRepository.save(category);

        return modelMapper.map(saveCategory,CategoryDTO.class);
    }

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
     Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
             ? Sort.by(sortBy).ascending()
             :Sort.by(sortBy).descending();
Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage=categoryRepository.findAll(pageDetails);
        List<Category> categories=categoryPage.getContent();
        if(categories.isEmpty())
            throw  new APIException("No category created till now.");
   List<CategoryDTO> categoryDTOS=categories.stream()
           .map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse= new CategoryResponse();
       categoryResponse.setContent(categoryDTOS);
       categoryResponse.setPageNumber(categoryPage.getNumber());
       categoryResponse.setTotalPages(categoryPage.getTotalPages());
       categoryResponse.setPageSize(categoryPage.getSize());
       categoryResponse.setPageSize(categoryPage.getSize());
       categoryResponse.setTotalElements(categoryPage.getTotalElements());
       categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category=modelMapper.map(categoryId,Category.class);
       Category categoryfindid=categoryRepository.findById(category.getCategoryId())
               .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));
    categoryRepository.delete(categoryfindid);
        return modelMapper.map(categoryfindid, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category=modelMapper.map(categoryId,Category.class);
        Category categoryfindid=categoryRepository.findById(category.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));
        categoryfindid.setCategoryId(categoryId);
        categoryRepository.save(categoryfindid);
        return modelMapper.map(categoryfindid, CategoryDTO.class);
    }
}
