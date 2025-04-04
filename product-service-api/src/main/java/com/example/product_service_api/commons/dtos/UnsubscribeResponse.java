package com.example.product_service_api.commons.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnsubscribeResponse {
    private String paymentUrl;
}
