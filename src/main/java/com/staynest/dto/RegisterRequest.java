package com.staynest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * RegisterRequest - a DTO (Data Transfer Object) used ONLY for the
 * registration form.
 *
 * WHY NOT JUST BIND THE FORM DIRECTLY TO THE `User` ENTITY?
 * Two important reasons:
 *  1. SECURITY: if we bound forms directly to User, a malicious user
 *     could add hidden fields to the HTML form (e.g. role=ADMIN,
 *     enabled=true) and submit them - Spring would happily bind those
 *     onto the entity too ("mass assignment" vulnerability). A DTO only
 *     exposes the exact fields we intend to accept from the browser.
 *  2. SEPARATION OF CONCERNS: the entity represents the DATABASE shape;
 *     the DTO represents the FORM/API shape. They often need different
 *     validation rules (e.g. a "confirmPassword" field that only makes
 *     sense on the form, never in the database).
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    /** "GUEST" or "HOST" - chosen at signup. Admin accounts are never self-registered. */
    @NotBlank(message = "Please select a role")
    private String role;
}
