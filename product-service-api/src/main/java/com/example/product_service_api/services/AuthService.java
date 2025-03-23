package com.example.product_service_api.services;

import com.example.product_service_api.commons.dtos.TokenResponse;
import com.example.product_service_api.commons.dtos.AuthRequest;
import com.example.product_service_api.commons.entities.UserModel;
import com.example.product_service_api.commons.exceptions.BadRequestException;
import com.example.product_service_api.mappers.UserMapper;
import com.example.product_service_api.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    public TokenResponse register(AuthRequest authRequest) {
        return Optional.of(authRequest)
                .map(userMapper::authToEntity)
                .map(this::encodePassword)
                .map(userRepository::save)
                .map(user -> jwtService.generateToken(user.getUserId()))
                .orElseThrow(() -> new BadRequestException("Error creating user"));
    }

    public TokenResponse login(AuthRequest authRequest) {
        return userRepository.findByEmail(authRequest.getEmail())
                .filter(user -> passwordEncoder.matches(authRequest.getPassword(), user.getPassword()))
                .map(user -> jwtService.generateToken(user.getUserId()))
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
    }

    private UserModel encodePassword(UserModel user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }
}