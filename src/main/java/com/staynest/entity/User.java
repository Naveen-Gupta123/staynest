package com.staynest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity - represents every person who uses StayNest: guests, hosts,
 * and admins. We use ONE table for all three roles (rather than three
 * separate tables) because they share the same core fields (name, email,
 * password) and a person can technically be a guest who ALSO becomes a
 * host later. The `role` column is what distinguishes their permissions.
 *
 * ANNOTATIONS EXPLAINED:
 * @Entity              -> tells Hibernate "this class maps to a database table"
 * @Table(name="users") -> the actual table name. We avoid the word "user"
 *                          because it's a reserved keyword in some databases.
 * @Data (Lombok)       -> auto-generates getters, setters, toString(),
 *                          equals() and hashCode() at compile time, so we
 *                          don't have to hand-write 100+ lines of boilerplate.
 * @NoArgsConstructor / @AllArgsConstructor -> generates a no-args and a
 *                          full-args constructor (JPA REQUIRES a no-args
 *                          constructor to work).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * @Id marks this as the primary key.
     * @GeneratedValue(strategy = IDENTITY) tells MySQL to auto-increment
     * this column itself (1, 2, 3, ...) - we never set it manually.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false, length = 100)
    private String fullName;

    /**
     * unique = true creates a UNIQUE constraint in MySQL, so two users
     * can never register with the same email - the database itself
     * enforces this, even if our Java validation somehow gets bypassed.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * IMPORTANT: this column never stores the plain text password.
     * PasswordEncoder (BCrypt) hashes it before it ever reaches this
     * field - see AuthServiceImpl.register(). BCrypt hashes are ~60
     * characters, hence length = 255 to be safe.
     */
    @NotBlank(message = "Password is required")
    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 15)
    private String phone;

    @Column(length = 500)
    private String bio;

    @Column(name = "profile_image")
    private String profileImage;

    /**
     * @Enumerated(EnumType.STRING) stores the role as readable text
     * ("ADMIN", "HOST", "GUEST") in the database instead of a number
     * (0, 1, 2). Numbers break silently if you ever reorder the enum;
     * strings are self-explanatory when you inspect the table directly.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.GUEST;

    /**
     * Soft-disable flag. Instead of deleting a problem user (which would
     * also wipe their booking history / reviews via cascade), the admin
     * can just deactivate them. Spring Security checks this via
     * UserDetails.isEnabled().
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * @CreationTimestamp auto-fills this the moment the row is first
     * saved - we never set it manually.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * ---- RELATIONSHIPS ----
     * mappedBy = "host" means: "the foreign key for this relationship
     * lives on the OTHER side (Property.host), not here." This is the
     * "one" side of a one-to-many: one User (host) -> many Properties.
     *
     * cascade = CascadeType.ALL means: if we delete this User, also
     * delete all their Properties. orphanRemoval = true means: if a
     * Property is removed from this list, delete it from the DB too.
     *
     * We initialize with `= new ArrayList<>()` to avoid NullPointerException
     * when a brand-new User hasn't got any properties yet.
     */
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Property> properties = new ArrayList<>();

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wishlist> wishlists = new ArrayList<>();
}
