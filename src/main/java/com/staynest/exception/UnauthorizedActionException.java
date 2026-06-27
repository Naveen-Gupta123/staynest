package com.staynest.exception;

/**
 * Thrown when a logged-in user tries to act on something they don't
 * own - e.g. Host A trying to edit Host B's property, even though both
 * are HOSTs. URL-level role rules in SecurityConfig can't catch this
 * (they only check ROLE, not OWNERSHIP), so we check ownership manually
 * in the service layer and throw this when it fails.
 */
public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
