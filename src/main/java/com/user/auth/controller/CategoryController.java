package com.user.auth.controller;

import com.user.auth.config.AppConfig;
import com.user.auth.config.AppConstants;
import com.user.auth.payload.CategoryDTO;
import com.user.auth.security.response.CategoryResponse;
import com.user.auth.security.services.CategoryService;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

  @GetMapping("/public/categories")
  public  ResponseEntity<CategoryResponse> getAllCategories(
          @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
          @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
          @RequestParam(name = "sortBy" ,defaultValue = AppConstants.SORT_CATEGORIES_BY,required = false) String sortBy,
          @RequestParam(name = "sortOrder" , defaultValue = AppConstants.SORT_DIR,required = false)String sortOrder

  )
  {
      CategoryResponse categoryResponse=categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
      return  new ResponseEntity<CategoryResponse>(categoryResponse,HttpStatus.OK);

  }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO)
    {
      CategoryDTO savedCategoryDTO=  categoryService.createCategory(categoryDTO);
      return new  ResponseEntity<CategoryDTO>(savedCategoryDTO, HttpStatus.CREATED);
    }

     @DeleteMapping("/admin/categories/{categoryId}")
     public  ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId)
     {
         CategoryDTO categoryDTO=categoryService.deleteCategory(categoryId);
         return  new ResponseEntity<>(categoryDTO,HttpStatus.OK);
     }
     @PutMapping("/public/categories/{categoryId}")
    public  ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                            @PathVariable Long categoryId

                                                       )
     {
         CategoryDTO categoryDTO1=categoryService.updateCategory(categoryDTO,categoryId);
         return  new ResponseEntity<>(categoryDTO1,HttpStatus.OK);
     }


}
