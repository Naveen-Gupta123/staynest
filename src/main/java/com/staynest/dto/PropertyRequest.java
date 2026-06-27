package com.staynest.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * PropertyRequest - DTO bound to the "Create/Edit Property" form used
 * by Hosts. Notice it does NOT contain a `host` or `id` field for
 * creation - the host is taken from the logged-in session
 * (SecurityContext), never trusted from form input, which prevents a
 * host from creating a listing "as" another host.
 */
@Data
public class PropertyRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.0", message = "Price must be at least 1")
    private BigDecimal pricePerNight;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Property type is required")
    private String type;

    @Min(1)
    private Integer bedrooms;

    @Min(1)
    private Integer bathrooms;

    @Min(1)
    private Integer maxGuests;

    /** Comma-separated amenities string, e.g. "WiFi,Pool,Parking" */
    private String amenities;

    /** Uploaded image files come through as multipart files in the controller, not here. */
    private List<String> existingImageUrls;
}
