package com.example.product_service_api.commons.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class ErrorResponse {
    private HttpStatus httpStatus;
    private String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}