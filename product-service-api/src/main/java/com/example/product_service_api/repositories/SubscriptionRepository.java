package com.example.product_service_api.repositories;

import com.example.product_service_api.commons.entities.SubscriptionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionModel, Long> {
    Optional<SubscriptionModel> findByCustomerId(String customerId);
}
