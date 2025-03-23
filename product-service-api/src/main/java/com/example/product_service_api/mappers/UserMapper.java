package com.example.product_service_api.mappers;

import com.example.product_service_api.commons.dtos.AuthRequest;
import com.example.product_service_api.commons.entities.UserModel;
import org.springframework.stereotype.Component;

import static com.example.product_service_api.commons.enums.UserRoleEnum.USER;

@Component
public class UserMapper {
    public UserModel authToEntity(AuthRequest authRequest) {
        return UserModel.builder()
                .email(authRequest.getEmail())
                .password(authRequest.getPassword())
                .role(USER)
                .build();
    }
}
