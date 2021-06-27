package com.maersk.shoppingcart.service;

import com.maersk.shoppingcart.entity.Product;
import com.maersk.shoppingcart.jpa.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sumit Kumar
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductById(Integer productId) {
        return productRepository.findById(productId).orElse(null);
    }
}
