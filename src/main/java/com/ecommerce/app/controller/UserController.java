package com.ecommerce.app.controller;

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


    @GetMapping("/profile")
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
