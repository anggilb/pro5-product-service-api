package com.example.product_service_api.commons.enums;

public enum StripeEventEnum {
    SUBSCRIPTION_UPDATED("customer.subscription.updated"),
    SUBSCRIPTION_PLAN_CHANGED("customer.subscription.plan_changed"),
    SUBSCRIPTION_UNSUBSCRIBE("customer.subscription.unsubscribe");

    private final String value;

    StripeEventEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}