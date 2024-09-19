package com.ecommerce.app.controller;

import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.model.Product;
import com.ecommerce.app.model.User;
import com.ecommerce.app.service.SellerService;
import com.ecommerce.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private UserService userService;

    
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(Principal principal) {
        User seller = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        List<Product> products = sellerService.getAllProducts(seller);
        return ResponseEntity.ok(products);
    }

    
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(Principal principal, @RequestBody Product product) {
        User seller = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Product createdProduct = sellerService.addProduct(seller.getId(), product);
        return ResponseEntity.ok(createdProduct);
    }

    
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(Principal principal, @PathVariable Long productId) {
        User seller = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Product product = sellerService.getProductById(seller.getId(), productId);
        return ResponseEntity.ok(product);
    }
    

    
    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(Principal principal, @PathVariable Long productId, @RequestBody Product product) {
        User seller = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Product updatedProduct = sellerService.updateProduct(seller.getId(), productId, product);
        return ResponseEntity.ok(updatedProduct);
    }

    
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(Principal principal, @PathVariable Long productId) {
        User seller = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        sellerService.deleteProduct(seller.getId(), productId);
        return ResponseEntity.ok().build();
    }
}
