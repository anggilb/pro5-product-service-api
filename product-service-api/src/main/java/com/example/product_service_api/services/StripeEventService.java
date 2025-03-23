package com.example.product_service_api.services;

import com.example.product_service_api.commons.exceptions.BadRequestException;
import com.example.product_service_api.commons.properties.StripeProperties;
import com.example.product_service_api.strategies.StripeStrategy;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StripeEventService {
    private final StripeProperties stripeProperties;
    private final List<StripeStrategy> stripeStrategies;

    public StripeEventService(StripeProperties stripeProperties, List<StripeStrategy> stripeStrategies) {
        this.stripeProperties = stripeProperties;
        this.stripeStrategies = stripeStrategies;
    }

    public Event constructEvent(String payload, String stripeHeader) {
        try {
            return Webhook.constructEvent(payload, stripeHeader,stripeProperties.getEndpoint());
        } catch (SignatureVerificationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void manageWebhook(Event event) {
        Optional.of(event)
                .map(this::processStrategy);
    }

    private Object processStrategy(Event event) {
        return stripeStrategies.stream()
                .filter(stripeStrategy -> stripeStrategy.isApplicable(event))
                .findFirst()
                .map(stripeStrategy -> stripeStrategy.process(event))
                .orElseGet(Event::new);
    }
}
