package com.staynest.service.impl;

import com.staynest.dto.RegisterRequest;
import com.staynest.entity.Role;
import com.staynest.entity.User;
import com.staynest.exception.DuplicateResourceException;
import com.staynest.repository.UserRepository;
import com.staynest.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthServiceImpl - the concrete implementation of AuthService.
 *
 * @Service marks this as a Spring-managed service bean (it gets
 * auto-detected by @ComponentScan and instantiated once, then injected
 * wherever AuthService is needed - this is "Dependency Injection").
 *
 * @Transactional means: if ANYTHING inside register() throws an
 * exception partway through, every database change made so far in this
 * method is automatically ROLLED BACK. For register() specifically this
 * matters less (it's one simple save), but it's a habit we'll rely on
 * heavily in the Booking service where multiple related writes must
 * all succeed together or not at all.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterRequest request) {

        // ---- Business rule 1: email must be unique ----
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                "An account with email '" + request.getEmail() + "' already exists. Try logging in instead."
            );
        }

        // ---- Business rule 2: password and confirmPassword must match ----
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match.");
        }

        // ---- Business rule 3: only GUEST or HOST can be self-registered ----
        // (ADMIN accounts are created directly in the database / by another
        // admin - never through the public signup form, for security.)
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
            if (role == Role.ADMIN) {
                throw new IllegalArgumentException("Cannot self-register as ADMIN.");
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role selected. Choose GUEST or HOST.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        // CRITICAL LINE: we NEVER store request.getPassword() directly.
        // passwordEncoder.encode() runs it through BCrypt, producing a
        // one-way hash. Even Anthropic's Claude (or anyone with database
        // access) cannot reverse this back into the original password.
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(role);
        user.setEnabled(true);

        return userRepository.save(user);
    }
}
