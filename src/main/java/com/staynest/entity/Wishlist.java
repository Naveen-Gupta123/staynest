package com.staynest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Wishlist entity - represents a Guest saving a Property for later.
 * Conceptually a many-to-many between User and Property, but modeled as
 * its own entity (rather than @ManyToMany) so we can record WHEN it was
 * saved, and so removing one saved item doesn't require touching the
 * User or Property tables at all.
 *
 * A unique constraint on (user_id, property_id) stops the same guest
 * from saving the same property twice.
 */
@Entity
@Table(
    name = "wishlists",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "property_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
}
