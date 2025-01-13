package com.matheushdas.taskflowingapi.service;

import com.matheushdas.taskflowingapi.config.security.jwt.LoginTokenProvider;
import com.matheushdas.taskflowingapi.dto.auth.LoginRequest;
import com.matheushdas.taskflowingapi.dto.auth.LoginResponse;
import com.matheushdas.taskflowingapi.dto.auth.RegisterRequest;
import com.matheushdas.taskflowingapi.dto.auth.RegisterResponse;
import com.matheushdas.taskflowingapi.model.entity.User;
import com.matheushdas.taskflowingapi.persistence.UserRepository;
import com.matheushdas.taskflowingapi.util.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private LoginTokenProvider loginTokenProvider;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest request) {
        User toSave = userMapper.toEntity(request);
        toSave.setPassword(passwordEncoder.encode(toSave.getPassword()));
        return userMapper.toResponse(userRepository.save(toSave));
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.login())
                .orElseThrow();

        if(user == null) throw new RuntimeException("No records found for this username or email!");

        String username = user.getUsername();
        String password = request.password();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        return loginTokenProvider.generateAuthorizationToken(username, user.getRoles());
    }

    public LoginResponse refreshToken(String username, String refreshToken) {
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow();

        if(user == null) throw new RuntimeException("No records found for this username or email!");

        return loginTokenProvider.refreshAccessToken(refreshToken);
    }
}
