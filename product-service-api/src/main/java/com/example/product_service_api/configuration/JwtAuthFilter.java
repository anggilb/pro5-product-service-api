package com.example.product_service_api.configuration;

import com.example.product_service_api.commons.entities.UserModel;
import com.example.product_service_api.repositories.UserRepository;
import com.example.product_service_api.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // Remover "Bearer "
                Long userId = jwtService.extractedUserId(token);

                if (userId != null) {
                    Long parsedUserId = Long.valueOf(userId);
                    Optional<UserModel> userDetails = userRepository.findById(parsedUserId);

                    if (userDetails.isPresent()) {
                        log.info("User found: {}", userDetails.get());
                        request.setAttribute("X-User-Id", userDetails.get().getUserId());
                        request.setAttribute("X-User-Role", userDetails.get().getRole());
                        processAuthentication(request, userDetails.get());
                    } else {
                        log.warn("User not found for ID: {}", parsedUserId);
                    }
                } else {
                    log.warn("Invalid or missing user ID in JWT");
                }
            }
        } catch (Exception e) {
            log.error("Error in JWT filter: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void processAuthentication(HttpServletRequest request, UserModel userDetails) {
        String jwtToken = request.getHeader("Authorization").substring(7);
        Optional.of(jwtToken)
                .filter(token -> !jwtService.isExpired(token))
                .ifPresent(token -> {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                });
    }
}