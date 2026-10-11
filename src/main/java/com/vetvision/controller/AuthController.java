package com.vetvision.controller;

import com.vetvision.dto.AuthResponse;
import com.vetvision.dto.LoginRequest;
import com.vetvision.dto.RegisterRequest;
import com.vetvision.dto.UserResponse;
import com.vetvision.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) { return auth.register(request); }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { return auth.login(request); }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) { return auth.currentUser(authentication.getName()); }
}