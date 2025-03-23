package com.example.product_service_api.commons.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix="stripe.secret")
public class StripeProperties {
    private String endpoint;
    private String key;
}
