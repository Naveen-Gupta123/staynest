package com.staynest.controller;

import com.staynest.entity.BookingStatus;
import com.staynest.service.BookingService;
import com.staynest.service.PropertyService;
import com.staynest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AdminController - dashboard with platform-wide stats, plus user and
 * property management. Restricted to hasRole("ADMIN") in SecurityConfig.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PropertyService propertyService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalProperties", propertyService.getAllActiveProperties().size());
        model.addAttribute("pendingBookings", bookingService.countByStatus(BookingStatus.PENDING));
        model.addAttribute("confirmedBookings", bookingService.countByStatus(BookingStatus.CONFIRMED));
        model.addAttribute("recentBookings", bookingService.getRecentBookings());
        model.addAttribute("topProperties", propertyService.getTopBookedProperties());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id) {
        userService.toggleUserEnabled(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/properties")
    public String manageProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllActiveProperties());
        return "admin/properties";
    }

    @PostMapping("/properties/toggle/{id}")
    public String toggleProperty(@PathVariable Long id) {
        propertyService.toggleActiveStatus(id);
        return "redirect:/admin/properties";
    }

    @GetMapping("/bookings")
    public String manageBookings(Model model) {
        model.addAttribute("bookings", bookingService.getRecentBookings());
        return "admin/bookings";
    }
}
