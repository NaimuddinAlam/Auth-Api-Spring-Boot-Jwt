package com.user.auth.security.services.impl;

import com.user.auth.exceptions.APIException;
import com.user.auth.exceptions.ResourceNotFoundException;
import com.user.auth.model.Category;
import com.user.auth.model.Product;
import com.user.auth.payload.ProductDTO;
import com.user.auth.repositorys.CategoryRepository;
import com.user.auth.repositorys.ProductRepository;
import com.user.auth.security.response.ProductResponse;
import com.user.auth.security.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private  ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;;
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product product : products) {
            if (product.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if (isProductNotPresent) {
            Product product1 = modelMapper.map(productDTO, Product.class);
            product1.setImage("default.png");
            product1.setCategory(category);
            double specialPrice = product1.getPrice() - (product1.getDiscount() * 0.01 * product1.getPrice());
            product1.setSpecialPrice(specialPrice);
            Product saveProduct = productRepository.save(product1);
            return modelMapper.map(saveProduct, ProductDTO.class);

        } else {
            throw new APIException("Product already exist!!");
        }

    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
    Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
     ?Sort.by(sortBy).ascending()
            :Sort.by(sortBy).descending();

        Pageable pageable= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts=productRepository.findAll(pageable);
        List<Product> productList=pageProducts.getContent();
      List<ProductDTO> productDTOS=productList.stream()
              .map(product -> modelMapper.map(pageProducts, ProductDTO.class))
              .toList();

      ProductResponse productResponse= new ProductResponse();
      productResponse.setContent(productDTOS);
      productResponse.setPageNumber(pageable.getPageNumber());
      productResponse.setPageSize(pageable.getPageSize());
      productResponse.setTotalPages(pageProducts.getTotalPages());
      productResponse.setLastPage(pageProducts.isLast());
      productResponse.setTotalElements(pageProducts.getTotalElements());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
       Category category=categoryRepository.findById(categoryId)
               .orElseThrow(()->  new ResourceNotFoundException("Category","CategoryId",categoryId));

    Sort sortByAndOrder=sortOrder.equalsIgnoreCase("ase")
            ?Sort.by(sortBy).ascending()
            :Sort.by(sortBy).descending();
    Pageable pageable =  PageRequest.of(pageNumber,pageSize,sortByAndOrder);
  Page<Product> pageProducts=productRepository.findByCategoryOrderByPriceAsc(category,pageable);
   List<Product> products=pageProducts.getContent();
   if(products.isEmpty())
   {
       throw  new APIException(category.getCategoryName()+ " category does not have any products");
   }
   List<ProductDTO> productDTOS=products.stream()
           .map(product -> modelMapper.map(products,ProductDTO.class)).toList();
   ProductResponse productResponse= new ProductResponse();
   productResponse.setContent(productDTOS);
   productResponse.setPageSize(pageProducts.getSize());
   productResponse.setTotalPages(pageProducts.getTotalPages());
   productResponse.setPageNumber(pageProducts.getTotalPages());
   productResponse.setTotalElements(pageProducts.getTotalElements());
   productResponse.setLastPage(pageProducts.isLast());


        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortOrder).descending();
        Pageable pageable=PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts=productRepository.findByProductNameLikeIgnoreCase(keyword,pageable);
        List<Product> product=pageProducts.getContent();
        List<ProductDTO> productList=product.stream()
                .map(products->modelMapper.map(product,ProductDTO.class)).toList();

        if(product.isEmpty()){
            throw new APIException("Products not found with keyword: " + keyword);
        }
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productList);
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setPageNumber(pageProducts.getTotalPages());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO product) {
        Product productFromDb=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","ProductId",productId));

        Product product1=modelMapper.map(product,Product.class);
        productFromDb.setProductName(product1.getProductName());
        productFromDb.setDescription(product1.getDescription());
        productFromDb.setQuantity(product1.getQuantity());
        productFromDb.setPrice(product1.getPrice());
        productFromDb.setSpecialPrice(product1.getSpecialPrice());
        Product savedProduct=productRepository.save(productFromDb);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","ProductId",productId));

       productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }
}


