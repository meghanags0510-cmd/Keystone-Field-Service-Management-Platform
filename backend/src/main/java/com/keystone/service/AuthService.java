package com.keystone.service;

import com.keystone.domain.User;
import com.keystone.dto.*;
import com.keystone.repository.UserRepository;
import com.keystone.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final UserRepository users;
    private final JwtService jwt;

    public AuthService(AuthenticationManager authManager, UserRepository users, JwtService jwt) {
        this.authManager = authManager; this.users = users; this.jwt = jwt;
    }

    public LoginResponse login(LoginRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = users.findByEmailIgnoreCase(request.email()).orElseThrow();
        return new LoginResponse(jwt.createToken(user.getEmail(), user.getRole().name()), user.getRole().name(), user.getFullName());
    }
}
