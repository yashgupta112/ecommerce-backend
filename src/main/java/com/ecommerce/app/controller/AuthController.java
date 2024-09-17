package com.ecommerce.app.controller;

import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.model.JwtRequest;
import com.ecommerce.app.model.JwtResponse;
import com.ecommerce.app.model.User;
import com.ecommerce.app.service.UserService;
import com.ecommerce.app.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        String token = userService.loginUser(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserDetails(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/update")
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String token, @RequestBody User updatedUser) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User updated = userService.updateUser(user.getId(), updatedUser);
        return ResponseEntity.ok(updated);
    }
}
