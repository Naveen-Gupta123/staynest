package com.staynest.util;

import com.staynest.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityUtil - a small static helper so controllers can grab the
 * currently logged-in User entity in ONE line, instead of repeating
 * "cast Authentication.getPrincipal() to CustomUserDetails, then call
 * getUser()" in every single controller method.
 *
 * WHY STATIC (not a @Component / Spring bean)?
 * SecurityContextHolder itself is a static, thread-local holder
 * provided by Spring Security - there's no per-request state to
 * inject here, so a plain static utility method is simpler and avoids
 * unnecessary Spring wiring for something that's really just a
 * convenience wrapper.
 */
public final class SecurityUtil {

    private SecurityUtil() {
        // prevent instantiation - this class only has static helpers
    }

    public static com.staynest.entity.User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }
}
