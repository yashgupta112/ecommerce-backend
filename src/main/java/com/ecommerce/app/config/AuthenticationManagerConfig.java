// package com.ecommerce.app.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// import com.ecommerce.app.service.UserService;

// @Configuration
// public class AuthenticationManagerConfig {

//     @Bean
//     public AuthenticationManager authenticationManagerBean(AuthenticationManagerBuilder builder, UserService userService, BCryptPasswordEncoder passwordEncoder) throws Exception {
//         builder.userDetailsService(userService).passwordEncoder(passwordEncoder);
//         return builder.build();
//     }
// }
