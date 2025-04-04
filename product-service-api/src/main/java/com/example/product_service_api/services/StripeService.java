package com.example.product_service_api.services;

import com.example.product_service_api.commons.dtos.CheckoutRequest;
import com.example.product_service_api.commons.dtos.CheckoutResponse;
import com.example.product_service_api.commons.dtos.PlanChangeRequest;
import com.example.product_service_api.commons.dtos.PlanChangeResponse;
import com.example.product_service_api.commons.dtos.UnsubscribeRequest;
import com.example.product_service_api.commons.dtos.UnsubscribeResponse;
import com.example.product_service_api.commons.exceptions.BadRequestException;
import com.example.product_service_api.commons.properties.StripeProperties;
import com.stripe.exception.StripeException;
import com.stripe.Stripe;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.CouponCreateParams;
import com.stripe.param.PriceListParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class StripeService {
    public StripeService(StripeProperties stripeProperties) {
        Stripe.apiKey = stripeProperties.getKey();
    }

    public Customer getCustomerById(String customerId) {
        try {
            return Customer.retrieve(customerId);
        } catch (StripeException e) {
            log.error("Error getting customer: {}", e.getMessage());
            return null;
        }
    }

    public CheckoutResponse createCheckoutWithDiscount(CheckoutRequest checkoutRequest, BigDecimal discount) {
        var discountCreated = createDiscount(discount);
        var session = getSessionCreateParams(checkoutRequest)
                        .addDiscount(SessionCreateParams.Discount.builder()
                                .setCoupon(discountCreated.getId())
                                .build())
                        .build();

        try {
            return Optional.of(Session.create(session))
                    .map(sessionCreated ->
                        CheckoutResponse.builder()
                                .paymentUrl(sessionCreated.getUrl())
                                .build()
                    )
                    .orElseThrow(() -> new RuntimeException("Error couldn't create checkout"));
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private SessionCreateParams.Builder getSessionCreateParams(CheckoutRequest checkoutRequest) {
        var price = Optional.ofNullable(getPriceFromAProduct(checkoutRequest.getProductId()))
                .orElseThrow(() -> new BadRequestException("Price not found with product id: " + checkoutRequest.getProductId()));
        var session = SessionCreateParams.builder()
                .setCustomer(checkoutRequest.getCustomerId())
                .setSuccessUrl("http://localhost:8088/success")
                .setCancelUrl("http://localhost:8088/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                           .setPrice(price.getId())
                            .setQuantity(1L)
                            .build()
                )
                .putExtraParam("metadata", extraMetadata(checkoutRequest.getProductId()));

        return session.setMode(SessionCreateParams.Mode.SUBSCRIPTION);
    }

    private SessionCreateParams.Builder getSessionCreateParamsPlanChange(PlanChangeRequest planChangeRequest) {
        var price = Optional.ofNullable(getPriceFromAProduct(planChangeRequest.getProductId()))
                .orElseThrow(() -> new BadRequestException("Price not found with product id: " + planChangeRequest.getProductId()));
        var session = SessionCreateParams.builder()
                .setCustomer(planChangeRequest.getCustomerId())
                .setSuccessUrl("http://localhost:8088/success")
                .setCancelUrl("http://localhost:8088/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                           .setPrice(price.getId())
                            .setQuantity(1L)
                            .build()
                )
                .putExtraParam("metadata", extraMetadata(planChangeRequest.getProductId()));

        return session.setMode(SessionCreateParams.Mode.SUBSCRIPTION);
    }

    private SessionCreateParams.Builder getSessionCreateParamsUnsubscribe(UnsubscribeRequest unsubscribeRequest) {
        var price = Optional.ofNullable(getPriceFromAProduct(unsubscribeRequest.getProductId()))
                .orElseThrow(() -> new BadRequestException("Price not found with product id: " + unsubscribeRequest.getProductId()));
        var session = SessionCreateParams.builder()
                .setCustomer(unsubscribeRequest.getCustomerId())
                .setSuccessUrl("http://localhost:8088/success")
                .setCancelUrl("http://localhost:8088/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(price.getId())
                        .setQuantity(1L)
                        .build()
                );

        return session.setMode(SessionCreateParams.Mode.SUBSCRIPTION);
    }

    private Map<String, Object> extraMetadata(String productId) {
        return Map.of(
           "product_id", productId
        );
    }

    private Price getPriceFromAProduct(String productId) {
        try {
            var priceListParams = PriceListParams.builder()
                    .setProduct(productId)
                    .build();
            var priceList = Price.list(priceListParams)
                    .getData()
                    .stream();

            return priceList
                    .findFirst()
                    .orElse(null);
        } catch (StripeException e) {
            log.error("Error getting price from product: {}", e.getMessage());
            return null;
        }
    }

    private Coupon createDiscount(BigDecimal discount) {
        CouponCreateParams couponParams = CouponCreateParams.builder()
                .setName("PROYECTOS")
                .setPercentOff(discount)
                .setDuration(CouponCreateParams.Duration.ONCE)
                .build();

        try {
            return Coupon.create(couponParams);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create discount coupon", e);
        }
    }

    public PlanChangeResponse createChangePlan(PlanChangeRequest planChangeRequest) {
        var session = getSessionCreateParamsPlanChange(planChangeRequest).build();

        try {
            return Optional.of(Session.create(session))
                    .map(sessionCreated ->
                            PlanChangeResponse.builder()
                                    .paymentUrl(sessionCreated.getUrl())
                                    .build()
                    )
                    .orElseThrow(() -> new RuntimeException("Error couldn't create change plan."));
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    public UnsubscribeResponse createUnsubscribe(UnsubscribeRequest unsubscribeRequest) {
        var session = getSessionCreateParamsUnsubscribe(unsubscribeRequest).build();

        try {
            return Optional.of(Session.create(session))
                    .map(sessionCreated ->
                            UnsubscribeResponse.builder()
                                    .paymentUrl(sessionCreated.getUrl())
                                    .build()
                    )
                    .orElseThrow(() -> new RuntimeException("Error couldn't create unsubscribe"));
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}