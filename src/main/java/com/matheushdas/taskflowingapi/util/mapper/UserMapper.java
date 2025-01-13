package com.matheushdas.taskflowingapi.util.mapper;

import com.matheushdas.taskflowingapi.dto.auth.RegisterRequest;
import com.matheushdas.taskflowingapi.dto.auth.RegisterResponse;
import com.matheushdas.taskflowingapi.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(RegisterRequest data) {
        return new User(
                data.username(),
                data.email(),
                data.password()
        );
    }

    public RegisterResponse toResponse(User data) {
        return new RegisterResponse(
                data.getId(),
                data.getUsername(),
                data.getEmail()
        );
    }
}
