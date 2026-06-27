package com.staynest.service.impl;

import com.staynest.dto.BookingRequest;
import com.staynest.entity.Booking;
import com.staynest.entity.BookingStatus;
import com.staynest.entity.PaymentStatus;
import com.staynest.entity.Property;
import com.staynest.entity.User;
import com.staynest.exception.BookingConflictException;
import com.staynest.exception.ResourceNotFoundException;
import com.staynest.exception.UnauthorizedActionException;
import com.staynest.repository.BookingRepository;
import com.staynest.repository.PropertyRepository;
import com.staynest.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;

    @Override
    @Transactional
    public Booking createBooking(Long propertyId, BookingRequest request, User guest) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        // ---- Business rule: check-out must be after check-in ----
        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        // ---- Business rule: guests must not exceed the property's max ----
        if (request.getGuests() > property.getMaxGuests()) {
            throw new IllegalArgumentException(
                "This property allows a maximum of " + property.getMaxGuests() + " guests."
            );
        }

        // ---- AVAILABILITY CHECK ----
        // This query (see BookingRepository.findOverlappingBookings) is
        // the single most important correctness check in the whole app:
        // it prevents DOUBLE-BOOKING the same property for overlapping
        // dates. We run this INSIDE the @Transactional method so the
        // check-then-save happens atomically from the database's point
        // of view for this request.
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                propertyId, request.getCheckIn(), request.getCheckOut()
        );
        if (!overlapping.isEmpty()) {
            throw new BookingConflictException(
                "This property is already booked for some or all of the selected dates. Please choose different dates."
            );
        }

        // ---- TOTAL PRICE CALCULATION ----
        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        BigDecimal totalPrice = property.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setGuests(request.getGuests());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, User guest) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getGuest().getId().equals(guest.getId())) {
            throw new UnauthorizedActionException("You can only cancel your own bookings.");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("A completed stay cannot be cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void confirmBooking(Long bookingId, User host) {
        Booking booking = getBookingById(bookingId);
        verifyHostOwnsBookingProperty(booking, host);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void rejectBooking(Long bookingId, User host) {
        Booking booking = getBookingById(bookingId);
        verifyHostOwnsBookingProperty(booking, host);

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByGuest(User guest) {
        return bookingRepository.findByGuestOrderByCreatedAtDesc(guest);
    }

    @Override
    public List<Booking> getBookingsByHost(User host) {
        return bookingRepository.findByPropertyHostOrderByCreatedAtDesc(host);
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    @Override
    public List<Booking> getRecentBookings() {
        return bookingRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public long countByStatus(BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }

    private void verifyHostOwnsBookingProperty(Booking booking, User host) {
        if (!booking.getProperty().getHost().getId().equals(host.getId())) {
            throw new UnauthorizedActionException("You do not have permission to manage this booking.");
        }
    }
}
