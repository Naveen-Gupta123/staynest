package com.staynest.entity;

/**
 * Role represents the three types of users in StayNest.
 *
 * WHY AN ENUM (not just a String column)?
 * Using an enum means the database can only ever store one of these three
 * fixed values - the compiler catches typos like "ADIMN" before the code
 * even runs. Spring Security reads this value to decide what a logged-in
 * user is allowed to see and do (this is "Role Based Authentication").
 *
 *  - GUEST: default role on signup. Can search, book, review, wishlist.
 *  - HOST:  can list properties and manage bookings on their own listings.
 *  - ADMIN: full platform control - manage users, properties, bookings.
 *
 * Spring Security convention requires authorities to be prefixed with
 * "ROLE_" (e.g. ROLE_ADMIN). We keep the enum names clean (ADMIN, HOST,
 * GUEST) and add the "ROLE_" prefix only where Spring Security needs it -
 * see CustomUserDetailsService.
 */
public enum Role {
    GUEST,
    HOST,
    ADMIN
}
