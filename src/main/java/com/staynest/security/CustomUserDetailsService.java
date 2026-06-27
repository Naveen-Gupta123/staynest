package com.staynest.security;

import com.staynest.entity.User;
import com.staynest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService is the bridge between Spring Security's login
 * process and OUR database.
 *
 * WHEN THIS RUNS: every time someone submits the login form, Spring
 * Security automatically calls loadUserByUsername(email) internally,
 * then compares the returned UserDetails' password (hashed) against
 * what the user typed (also hashed on the fly) using PasswordEncoder.
 * We never write that comparison ourselves - Spring Security's
 * DaoAuthenticationProvider does it for us, as long as we implement
 * this one method correctly.
 *
 * @RequiredArgsConstructor (Lombok) auto-generates a constructor that
 * takes every `final` field as a parameter - this is how we get
 * UserRepository injected without writing the constructor by hand.
 * This pattern is called "constructor injection" and is the
 * recommended way to wire dependencies in Spring (safer than
 * @Autowired on a field, because it makes dependencies explicit and
 * the class immutable).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No account found with email: " + email));
        return new CustomUserDetails(user);
    }
}
