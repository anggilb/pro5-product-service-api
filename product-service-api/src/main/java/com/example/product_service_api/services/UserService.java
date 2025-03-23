package com.example.product_service_api.services;

import com.example.product_service_api.commons.entities.UserModel;
import com.example.product_service_api.commons.enums.UserRoleEnum;
import com.example.product_service_api.commons.exceptions.BadRequestException;
import com.example.product_service_api.commons.exceptions.NotFoundException;
import com.example.product_service_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserModel updateRole(String email, UserRoleEnum role) {
        return Optional.of(email)
                .map(this::getUserByEmail)
                .map(userModel -> {
                    userModel.setRole(role);
                    return userModel;
                })
                .map(userRepository::save)
                .orElseThrow(() -> new BadRequestException("Error couldn't update user role with email: " + email));
    }

    private UserModel getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Error getting user by email"));
    }

    public UserModel getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Error getting user by id"));
    }
 }
