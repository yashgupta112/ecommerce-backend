package com.ecommerce.app.service;

import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.model.*;
import com.ecommerce.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    // View products by category
    public List<Product> viewProducts(String category) {
        List<Product> products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found in category: " + category);
        }
        return products;
    }

    // Add product to cart
    public Cart addToCart(User customer, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    // View cart items
    public List<Cart> viewCart(User customer) {
        List<Cart> cartItems = cartRepository.findByCustomer(customer);
        return cartItems.isEmpty() ? List.of() : cartItems;  // Return empty list if cart is empty
    }

    // Remove item from cart
    public void removeFromCart(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new ResourceNotFoundException("Cart item not found with ID: " + cartId);
        }
        cartRepository.deleteById(cartId);
    }

    // Place an order
    public Order placeOrder(User customer, String shippingAddress, String phone) {
        List<Cart> cartItems = cartRepository.findByCustomer(customer);
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("No items in the cart.");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(new Date());
        order.setShippingAddress(shippingAddress);
        order.setPhone(phone);
        
        double totalAmount = 0.0;
        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
            order.getOrderItems().add(orderItem);
            totalAmount += orderItem.getPrice();
        }
        
        order.setTotalAmount(totalAmount);
        cartRepository.deleteAll(cartItems);  // Clear the cart after placing the order
        return orderRepository.save(order);
    }

    // View order history
    public List<Order> viewOrderHistory(User customer) {
        List<Order> orders = orderRepository.findByCustomer(customer);
        return orders.isEmpty() ? List.of() : orders;  // Return empty list if no orders
    }
}
