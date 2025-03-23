package com.example.product_service_api.commons.properties;

import com.example.product_service_api.commons.enums.ProductPlanEnum;
import com.example.product_service_api.commons.enums.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.example.product_service_api.commons.enums.ProductPlanEnum.ALL_IN;
import static com.example.product_service_api.commons.enums.ProductPlanEnum.STARTER;
import static com.example.product_service_api.commons.enums.UserRoleEnum.USER_PAID_ALL_IN;
import static com.example.product_service_api.commons.enums.UserRoleEnum.USER_PAID_STARTER;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "stripe.products")
public class ProductProperties {
    private String starter;
    private String allIn;

    public Map<String, UserRoleEnum> getProductStripeMap() {
        return Map.of(
                starter, USER_PAID_STARTER,
                allIn, USER_PAID_ALL_IN
        );
    }

    public Map<UserRoleEnum, String> getProductNames() {
        return Map.of(
                USER_PAID_STARTER, "starter",
                USER_PAID_ALL_IN, "allIn"
        );
    }

    public Map<ProductPlanEnum, String> getProductIds() {
        return Map.of(
                STARTER, starter,
                ALL_IN, allIn
        );
    }
}
