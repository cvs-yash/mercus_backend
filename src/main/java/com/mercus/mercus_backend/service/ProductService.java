package com.mercus.mercus_backend.service;

import com.mercus.mercus_backend.model.Product;
import com.mercus.mercus_backend.payload.ProductDTO;
import com.mercus.mercus_backend.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, Product product);

    ProductResponse getAllProducts();
}
