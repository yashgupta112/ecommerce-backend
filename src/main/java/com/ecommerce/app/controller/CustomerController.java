package com.ecommerce.app.controller;

import com.ecommerce.app.model.*;
import com.ecommerce.app.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // View products by category
    @GetMapping("/products/{category}")
    public ResponseEntity<List<Product>> viewProducts(@PathVariable String category) {
        List<Product> products = customerService.viewProducts(category);
        return ResponseEntity.ok(products);
    }

    // Add product to cart
    @PostMapping("/cart/add")
    public ResponseEntity<Cart> addToCart(@RequestParam Long customerId, @RequestParam Long productId, @RequestParam int quantity) {
        User customer = new User();  // Fetch customer by ID from UserService (assumed)
        customer.setId(customerId);
        Cart cart = customerService.addToCart(customer, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    // View cart items
    @GetMapping("/cart")
    public ResponseEntity<List<Cart>> viewCart(@RequestParam Long customerId) {
        User customer = new User();  // Fetch customer by ID
        customer.setId(customerId);
        List<Cart> cartItems = customerService.viewCart(customer);
        return ResponseEntity.ok(cartItems);
    }

    // Remove item from cart
    @DeleteMapping("/cart/remove/{cartId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartId) {
        customerService.removeFromCart(cartId);
        return ResponseEntity.ok().build();
    }

    // Place an order
    @PostMapping("/order/place")
    public ResponseEntity<Order> placeOrder(@RequestParam Long customerId, @RequestParam String shippingAddress, @RequestParam String phone) {
        User customer = new User();  // Fetch customer by ID
        customer.setId(customerId);
        Order order = customerService.placeOrder(customer, shippingAddress, phone);
        return ResponseEntity.ok(order);
    }

    // View order history
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> viewOrderHistory(@RequestParam Long customerId) {
        User customer = new User();  // Fetch customer by ID
        customer.setId(customerId);
        List<Order> orders = customerService.viewOrderHistory(customer);
        return ResponseEntity.ok(orders);
    }
}
