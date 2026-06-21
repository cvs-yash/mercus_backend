package com.mercus.mercus_backend.service;


import com.mercus.mercus_backend.exception.APIException;
import com.mercus.mercus_backend.exception.ResourceNotFoundException;
import com.mercus.mercus_backend.model.Category;
import com.mercus.mercus_backend.model.Product;
import com.mercus.mercus_backend.payload.ProductDTO;
import com.mercus.mercus_backend.payload.ProductResponse;
import com.mercus.mercus_backend.repository.CategoryRepository;
import com.mercus.mercus_backend.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements  ProductService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FIleService fIleService;

    @Value("${project.image}")
    private String path;


    @Override
    public ProductDTO addProduct(Long categoryId,  ProductDTO productDTO) {
        Category category= categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","Category ID not found",categoryId));


        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();

        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }

        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);

            product.setImage("default.png");
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }else{
            throw new APIException("Product already exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize , String sortBy , String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No product Create till now!!!");

        }
         List<ProductDTO> productDTOS = products.stream()
                 .map(product -> modelMapper.map(product, ProductDTO.class))
                 .toList();

         ProductResponse productResponse= new ProductResponse();
         productResponse.setContent(productDTOS);
         productResponse.setPageNumber(productPage.getNumber());
         productResponse.setPageSize(productPage.getSize());
         productResponse.setTotalElements(productPage.getTotalElements());
         productResponse.setTotalPages(productPage.getTotalPages());
         productResponse.setLastPage(productPage.isLast());

         return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category= categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);
        List<Product> products = productPage.getContent();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        if(products.isEmpty()){
            throw new APIException(category.getCategoryName()+" category does not have any products ");
        }
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageDetails);



        List<Product>  products  = productPage.getContent();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        if(products.isEmpty()){
            throw new APIException("Products not found with the keyword: "+keyword);
        }

        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
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

    @Override
    public ProductDTO updateProudctImage(Long productId, MultipartFile image) throws IOException {
        //get the product from the image
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","ProductId",productId));
        //Upload image to the server  (slash directory)
        //get the file name of uploaded image

        String fileName = fIleService.uploadImage(path,image);

        //Updating the new file name to the product
        productFromDb.setImage(fileName);

        //save the updated product
        Product updatedProduct = productRepository.save(productFromDb);
        //return dto after mapping the product to the DTO
        return modelMapper.map(updatedProduct,ProductDTO.class);



    }




}
