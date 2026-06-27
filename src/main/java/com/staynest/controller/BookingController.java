package com.staynest.controller;

import com.staynest.dto.BookingRequest;
import com.staynest.entity.User;
import com.staynest.service.BookingService;
import com.staynest.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * BookingController - everything a GUEST does with bookings: create,
 * view their own list, and cancel. (Host-side accept/reject lives in
 * HostController, since the access rules differ.)
 */
@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/new/{propertyId}")
    public String newBookingForm(@PathVariable Long propertyId, Model model) {
        model.addAttribute("propertyId", propertyId);
        model.addAttribute("bookingRequest", new BookingRequest());
        return "booking/new";
    }

    @PostMapping("/create/{propertyId}")
    public String createBooking(
            @PathVariable Long propertyId,
            @Valid @ModelAttribute("bookingRequest") BookingRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("propertyId", propertyId);
            return "booking/new";
        }

        User guest = SecurityUtil.getCurrentUser();
        try {
            var booking = bookingService.createBooking(propertyId, request, guest);
            return "redirect:/booking/confirmation/" + booking.getId();
        } catch (Exception ex) {
            model.addAttribute("propertyId", propertyId);
            model.addAttribute("errorMessage", ex.getMessage());
            return "booking/new";
        }
    }

    @GetMapping("/confirmation/{id}")
    public String confirmation(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookingService.getBookingById(id));
        return "booking/confirmation";
    }

    @GetMapping("/my-bookings")
    public String myBookings(Model model) {
        User guest = SecurityUtil.getCurrentUser();
        model.addAttribute("bookings", bookingService.getBookingsByGuest(guest));
        return "booking/my-bookings";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {
        User guest = SecurityUtil.getCurrentUser();
        bookingService.cancelBooking(id, guest);
        return "redirect:/booking/my-bookings";
    }
}
