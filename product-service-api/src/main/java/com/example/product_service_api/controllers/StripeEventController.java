package com.example.product_service_api.controllers;

import com.example.product_service_api.services.StripeEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/stripe/webhook")
@RestController
public class StripeEventController {
    private final StripeEventService stripeEventService;

    public StripeEventController(StripeEventService stripeEventService) {
        this.stripeEventService = stripeEventService;
    }

    @PostMapping
    public ResponseEntity<Void> processEvent(@RequestBody String payload, @RequestHeader(name = "stripe-signature") String stripeHeader)
    {
        var event = stripeEventService.constructEvent(payload, stripeHeader);
        stripeEventService.manageWebhook(event);

        return ResponseEntity.noContent().build();
    }
}
