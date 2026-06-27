package com.staynest.config;

import com.staynest.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig is the heart of all authentication & authorization rules
 * in StayNest. This is the modern Spring Security 6 style: instead of
 * extending WebSecurityConfigurerAdapter (deprecated), we expose a
 * SecurityFilterChain @Bean directly.
 *
 * @EnableWebSecurity      -> activates Spring Security's web support
 * @EnableMethodSecurity   -> lets us use @PreAuthorize("hasRole('ADMIN')")
 *                             directly on individual controller/service
 *                             methods for fine-grained control, on top of
 *                             the URL-pattern rules defined below.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * BCryptPasswordEncoder is a one-way hashing algorithm specifically
     * designed for passwords. It automatically:
     *  - generates a random "salt" for every password (so two users with
     *    the same password get completely different hashes in the DB)
     *  - is deliberately SLOW (computationally expensive), which makes
     *    brute-force attacks impractical even if the database leaks.
     * We NEVER store or compare raw passwords - only their BCrypt hash.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationProvider tells Spring Security exactly HOW to verify
     * a login attempt: "look the user up via userDetailsService, then
     * compare passwords using passwordEncoder."
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authProvider) {
        return new org.springframework.security.authentication.ProviderManager(authProvider);
    }

    /**
     * THE MAIN SECURITY RULEBOOK.
     *
     * authorizeHttpRequests() defines which URL patterns require which
     * role. Spring Security checks these IN ORDER, top to bottom, and
     * uses the FIRST matching rule - so order matters (more specific
     * rules must come before general ones).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public pages: anyone (even logged-out visitors) can access these.
                .requestMatchers(
                    "/", "/home",
                    "/auth/**",
                    "/properties", "/properties/view/**", "/properties/search",
                    "/css/**", "/js/**", "/images/**", "/uploads/**",
                    "/error"
                ).permitAll()

                // Admin-only area.
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Host-only area (creating/managing listings).
                .requestMatchers("/host/**").hasRole("HOST")

                // Anything under /booking, /wishlist, /profile requires SOME
                // logged-in account - guest, host, or admin - hence
                // hasAnyRole instead of permitAll.
                .requestMatchers("/booking/**", "/wishlist/**", "/profile/**", "/review/**")
                    .hasAnyRole("GUEST", "HOST", "ADMIN")

                // Anything not explicitly listed above still requires login.
                // This is the SAFE DEFAULT (deny-by-default) rather than
                // accidentally leaving a new page public by forgetting to
                // list it.
                .anyRequest().authenticated()
            )

            // ---- LOGIN CONFIGURATION ----
            .formLogin(form -> form
                .loginPage("/auth/login")          // our custom login page
                .loginProcessingUrl("/auth/login") // the URL the login FORM submits to
                .defaultSuccessUrl("/", true)       // redirect here after successful login
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )

            // ---- LOGOUT CONFIGURATION ----
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            /*
             * CSRF (Cross-Site Request Forgery) protection is ENABLED by
             * default in Spring Security - we are NOT disabling it. Every
             * Thymeleaf <form> we write automatically includes a hidden
             * CSRF token field (Thymeleaf does this for us when the form
             * uses th:action), so legitimate form submissions work fine
             * while forged cross-site requests are rejected.
             */

            // Access-denied page for logged-in users who try to access a
            // role they don't have (e.g. a GUEST trying to hit /admin/**).
            .exceptionHandling(ex -> ex.accessDeniedPage("/error/403"));

        return http.build();
    }
}
