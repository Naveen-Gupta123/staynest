package com.staynest.entity;

/**
 * BookingStatus tracks the lifecycle of a reservation:
 *
 *   PENDING   -> guest just booked, waiting for host to respond
 *   CONFIRMED -> host accepted the booking
 *   REJECTED  -> host declined the booking
 *   CANCELLED -> guest cancelled it (before or after confirmation)
 *   COMPLETED -> the stay has happened and finished
 */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    CANCELLED,
    COMPLETED
}
