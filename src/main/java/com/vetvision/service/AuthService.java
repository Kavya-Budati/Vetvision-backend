package com.vetvision.service;

import com.vetvision.dto.AuthResponse;
import com.vetvision.dto.LoginRequest;
import com.vetvision.dto.RegisterRequest;
import com.vetvision.dto.UserResponse;
import com.vetvision.entity.Role;
import com.vetvision.entity.User;
import com.vetvision.repository.UserRepository;
import com.vetvision.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder,
                       AuthenticationManager authenticationManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists.");
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPhone(request.phone());
        user.setRole(Role.RESCUER);
        users.save(user);
        return response(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = users.findByEmailIgnoreCase(email).orElseThrow();
        return response(user);
    }

    public UserResponse currentUser(String email) {
        return users.findByEmailIgnoreCase(email).map(UserResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    private AuthResponse response(User user) {
        return new AuthResponse(jwt.createToken(user.getEmail()), UserResponse.from(user));
    }
}