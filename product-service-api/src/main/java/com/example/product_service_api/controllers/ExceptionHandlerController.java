package com.example.product_service_api.controllers;

import com.example.product_service_api.commons.dtos.ErrorResponse;
import com.example.product_service_api.commons.exceptions.BadRequestException;
import com.example.product_service_api.commons.exceptions.GeneralException;
import com.example.product_service_api.commons.exceptions.NotFoundException;
import com.example.product_service_api.commons.exceptions.OkayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.GeneralSecurityException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    @ExceptionHandler(value = {GeneralSecurityException.class, BadRequestException.class, NotFoundException.class, OkayException.class})
    ResponseEntity<ErrorResponse> handler(GeneralException exception) {
        log.warn("General Request Exception with message: {}", exception.getMessage());
        var errorResponse = getErrorResponse(exception);

        return ResponseEntity.status(errorResponse.getHttpStatus())
                .body(errorResponse);
    }

    private ErrorResponse getErrorResponse(GeneralException exception) {
        return ErrorResponse.builder()
                .httpStatus(exception.getHttpStatus())
                .message(exception.getMessage())
                .build();
    }
}
