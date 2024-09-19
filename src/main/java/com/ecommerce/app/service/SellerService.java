package com.ecommerce.app.service;

import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.model.Product;
import com.ecommerce.app.model.User;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Product> getAllProducts(User seller) {
        return productRepository.findBySeller(seller);
    }

    public Product getProductById(Long sellerId, Long productId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("Product does not belong to the seller");
        }
        return product;
    }

    public Product addProduct(Long sellerId, Product product) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        product.setSeller(seller);
        return productRepository.save(product);
    }

    public Product updateProduct(Long sellerId, Long productId, Product updatedProduct) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("Product does not belong to the seller");
        }

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setCategory(updatedProduct.getCategory());
        product.setPrice(updatedProduct.getPrice());
        product.setStock(updatedProduct.getStock());
        return productRepository.save(product);
    }

    public void deleteProduct(Long sellerId, Long productId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("Product does not belong to the seller");
        }

        productRepository.delete(product);
    }
}
