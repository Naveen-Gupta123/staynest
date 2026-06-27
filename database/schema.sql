-- ============================================================
-- StayNest - Database Schema
-- ============================================================
-- This script is provided for REFERENCE and for your ER-diagram /
-- viva documentation. You do NOT need to run this manually:
-- Hibernate (spring.jpa.hibernate.ddl-auto=update in
-- application.properties) will create every one of these tables
-- automatically the first time you run the app, based on the
-- @Entity classes in src/main/java/com/staynest/entity.
--
-- Running this file yourself is optional - useful if you want to
-- inspect the exact schema for your project report / viva, or set
-- up the database structure before the very first run.
-- ============================================================

CREATE DATABASE IF NOT EXISTS staynest_db;
USE staynest_db;

-- ---------------------------------------------------------------
-- USERS - stores Guests, Hosts, and Admins in one table.
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(100)  NOT NULL,
    email           VARCHAR(150)  NOT NULL UNIQUE,
    password        VARCHAR(255)  NOT NULL,
    phone           VARCHAR(15),
    bio             VARCHAR(500),
    profile_image   VARCHAR(255),
    role            VARCHAR(20)   NOT NULL DEFAULT 'GUEST',  -- GUEST | HOST | ADMIN
    enabled         BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- PROPERTIES - listings created by Hosts.
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS properties (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(150)   NOT NULL,
    description      TEXT           NOT NULL,
    price_per_night  DECIMAL(10,2)  NOT NULL,
    address          VARCHAR(255)   NOT NULL,
    city             VARCHAR(100)   NOT NULL,
    state            VARCHAR(100)   NOT NULL,
    country          VARCHAR(100)   NOT NULL,
    type             VARCHAR(20)    NOT NULL,   -- APARTMENT|VILLA|HOUSE|CABIN|HOTEL_ROOM
    bedrooms         INT            NOT NULL,
    bathrooms        INT            NOT NULL,
    max_guests       INT            NOT NULL,
    amenities        VARCHAR(500),
    active           BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    host_id          BIGINT         NOT NULL,

    CONSTRAINT fk_property_host FOREIGN KEY (host_id) REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_property_city (city),
    INDEX idx_property_state (state),
    INDEX idx_property_country (country),
    INDEX idx_property_type (type),
    INDEX idx_property_price (price_per_night),
    INDEX idx_property_active (active)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- PROPERTY_IMAGES - one-to-many photo gallery per property.
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS property_images (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url     VARCHAR(255) NOT NULL,
    is_cover      BOOLEAN      NOT NULL DEFAULT FALSE,
    property_id   BIGINT       NOT NULL,

    CONSTRAINT fk_image_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    INDEX idx_image_property (property_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- BOOKINGS - a Guest reserving a Property for a date range.
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    check_in        DATE           NOT NULL,
    check_out       DATE           NOT NULL,
    guests          INT            NOT NULL,
    total_price     DECIMAL(10,2)  NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'PENDING',  -- PENDING|CONFIRMED|REJECTED|CANCELLED|COMPLETED
    payment_status  VARCHAR(20)    NOT NULL DEFAULT 'PENDING',  -- PENDING|PAID|REFUNDED|FAILED
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    property_id     BIGINT         NOT NULL,
    guest_id        BIGINT         NOT NULL,

    CONSTRAINT fk_booking_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_guest    FOREIGN KEY (guest_id)    REFERENCES users(id)      ON DELETE CASCADE,

    INDEX idx_booking_property (property_id),
    INDEX idx_booking_guest (guest_id),
    INDEX idx_booking_status (status),
    INDEX idx_booking_dates (check_in, check_out)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- REVIEWS - a Guest's rating + comment for a Property.
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating       INT          NOT NULL,   -- 1 to 5
    comment      TEXT         NOT NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    property_id  BIGINT       NOT NULL,
    user_id      BIGINT       NOT NULL,

    CONSTRAINT fk_review_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_user     FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE,
    CONSTRAINT chk_review_rating  CHECK (rating BETWEEN 1 AND 5),

    INDEX idx_review_property (property_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- WISHLISTS - Guests saving Properties for later.
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS wishlists (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id      BIGINT   NOT NULL,
    property_id  BIGINT   NOT NULL,

    CONSTRAINT fk_wishlist_user     FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT uq_wishlist_user_property UNIQUE (user_id, property_id)
) ENGINE=InnoDB;

-- ============================================================
-- SAMPLE / SEED DATA (optional - for quick demo & evaluation)
-- Password for ALL seeded accounts below is: Password123
-- (already BCrypt-hashed below, never stored in plain text)
-- ============================================================

INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Admin User',  'admin@staynest.com',  '$2b$10$jOKEssUC.I/8CJvoPCEoLuDKv7ZYZi80lIJ67zFuPL4RcfP.GR.pe', 'ADMIN', TRUE),
('Riya Sharma', 'host1@staynest.com',  '$2b$10$jOKEssUC.I/8CJvoPCEoLuDKv7ZYZi80lIJ67zFuPL4RcfP.GR.pe', 'HOST',  TRUE),
('Aman Verma',  'guest1@staynest.com', '$2b$10$jOKEssUC.I/8CJvoPCEoLuDKv7ZYZi80lIJ67zFuPL4RcfP.GR.pe', 'GUEST', TRUE);

-- NOTE: this hash was generated with bcrypt (cost factor 10) and
-- verified to correctly match the plaintext password "Password123"
-- before being placed in this script. It is reused across all 3 demo
-- rows purely for convenience - in real registrations through the app,
-- BCryptPasswordEncoder generates a fresh random salt (and therefore a
-- different hash) for every single user automatically.
-- Spring Security's BCryptPasswordEncoder accepts the $2a$/$2b$/$2y$
-- hash prefixes interchangeably, so this $2b$ hash works correctly.
