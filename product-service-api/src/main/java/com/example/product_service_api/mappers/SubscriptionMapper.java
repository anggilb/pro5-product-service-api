package com.example.product_service_api.mappers;

import com.example.product_service_api.commons.entities.SubscriptionModel;
import com.stripe.model.Subscription;
import org.springframework.stereotype.Component;

import static com.example.product_service_api.commons.enums.StripeEventEnum.SUBSCRIPTION_UPDATED;
import static com.example.product_service_api.commons.enums.StripeEventEnum.SUBSCRIPTION_PLAN_CHANGED;
import static com.example.product_service_api.commons.enums.StripeEventEnum.SUBSCRIPTION_UNSUBSCRIBE;

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
        subscriptionModel.setType(SUBSCRIPTION_UPDATED);
        subscriptionModel.setMonths(subscriptionModel.getMonths());

        Long unitAmount = subscription.getItems().getData().get(0).getPrice().getUnitAmount();
        Long currentAmount = subscriptionModel.getAmount();
        subscriptionModel.setAmount(unitAmount + currentAmount);

        return subscriptionModel;
    }

    public SubscriptionModel mapSubscriptionToSubscriptionPlanChanged(SubscriptionModel subscriptionModel, Subscription subscription) {
        String productId = subscription.getItems().getData().get(0).getPrice().getProduct();

        if (productId != subscriptionModel.getProductId()) {
            subscriptionModel.setType(SUBSCRIPTION_PLAN_CHANGED);
            subscriptionModel.setProductId(subscriptionModel.getProductId());
        }

        return subscriptionModel;
    }

    public SubscriptionModel mapSubscriptionToSubscriptionUnsubscribe(SubscriptionModel subscriptionModel, Subscription subscription) {
        subscriptionModel.setType(SUBSCRIPTION_UNSUBSCRIBE);

        return subscriptionModel;
    }
}
