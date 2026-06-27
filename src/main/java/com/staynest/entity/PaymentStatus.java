package com.staynest.entity;

/**
 * PaymentStatus tracks whether a booking has actually been paid for.
 * In this version, payment is SIMULATED (no real payment gateway
 * integration) - it exists so the architecture is ready to plug in
 * Razorpay/Stripe later (noted as a Phase 2 enhancement). For now,
 * marking a booking "PAID" just flips this flag for demo purposes.
 */
public enum PaymentStatus {
    PENDING,
    PAID,
    REFUNDED,
    FAILED
}
