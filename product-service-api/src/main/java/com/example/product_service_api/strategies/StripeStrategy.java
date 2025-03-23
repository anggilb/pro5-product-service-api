package com.example.product_service_api.strategies;

import com.stripe.model.Event;

public interface StripeStrategy {
    boolean isApplicable(Event event);
    Event process(Event event);
}
