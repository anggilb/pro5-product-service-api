package com.example.product_service_api.strategies.impl;

import com.example.product_service_api.commons.entities.SubscriptionModel;
import com.example.product_service_api.commons.exceptions.BadRequestException;
import com.example.product_service_api.commons.properties.ProductProperties;
import com.example.product_service_api.mappers.SubscriptionMapper;
import com.example.product_service_api.repositories.SubscriptionRepository;
import com.example.product_service_api.services.StripeService;
import com.example.product_service_api.services.UserService;
import com.example.product_service_api.strategies.StripeStrategy;
import com.stripe.model.Event;
import com.stripe.model.Subscription;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.example.product_service_api.commons.enums.StripeEventEnum.SUBSCRIPTION_PLAN_CHANGED;

@Slf4j
@Component
public class StripeStrategySubscriptionPlanChange implements StripeStrategy {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final StripeService stripeService;
    private final ProductProperties productProperties;
    private final UserService userService;

    public StripeStrategySubscriptionPlanChange(SubscriptionRepository subscriptionRepository, SubscriptionMapper subscriptionMapper, StripeService stripeService, ProductProperties productProperties, UserService userService) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionMapper = subscriptionMapper;
        this.stripeService = stripeService;
        this.productProperties = productProperties;
        this.userService = userService;
    }

    @Override
    public boolean isApplicable(Event event) {
        return SUBSCRIPTION_PLAN_CHANGED.getValue().equals(event.getType());
    }

    public Event process(Event event) {
        Optional.of(event)
                .map(this::deserializeEvent)
                .map(this::processPlanChange)
                .orElseThrow(() -> new BadRequestException("Event is not applicable"));

        return event;
    }

    private Subscription processPlanChange(Subscription subscription) {
        Optional.of(subscription)
                .map(this::findSubscriptionById)
                .map(sub -> subscriptionMapper.mapSubscriptionToSubscriptionPlanChanged(sub, subscription))
                .map(subscriptionRepository::save)
                .ifPresent(sub -> {
                    log.info("Subscription updated: {}, type event: {}", sub.getSubscriptionId(), sub.getType());
                });

        return subscription;
    }

    private SubscriptionModel findSubscriptionById(Subscription subscription) {
        return subscriptionRepository.findByCustomerId(subscription.getId())
                .orElseThrow(() -> new RuntimeException("Subscription exception."));

    }

    private Subscription deserializeEvent(Event event) {
        return (Subscription) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new RuntimeException("Cannot deserialize event"));
    }
}
