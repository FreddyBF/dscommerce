package com.github.freddy.controllers;


import com.github.freddy.dtos.auth.*;

import com.github.freddy.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody @Valid RegisterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody @Valid RefreshTokenDTO dto) {
        return ResponseEntity.ok(authService.refreshToken(dto.refreshToken()));
    }
}
