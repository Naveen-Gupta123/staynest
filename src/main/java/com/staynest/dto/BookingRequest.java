package com.staynest.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * BookingRequest - DTO bound to the "Book Now" form on a property page.
 * Like PropertyRequest, it deliberately omits `property` and `guest` -
 * those come from the URL path variable and the logged-in session,
 * never trusted from raw form input.
 */
@Data
public class BookingRequest {

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOut;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    private Integer guests;
}
