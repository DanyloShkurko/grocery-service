package com.example.ProductService.service;

import com.example.ProductService.model.ProductRequest;
import com.example.ProductService.model.ProductResponse;

public interface ProductService {
    long saveProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long id, long quantity);
}
