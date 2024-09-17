package com.ecommerce.app.service;

import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.model.*;
import com.ecommerce.app.repository.CartRepository;
import com.ecommerce.app.repository.OrderRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found");
        }
        return products;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found in category: " + category);
        }
        return products;
    }

    public List<Cart> getCartItems(User user) {
        List<Cart> cartItems = cartRepository.findByCustomer(user);
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("No items found in cart");
        }
        return cartItems;
    }

    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Cart cartItem = new Cart();
        cartItem.setCustomer(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    public void removeFromCart(Long cartItemId) {
        if (!cartRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        cartRepository.deleteById(cartItemId);
    }

    public Cart updateCartItem(Long cartItemId, Integer quantity) {
        Cart cartItem = cartRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    public List<Order> getOrders(User user) {
        List<Order> orders = orderRepository.findByCustomer(user);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found");
        }
        return orders;
    }

    public Order placeOrder(Long userId, String shippingAddress, String phone) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Cart> cartItems = cartRepository.findByCustomer(user);

        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("No items in cart to place order");
        }

        Order order = new Order();
        order.setCustomer(user);
        order.setOrderDate(new Date());
        order.setStatus("Placed");
        order.setShippingAddress(shippingAddress);
        order.setPhone(phone);

        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            totalAmount += cartItem.getQuantity() * product.getPrice();
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        cartRepository.deleteAll(cartItems);
        return orderRepository.save(order);
    }
}
