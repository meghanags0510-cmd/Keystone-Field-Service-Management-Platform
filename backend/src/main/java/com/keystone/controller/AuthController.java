package com.keystone.controller;
import com.keystone.dto.*;
import com.keystone.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }
    @PostMapping("/login") public LoginResponse login(@Valid @RequestBody LoginRequest r) { return service.login(r); }
}
