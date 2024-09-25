package com.user.auth.controller;

import com.user.auth.config.AppConstants;
import com.user.auth.payload.ProductDTO;
import com.user.auth.security.response.ProductResponse;
import com.user.auth.security.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {
@Autowired
   private ProductService productService;
@PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO>addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                @PathVariable Long categoryId )
{
    ProductDTO productDTO1=productService.addProduct(categoryId,productDTO);
    return  new ResponseEntity<>(productDTO1, HttpStatus.CREATED);
}
@GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProduct(
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
    @RequestParam(name = "pageSize" , defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_DIR) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortOrder


)
{
    ProductResponse productResponse=productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
    return  new ResponseEntity<>(productResponse,HttpStatus.OK);
}
@GetMapping("/public/categories/{categoryId}/products")
    public  ResponseEntity<ProductResponse>getProductsByCategory(@PathVariable Long categoryId,
                                                            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                            @RequestParam(name = "pageSize" , defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
                                                            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_DIR) String sortBy,
                                                            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortOrder)
{
    ProductResponse productResponse=productService.searchByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
    return  new ResponseEntity<>(productResponse,HttpStatus.OK);
}

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        ProductResponse productResponse = productService.searchProductByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }
    @PutMapping("/admin/products/{productId}")
      public  ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId,
                                                       @RequestBody ProductDTO productdtc)
    {
        ProductDTO productDTO=productService.updateProduct(productId,productdtc);
        return  new ResponseEntity<>(productDTO,HttpStatus.OK);
    }
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO deletedProduct = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }
}
