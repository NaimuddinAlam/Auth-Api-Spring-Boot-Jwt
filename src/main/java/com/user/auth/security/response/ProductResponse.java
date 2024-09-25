package com.user.auth.security.response;

import com.user.auth.payload.CategoryDTO;
import com.user.auth.payload.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponse {
    private List<ProductDTO> content;
    private Integer pageNumber;
    private  Integer pageSize;
    private  Long totalElements;
    private  Integer totalPages;
    private  boolean lastPage;
}
