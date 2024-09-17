package com.ecommerce.app.controller;

import com.ecommerce.app.model.JwtRequest;
import com.ecommerce.app.model.JwtResponse;
import com.ecommerce.app.model.User;
import com.ecommerce.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        String token = userService.loginUser(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/details")
    public ResponseEntity<User> getUserDetails(Principal principal) {
        Optional<User> user = userService.findByUsername(principal.getName());
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(Principal principal, @RequestBody User updatedUser) {
        Optional<User> user = userService.findByUsername(principal.getName());
        if (user.isPresent()) {
            User updated = userService.updateUser(user.get().getId(), updatedUser);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
