package com.ecommerce.app.controller;

import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.model.Cart;
import com.ecommerce.app.model.Order;
import com.ecommerce.app.model.Product;
import com.ecommerce.app.model.User;
import com.ecommerce.app.service.CustomerService;
import com.ecommerce.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = customerService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = customerService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/cart")
    public ResponseEntity<List<Cart>> getCartItems(Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Cart> cartItems = customerService.getCartItems(user);
        return ResponseEntity.ok(cartItems);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/cart")
    public ResponseEntity<Cart> addToCart(Principal principal, @RequestParam Long productId, @RequestParam Integer quantity) {
        User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cartItem = customerService.addToCart(user.getId(), productId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/cart/{cartItemId}")
    public ResponseEntity<Cart> updateCartItem(Principal principal, @PathVariable Long cartItemId, @RequestParam Integer quantity) {
        User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart updatedCartItem = customerService.updateCartItem(cartItemId, quantity);
        return ResponseEntity.ok(updatedCartItem);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/cart/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(Principal principal, @PathVariable Long cartItemId) {
        User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        customerService.removeFromCart(cartItemId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Order> orders = customerService.getOrders(user);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(Principal principal, @RequestParam String shippingAddress, @RequestParam String phone) {
        User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = customerService.placeOrder(user.getId(), shippingAddress, phone);
        return ResponseEntity.ok(order);
    }
}
