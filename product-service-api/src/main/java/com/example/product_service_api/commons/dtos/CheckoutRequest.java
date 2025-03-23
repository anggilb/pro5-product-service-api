package com.example.product_service_api.commons.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckoutRequest {
    private String customerId;
    private String productId;
}
