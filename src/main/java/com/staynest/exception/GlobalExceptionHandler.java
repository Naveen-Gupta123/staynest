package com.staynest.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * GlobalExceptionHandler - ONE place that catches exceptions thrown
 * anywhere in the app (any controller, any service called from a
 * controller) and turns them into a proper error page instead of
 * Spring's default ugly whitelabel error page or a raw stack trace.
 *
 * @ControllerAdvice makes this class apply GLOBALLY across every
 * @Controller in the project - we don't have to repeat try/catch
 * blocks in every single controller method.
 *
 * @ExceptionHandler(SomeException.class) on each method says: "if a
 * SomeException bubbles up anywhere in a controller, run THIS method
 * to handle it."
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ModelAndView handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/generic");
        mav.addObject("message", ex.getMessage());
        mav.addObject("title", "Already Exists");
        return mav;
    }

    @ExceptionHandler(BookingConflictException.class)
    public ModelAndView handleBookingConflict(BookingConflictException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/generic");
        mav.addObject("message", ex.getMessage());
        mav.addObject("title", "Booking Conflict");
        return mav;
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ModelAndView handleUnauthorized(UnauthorizedActionException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    /**
     * Catch-all safety net: ANY exception we didn't anticipate still
     * gets shown as a clean error page rather than leaking a stack
     * trace to the user (which would also be a security risk in
     * production - stack traces can reveal internal file paths,
     * library versions, and database structure to attackers).
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneric(Exception ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/generic");
        mav.addObject("message", "Something went wrong. Please try again.");
        mav.addObject("title", "Unexpected Error");
        return mav;
    }
}
