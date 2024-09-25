package com.user.auth.repositorys;

import com.user.auth.model.Category;
import com.user.auth.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
Page<Product>findByCategoryOrderByPriceAsc(Category category, Pageable pageable);
Page<Product> findByProductNameLikeIgnoreCase(String keyword,Pageable pageable);
}
