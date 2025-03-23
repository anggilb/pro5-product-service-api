package com.example.product_service_api.commons.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class NotFoundException extends  GeneralException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
