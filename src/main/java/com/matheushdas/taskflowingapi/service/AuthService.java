package com.matheushdas.taskflowingapi.service;

import com.matheushdas.taskflowingapi.dto.auth.RegisterRequest;
import com.matheushdas.taskflowingapi.dto.auth.RegisterResponse;
import com.matheushdas.taskflowingapi.model.entity.User;
import com.matheushdas.taskflowingapi.persistence.UserRepository;
import com.matheushdas.taskflowingapi.util.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        User toSave = userMapper.toEntity(request);
        toSave.setPassword(passwordEncoder.encode(toSave.getPassword()));
        return userMapper.toResponse(userRepository.save(toSave));
    }
}
