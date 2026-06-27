package com.staynest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Property entity - represents a single listing a Host creates
 * (an apartment, villa, house, cabin, or hotel room).
 *
 * This is the most "connected" entity in the whole app: it relates to
 * User (the host who owns it), PropertyImage (its photos), Booking
 * (guest reservations against it), Review (guest feedback), and
 * Wishlist (guests who saved it).
 */
@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * BigDecimal, NOT double/float, for money.
     * Floating point types (double, float) cannot represent decimal
     * fractions like 19.99 exactly in binary - they introduce tiny
     * rounding errors that compound across many bookings. BigDecimal
     * stores the exact decimal value, which is mandatory for anything
     * involving currency.
     */
    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "1.0", message = "Price must be at least 1")
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @NotBlank(message = "Address is required")
    @Column(nullable = false, length = 255)
    private String address;

    @NotBlank(message = "City is required")
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Column(nullable = false, length = 100)
    private String state;

    @NotBlank(message = "Country is required")
    @Column(nullable = false, length = 100)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PropertyType type;

    @Min(value = 1, message = "Must have at least 1 bedroom")
    @Column(nullable = false)
    private Integer bedrooms;

    @Min(value = 1, message = "Must have at least 1 bathroom")
    @Column(nullable = false)
    private Integer bathrooms;

    @Min(value = 1, message = "Must accommodate at least 1 guest")
    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    /**
     * Stored as a comma-separated string ("WiFi,Pool,Parking,Kitchen")
     * for simplicity in this version. A more advanced version would use
     * a separate Amenity entity + a join table (@ManyToMany), which we
     * can upgrade to later (noted in Phase 2 of the README).
     */
    @Column(length = 500)
    private String amenities;

    /**
     * Lets a host temporarily hide a listing without deleting it -
     * deleting would destroy its booking/review history.
     */
    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * ---- RELATIONSHIPS ----
     * @ManyToOne: many Properties belong to one User (the host).
     * @JoinColumn(name = "host_id") creates the actual foreign key
     * column "host_id" in the `properties` table.
     *
     * fetch = FetchType.LAZY means: don't load the host's full User
     * object from the DB until we actually call property.getHost().
     * This avoids pulling unnecessary data on every property query
     * (an important performance practice - the opposite, EAGER, would
     * fetch the host on every single query even when we don't need it).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wishlist> wishlists = new ArrayList<>();
}
