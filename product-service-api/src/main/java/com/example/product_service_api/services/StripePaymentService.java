package com.example.product_service_api.services;

import com.example.product_service_api.commons.dtos.CheckoutRequest;
import com.example.product_service_api.commons.dtos.CheckoutResponse;
import com.example.product_service_api.commons.dtos.PlanChangeRequest;
import com.example.product_service_api.commons.dtos.PlanChangeResponse;
import com.example.product_service_api.commons.dtos.UnsubscribeRequest;
import com.example.product_service_api.commons.dtos.UnsubscribeResponse;
import com.example.product_service_api.commons.entities.UserModel;
import com.example.product_service_api.commons.enums.ProductPlanEnum;
import com.example.product_service_api.commons.exceptions.BadRequestException;
import com.example.product_service_api.commons.properties.ProductProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StripePaymentService {
    private final UserService userService;
    private final ProductProperties productProperties;
    private final StripeService stripeService;

    public StripePaymentService(UserService userService, ProductProperties productProperties, StripeService stripeService) {
        this.userService = userService;
        this.productProperties = productProperties;
        this.stripeService = stripeService;
    }

    public String createCheckout(ProductPlanEnum productPlan, Long userId) {
        return Optional.of(userId)
                .map(userService::getUserById)
                .map(given -> buildCheckoutRequest(productPlan, given))
                .map(given -> stripeService.createCheckoutWithDiscount(given, new BigDecimal("50.00")))
                .map(CheckoutResponse::getPaymentUrl)
                .orElseThrow(() -> new BadRequestException("Invalid product plan"));
    }

    public String createChangePlan(ProductPlanEnum productPlan, Long userId) {
        return Optional.of(userId)
                .map(userService::getUserById)
                .map(given -> buildPlanChangeRequest(productPlan, given))
                .map(given -> stripeService.createChangePlan(given))
                .map(PlanChangeResponse::getPaymentUrl)
                .orElseThrow(() -> new BadRequestException("Invalid product plan"));
    }

    public String createUnsubscribe(Long userId) {
        return Optional.of(userId)
                .map(userService::getUserById)
                .map(given -> buildUnsubscribeRequest(given))
                .map(given -> stripeService.createUnsubscribe(given))
                .map(UnsubscribeResponse::getPaymentUrl)
                .orElseThrow(() -> new BadRequestException("Invalid product plan"));
    }

    private CheckoutRequest buildCheckoutRequest(ProductPlanEnum productPlan, UserModel given) {
        return CheckoutRequest.builder()
                .customerId(given.getCustomerId())
                .productId(productProperties.getProductIds().get(productPlan))
                .build();
    }

    private PlanChangeRequest buildPlanChangeRequest(ProductPlanEnum productPlan, UserModel given) {
        return PlanChangeRequest.builder()
                .customerId(given.getCustomerId())
                .productId(productProperties.getProductIds().get(productPlan))
                .build();
    }

    private UnsubscribeRequest buildUnsubscribeRequest(UserModel given) {
        return UnsubscribeRequest.builder()
                .customerId(given.getCustomerId())
                .build();
    }
}
