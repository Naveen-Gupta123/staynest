package com.staynest.controller;

import com.staynest.dto.PropertySearchRequest;
import com.staynest.dto.ReviewRequest;
import com.staynest.entity.Property;
import com.staynest.entity.User;
import com.staynest.service.PropertyService;
import com.staynest.service.ReviewService;
import com.staynest.service.WishlistService;
import com.staynest.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * PropertyController handles the PUBLIC browsing experience: search
 * results and an individual property's detail page. Both are open to
 * everyone (see SecurityConfig's permitAll() for "/properties/**"),
 * since you don't need an account to browse listings on Airbnb either -
 * only to actually book one.
 */
@Controller
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final ReviewService reviewService;
    private final WishlistService wishlistService;

    /**
     * @ModelAttribute here means: take all matching query parameters
     * from the URL (?city=Goa&minPrice=1000&...) and automatically bind
     * them onto a PropertySearchRequest object - no manual parsing needed.
     */
    @GetMapping
    public String search(@ModelAttribute PropertySearchRequest searchRequest, Model model) {
        Page<Property> results = propertyService.search(searchRequest);
        model.addAttribute("page", results);
        model.addAttribute("properties", results.getContent());
        model.addAttribute("searchRequest", searchRequest);
        return "property/search";
    }

    @GetMapping("/view/{id}")
    public String viewProperty(@PathVariable Long id, Model model) {
        Property property = propertyService.getPropertyById(id);
        model.addAttribute("property", property);
        model.addAttribute("reviews", reviewService.getReviewsForProperty(property));
        model.addAttribute("averageRating", reviewService.getAverageRating(id));

        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("isWishlisted", wishlistService.isPropertyWishlisted(id, currentUser));
        }
        model.addAttribute("reviewRequest", new ReviewRequest());

        return "property/detail";
    }
}
