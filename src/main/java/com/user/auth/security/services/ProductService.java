package com.user.auth.security.services;

import com.user.auth.payload.ProductDTO;
import com.user.auth.security.response.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);
    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse searchByCategory(Long categoryId,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse searchProductByKeyword(String keyword,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(Long productId, ProductDTO product);

    ProductDTO deleteProduct(Long productId);
}
