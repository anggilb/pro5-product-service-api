package com.example.product_service_api.commons.exceptions;

import org.springframework.http.HttpStatus;

public class OkayException extends GeneralException {
    public OkayException(String message) {
        super(HttpStatus.OK, message);
    }
}
