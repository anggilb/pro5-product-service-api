package com.example.product_service_api.controllers;

import com.example.product_service_api.commons.enums.ProductPlanEnum;
import com.example.product_service_api.commons.enums.UserRoleEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.product_service_api.services.StripePaymentService;

import static com.example.product_service_api.commons.enums.UserRoleEnum.USER;

@RequestMapping("/v1/stripe/payment")
@RestController
public class StripePaymentController {
    private final StripePaymentService stripePaymentService;

    public StripePaymentController(StripePaymentService stripePaymentService) {
        this.stripePaymentService = stripePaymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> createCheckout(@RequestParam ProductPlanEnum productPlan,
                                                 @RequestAttribute("X-User-Id") Long userId,
                                                 @RequestAttribute("X-User-Role") UserRoleEnum userRole){

        if (!USER.equals(userRole)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(stripePaymentService.createCheckout(productPlan, userId));
    }
}
