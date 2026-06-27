package com.staynest.entity;

/**
 * PropertyType is the category of listing a host creates.
 * Stored as a String in the DB (see Property.type) for the same
 * readability reason as Role.
 */
public enum PropertyType {
    APARTMENT,
    VILLA,
    HOUSE,
    CABIN,
    HOTEL_ROOM
}
