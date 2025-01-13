package com.matheushdas.taskflowingapi.controller;

import com.matheushdas.taskflowingapi.dto.auth.LoginRequest;
import com.matheushdas.taskflowingapi.dto.auth.LoginResponse;
import com.matheushdas.taskflowingapi.dto.auth.RegisterRequest;
import com.matheushdas.taskflowingapi.dto.auth.RegisterResponse;
import com.matheushdas.taskflowingapi.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest register) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(register));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest login) {
        return ResponseEntity.ok(authService.login(login));
    }

    @PutMapping("/refresh/{username}")
    public ResponseEntity refreshToken(@PathVariable String username,
                                @RequestHeader("Authorization") String refreshToken) {
        LoginResponse response = authService.refreshToken(username, refreshToken);

        return response == null ?
                ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!") :
                ResponseEntity.ok(response);
    }
}
