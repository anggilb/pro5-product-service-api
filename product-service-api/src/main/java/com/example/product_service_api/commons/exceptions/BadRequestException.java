package com.example.product_service_api.commons.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BadRequestException extends GeneralException {
  public BadRequestException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
