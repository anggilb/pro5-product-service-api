package com.example.product_service_api.mappers;

import com.example.product_service_api.commons.entities.SubscriptionModel;
import com.stripe.model.Subscription;
import org.springframework.stereotype.Component;

import static com.example.product_service_api.commons.enums.StripeEventEnum.SUBSCRIPTION_UPDATED;

@Component
public class SubscriptionMapper {
    public SubscriptionModel mapSubscriptionToSubscriptionCreated(Subscription subscription) {
        var product = subscription.getItems().getData().get(0).getPrice().getProduct();

        return SubscriptionModel.builder()
                .productId(product)
                .amount(0L)
                .subscriptionId(subscription.getId())
                .currency(subscription.getItems().getData().get(0).getPrice().getCurrency())
                .customerId(subscription.getCustomer())
                .months(0)
                .type(SUBSCRIPTION_UPDATED)
                .build();
    }

    public SubscriptionModel mapSubscriptionToSubscriptionUpdated(SubscriptionModel subscriptionModel, Subscription subscription) {
        var product = subscription.getItems().getData().get(0).getPrice().getProduct();

        subscriptionModel.setType(SUBSCRIPTION_UPDATED);
        subscriptionModel.setMonths(subscriptionModel.getMonths());
        subscriptionModel.setAmount(subscription.getItems().getData().get(0).getPrice().getUnitAmount() + subscriptionModel.getAmount());

        return subscriptionModel;
    }
}
