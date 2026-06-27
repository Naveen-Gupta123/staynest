package com.staynest.repository;

import com.staynest.entity.Property;
import com.staynest.entity.PropertyType;
import com.staynest.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * PropertyRepository - handles all database access for listings,
 * including the multi-field SEARCH feature (city, state, country,
 * price range, bedrooms, guests, property type).
 */
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByHost(User host);

    List<Property> findByActiveTrue();

    /**
     * The search query. We use @Query with JPQL (Java Persistence Query
     * Language - it's like SQL but written against entity field names
     * instead of raw table/column names).
     *
     * WHY EVERY PARAMETER HAS A NULL-CHECK (e.g. ":city IS NULL OR ..."):
     * This single query supports search forms where the user has filled
     * in only SOME fields (e.g. just city + guests, leaving price blank).
     * Each "(:param IS NULL OR field = :param)" clause means: "if the
     * user didn't provide this filter, ignore it; otherwise, apply it."
     * This avoids writing 10 different overloaded repository methods for
     * every possible combination of filters.
     *
     * Pageable + Page<Property> gives us automatic PAGINATION - the
     * database only returns one "page" of results (e.g. 12 properties)
     * at a time instead of loading every matching row into memory.
     */
    @Query("""
        SELECT p FROM Property p
        WHERE p.active = true
        AND (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')))
        AND (:state IS NULL OR LOWER(p.state) LIKE LOWER(CONCAT('%', :state, '%')))
        AND (:country IS NULL OR LOWER(p.country) LIKE LOWER(CONCAT('%', :country, '%')))
        AND (:minPrice IS NULL OR p.pricePerNight >= :minPrice)
        AND (:maxPrice IS NULL OR p.pricePerNight <= :maxPrice)
        AND (:bedrooms IS NULL OR p.bedrooms >= :bedrooms)
        AND (:guests IS NULL OR p.maxGuests >= :guests)
        AND (:type IS NULL OR p.type = :type)
        """)
    Page<Property> searchProperties(
            @Param("city") String city,
            @Param("state") String state,
            @Param("country") String country,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("bedrooms") Integer bedrooms,
            @Param("guests") Integer guests,
            @Param("type") PropertyType type,
            Pageable pageable
    );

    /**
     * Used by the admin dashboard's "Top Properties" widget - counts
     * how many bookings each property has and ranks them.
     */
    @Query("""
        SELECT p FROM Property p
        LEFT JOIN p.bookings b
        GROUP BY p
        ORDER BY COUNT(b) DESC
        """)
    List<Property> findTopBookedProperties();
}
