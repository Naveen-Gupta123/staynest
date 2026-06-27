package com.staynest.exception;

/**
 * Thrown specifically when a guest tries to book dates that overlap
 * with an existing booking on the same property (availability conflict).
 * Kept as its own exception type (rather than reusing a generic one) so
 * the controller can show a SPECIFIC, helpful message: "those dates are
 * already booked - try different dates" rather than a vague error.
 */
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}
