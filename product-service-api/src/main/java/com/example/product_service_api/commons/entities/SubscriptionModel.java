package com.example.product_service_api.commons.entities;

import com.example.product_service_api.commons.enums.StripeEventEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String subscriptionId;
    private String customerEmail;
    private String customerId;
    private String productId;
    private Long amount;
    private String currency;
    private Integer months;
    @Enumerated(EnumType.STRING)
    private StripeEventEnum type;
}
