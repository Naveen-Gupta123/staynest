package com.staynest.service.impl;

import com.staynest.dto.BookingRequest;
import com.staynest.entity.*;
import com.staynest.exception.BookingConflictException;
import com.staynest.repository.BookingRepository;
import com.staynest.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BookingServiceImplTest - unit tests for the most business-critical
 * logic in the whole app: booking creation, availability checking, and
 * total price calculation.
 *
 * WHY MOCKITO (@Mock / @InjectMocks) INSTEAD OF A REAL DATABASE?
 * Unit tests should test ONE class's logic in isolation, fast, without
 * needing MySQL running. We create fake (mocked) versions of
 * BookingRepository and PropertyRepository that return exactly the data
 * WE specify, so we can test BookingServiceImpl's decision-making
 * (the if/else business rules) without any real I/O.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Property testProperty;
    private User testGuest;

    @BeforeEach
    void setUp() {
        testGuest = new User();
        testGuest.setId(1L);
        testGuest.setFullName("Test Guest");

        testProperty = new Property();
        testProperty.setId(10L);
        testProperty.setTitle("Cozy Cabin");
        testProperty.setPricePerNight(new BigDecimal("2000.00"));
        testProperty.setMaxGuests(4);
    }

    @Test
    void createBooking_calculatesTotalPriceCorrectly_forThreeNights() {
        BookingRequest request = new BookingRequest();
        request.setCheckIn(LocalDate.now().plusDays(5));
        request.setCheckOut(LocalDate.now().plusDays(8)); // 3 nights
        request.setGuests(2);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(testProperty));
        when(bookingRepository.findOverlappingBookings(eq(10L), any(), any())).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.createBooking(10L, request, testGuest);

        // 3 nights * 2000.00/night = 6000.00
        assertEquals(new BigDecimal("6000.00"), result.getTotalPrice());
        assertEquals(BookingStatus.PENDING, result.getStatus());
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
    }

    @Test
    void createBooking_throwsBookingConflictException_whenDatesOverlapExistingBooking() {
        BookingRequest request = new BookingRequest();
        request.setCheckIn(LocalDate.now().plusDays(5));
        request.setCheckOut(LocalDate.now().plusDays(8));
        request.setGuests(2);

        Booking existingOverlap = new Booking();
        existingOverlap.setId(99L);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(testProperty));
        when(bookingRepository.findOverlappingBookings(eq(10L), any(), any()))
                .thenReturn(List.of(existingOverlap)); // simulate a conflicting booking already exists

        assertThrows(BookingConflictException.class,
                () -> bookingService.createBooking(10L, request, testGuest));

        // Critically: save() must NEVER be called when there's a conflict.
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_throwsIllegalArgumentException_whenGuestsExceedPropertyMax() {
        BookingRequest request = new BookingRequest();
        request.setCheckIn(LocalDate.now().plusDays(5));
        request.setCheckOut(LocalDate.now().plusDays(8));
        request.setGuests(10); // property max is 4

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(testProperty));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(10L, request, testGuest));

        verify(bookingRepository, never()).findOverlappingBookings(any(), any(), any());
    }

    @Test
    void createBooking_throwsIllegalArgumentException_whenCheckOutNotAfterCheckIn() {
        BookingRequest request = new BookingRequest();
        LocalDate sameDay = LocalDate.now().plusDays(5);
        request.setCheckIn(sameDay);
        request.setCheckOut(sameDay); // invalid: equal dates, zero nights
        request.setGuests(1);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(testProperty));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(10L, request, testGuest));
    }
}
