package com.staynest.security;

import com.staynest.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * CustomUserDetails ADAPTS our own `User` entity to the `UserDetails`
 * interface that Spring Security requires internally.
 *
 * WHY WE NEED THIS ADAPTER (instead of making User implement UserDetails
 * directly): keeping UserDetails separate means our `entity.User` class
 * stays a pure database/business model with ZERO Spring Security
 * dependencies. If we ever swap authentication frameworks, only this one
 * adapter class changes - the entity is untouched. This is the
 * "Adapter Pattern" and a clean separation of concerns.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /** Lets controllers/services get back the original entity when needed. */
    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    /**
     * Spring Security represents permissions as "authorities", which by
     * convention must be prefixed "ROLE_" to work with hasRole("ADMIN")
     * checks in @PreAuthorize / security config. Our enum just stores
     * "ADMIN" - we prepend "ROLE_" only here, at the security boundary.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * We log users in by EMAIL, not a separate "username" field, so
     * getUsername() returns the email. Spring Security doesn't care what
     * you call it internally - it just needs SOME unique identifier string.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Backed by our `enabled` flag - lets admins deactivate accounts. */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
