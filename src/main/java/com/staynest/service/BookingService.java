package com.staynest.service;

import com.staynest.dto.BookingRequest;
import com.staynest.entity.Booking;
import com.staynest.entity.User;

import java.util.List;

public interface BookingService {

    Booking createBooking(Long propertyId, BookingRequest request, User guest);

    void cancelBooking(Long bookingId, User guest);

    void confirmBooking(Long bookingId, User host);

    void rejectBooking(Long bookingId, User host);

    List<Booking> getBookingsByGuest(User guest);

    List<Booking> getBookingsByHost(User host);

    Booking getBookingById(Long bookingId);

    List<Booking> getRecentBookings();

    long countByStatus(com.staynest.entity.BookingStatus status);
}
