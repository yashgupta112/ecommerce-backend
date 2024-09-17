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
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Order> getOrders(User user) {
        List<Order> orders = orderRepository.findByCustomer(user);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found");
        }
        return orders;
    }

    public Order placeOrder(Long userId, String shippingAddress, String phone) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"CUSTOMER".equals(user.getRole())) {
            throw new IllegalArgumentException("Sellers/Admins cannot place orders");
        }

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
