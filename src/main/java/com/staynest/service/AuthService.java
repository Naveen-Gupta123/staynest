package com.staynest.service;

import com.staynest.dto.RegisterRequest;
import com.staynest.entity.User;

/**
 * AuthService interface - defines WHAT authentication-related operations
 * exist, without saying HOW they're implemented.
 *
 * WHY INTERFACE + IMPLEMENTATION (instead of just one class)?
 * This is the "Service Layer" pattern from your requirements. Controllers
 * depend on this INTERFACE, not the concrete AuthServiceImpl class. That
 * means:
 *   1. We could swap in a different implementation later (e.g. one that
 *      also sends a welcome email) without touching any controller code.
 *   2. It makes unit testing easier - in tests we can create a fake/mock
 *      AuthService that implements this interface without touching a
 *      real database.
 */
public interface AuthService {

    /**
     * Validates the registration data, checks the email isn't already
     * taken, hashes the password, and saves a new User.
     */
    User register(RegisterRequest request);
}
