package com.staynest.controller;

import com.staynest.dto.PropertyRequest;
import com.staynest.entity.User;
import com.staynest.service.BookingService;
import com.staynest.service.PropertyService;
import com.staynest.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * HostController - every page/action a HOST uses: their dashboard,
 * creating/editing/deleting their own listings, and confirming or
 * rejecting bookings made against those listings.
 *
 * All mappings live under "/host/**", which SecurityConfig restricts
 * to hasRole("HOST") at the URL level. Ownership (host A can't edit
 * host B's listing) is enforced separately inside PropertyService /
 * BookingService, as explained there.
 */
@Controller
@RequestMapping("/host")
@RequiredArgsConstructor
public class HostController {

    private final PropertyService propertyService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User host = SecurityUtil.getCurrentUser();
        model.addAttribute("properties", propertyService.getPropertiesByHost(host));
        model.addAttribute("bookings", bookingService.getBookingsByHost(host));
        return "host/dashboard";
    }

    @GetMapping("/properties/new")
    public String newPropertyForm(Model model) {
        model.addAttribute("propertyRequest", new PropertyRequest());
        return "host/property-form";
    }

    @PostMapping("/properties/create")
    public String createProperty(
            @Valid @ModelAttribute("propertyRequest") PropertyRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "host/property-form";
        }
        User host = SecurityUtil.getCurrentUser();
        try {
            propertyService.createProperty(request, images, host);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "host/property-form";
        }
        return "redirect:/host/dashboard";
    }

    @GetMapping("/properties/edit/{id}")
    public String editPropertyForm(@PathVariable Long id, Model model) {
        var property = propertyService.getPropertyById(id);

        PropertyRequest request = new PropertyRequest();
        request.setTitle(property.getTitle());
        request.setDescription(property.getDescription());
        request.setPricePerNight(property.getPricePerNight());
        request.setAddress(property.getAddress());
        request.setCity(property.getCity());
        request.setState(property.getState());
        request.setCountry(property.getCountry());
        request.setType(property.getType().name());
        request.setBedrooms(property.getBedrooms());
        request.setBathrooms(property.getBathrooms());
        request.setMaxGuests(property.getMaxGuests());
        request.setAmenities(property.getAmenities());

        model.addAttribute("propertyRequest", request);
        model.addAttribute("propertyId", id);
        model.addAttribute("existingImages", property.getImages());
        return "host/property-form";
    }

    @PostMapping("/properties/update/{id}")
    public String updateProperty(
            @PathVariable Long id,
            @Valid @ModelAttribute("propertyRequest") PropertyRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("propertyId", id);
            return "host/property-form";
        }
        User host = SecurityUtil.getCurrentUser();
        try {
            propertyService.updateProperty(id, request, images, host);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("propertyId", id);
            return "host/property-form";
        }
        return "redirect:/host/dashboard";
    }

    @PostMapping("/properties/delete/{id}")
    public String deleteProperty(@PathVariable Long id) {
        User host = SecurityUtil.getCurrentUser();
        propertyService.deleteProperty(id, host);
        return "redirect:/host/dashboard";
    }

    @GetMapping("/bookings")
    public String hostBookings(Model model) {
        User host = SecurityUtil.getCurrentUser();
        model.addAttribute("bookings", bookingService.getBookingsByHost(host));
        return "host/bookings";
    }

    @PostMapping("/bookings/confirm/{id}")
    public String confirmBooking(@PathVariable Long id) {
        User host = SecurityUtil.getCurrentUser();
        bookingService.confirmBooking(id, host);
        return "redirect:/host/bookings";
    }

    @PostMapping("/bookings/reject/{id}")
    public String rejectBooking(@PathVariable Long id) {
        User host = SecurityUtil.getCurrentUser();
        bookingService.rejectBooking(id, host);
        return "redirect:/host/bookings";
    }
}
