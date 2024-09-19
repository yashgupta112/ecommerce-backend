package com.ecommerce.app.config;

import com.ecommerce.app.security.JwtFilter;
import com.ecommerce.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @SuppressWarnings({ "deprecation", "removal" })
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(requests -> {
                    try {
                        System.err.println("requests");
                        System.err.println(requests);
                        requests
                                .requestMatchers("/api/auth/*").permitAll()
                                .requestMatchers("/api/users/**").hasAnyAuthority("ADMIN", "SELLER", "CUSTOMER")
                                .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN")
                                .requestMatchers("/api/seller/**").hasAnyAuthority("SELLER")
                                .requestMatchers("/api/customer/**").hasAnyAuthority("CUSTOMER")
                                .anyRequest().authenticated()
                                .and()
                                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        return builder.build();
    }
}
