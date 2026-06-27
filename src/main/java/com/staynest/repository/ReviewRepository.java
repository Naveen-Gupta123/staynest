package com.staynest.repository;

import com.staynest.entity.Property;
import com.staynest.entity.Review;
import com.staynest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPropertyOrderByCreatedAtDesc(Property property);

    List<Review> findByUser(User user);

    Optional<Review> findByPropertyAndUser(Property property, User user);

    /**
     * Computes the average star rating for a property. We use JPQL's
     * AVG() aggregate function so MySQL does the math, rather than
     * pulling every review into Java memory and averaging manually.
     */
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.property.id = :propertyId")
    Double findAverageRatingByPropertyId(@Param("propertyId") Long propertyId);

    long countByPropertyId(Long propertyId);
}
