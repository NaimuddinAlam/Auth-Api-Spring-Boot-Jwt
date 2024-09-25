package com.user.auth.security.response;

import com.user.auth.payload.CategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private List<CategoryDTO> content;
    private Integer pageNumber;
    private  Integer pageSize;
    private  Long totalElements;
    private  Integer totalPages;
    private  boolean lastPage;

}
