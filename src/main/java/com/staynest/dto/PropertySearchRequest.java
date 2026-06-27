package com.staynest.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * PropertySearchRequest - DTO bound to the search filter bar on the
 * homepage / search results page. Every field is OPTIONAL (no
 * @NotBlank/@NotNull) on purpose - a user might search by just city,
 * or just price range, or leave everything blank to browse all listings.
 */
@Data
public class PropertySearchRequest {
    private String city;
    private String state;
    private String country;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer bedrooms;
    private Integer guests;
    private String type;
    private int page = 0;
}
