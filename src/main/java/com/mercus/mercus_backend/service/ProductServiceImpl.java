package com.mercus.mercus_backend.service;


import com.mercus.mercus_backend.exception.ResourceNotFoundException;
import com.mercus.mercus_backend.model.Category;
import com.mercus.mercus_backend.model.Product;
import com.mercus.mercus_backend.payload.ProductDTO;
import com.mercus.mercus_backend.payload.ProductResponse;
import com.mercus.mercus_backend.repository.CategoryRepository;
import com.mercus.mercus_backend.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements  ProductService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ProductDTO addProduct(Long categoryId, Product product) {
        Category category= categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","Category ID not found",categoryId));
        product.setCategory(category);

        product.setImage("default.png");
        double specialPrice = product.getPrice() - ((product.getDiscount()*0.01)*product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
         List<Product>  products  = productRepository.findAll();
         List<ProductDTO> productDTOS = products.stream()
                 .map(product -> modelMapper.map(product, ProductDTO.class))
                 .toList();
         ProductResponse productResponse= new ProductResponse();
         productResponse.setContent(productDTOS);
         return productResponse;
    }
}
