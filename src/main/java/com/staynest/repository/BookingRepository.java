package com.staynest.repository;

import com.staynest.entity.Booking;
import com.staynest.entity.BookingStatus;
import com.staynest.entity.Property;
import com.staynest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByGuestOrderByCreatedAtDesc(User guest);

    List<Booking> findByPropertyHostOrderByCreatedAtDesc(User host);

    List<Booking> findByProperty(Property property);

    List<Booking> findByStatus(BookingStatus status);

    /**
     * AVAILABILITY CHECKING - the most important query in the booking
     * module. Before confirming a new booking, we must check: "does any
     * EXISTING confirmed/pending booking for this property overlap with
     * the requested check-in/check-out dates?"
     *
     * The overlap condition (checkIn < requestedCheckOut AND checkOut >
     * requestedCheckIn) is the standard date-range-overlap formula:
     * two date ranges overlap unless one ends before the other begins.
     *
     * We exclude CANCELLED and REJECTED bookings because those dates
     * are no longer "occupied" - the room is free again.
     */
    @Query("""
        SELECT b FROM Booking b
        WHERE b.property.id = :propertyId
        AND b.status NOT IN ('CANCELLED', 'REJECTED')
        AND b.checkIn < :checkOut
        AND b.checkOut > :checkIn
        """)
    List<Booking> findOverlappingBookings(
            @Param("propertyId") Long propertyId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    long countByStatus(BookingStatus status);

    /**
     * Used by the admin dashboard "Recent Bookings" widget.
     */
    List<Booking> findTop10ByOrderByCreatedAtDesc();
}
