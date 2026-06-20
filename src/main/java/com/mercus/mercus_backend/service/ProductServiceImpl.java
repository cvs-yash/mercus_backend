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

@Service
public class ProductServiceImpl implements  ProductService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ProductDTO addProduct(Long categoryId,  ProductDTO productDTO) {
        Category category= categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","Category ID not found",categoryId));

        Product product = modelMapper.map(productDTO , Product.class);
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

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category= categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));
        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {
        List<Product>  products  = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%');
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;

    }

    @Override
    public ProductDTO updateProduct( ProductDTO productDTO, Long productId) {
        //get the existing products from the db
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        Product product = modelMapper.map(productDTO, Product.class);
        //update the product info the one in request body
        productFromDB.setProductName(product.getProductName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setQuantity(product.getQuantity());
        productFromDB.setDiscount(product.getDiscount());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setSpecialPrice(product.getSpecialPrice());

        //save to db
         Product savedProduct =  productRepository.save(productFromDB);
         return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("product","productId",productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }


}
