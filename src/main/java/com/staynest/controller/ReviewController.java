package com.staynest.controller;

import com.staynest.dto.ReviewRequest;
import com.staynest.entity.User;
import com.staynest.service.ReviewService;
import com.staynest.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/add/{propertyId}")
    public String addReview(
            @PathVariable Long propertyId,
            @Valid @ModelAttribute("reviewRequest") ReviewRequest request,
            BindingResult bindingResult
    ) {
        User user = SecurityUtil.getCurrentUser();
        if (!bindingResult.hasErrors()) {
            reviewService.addReview(propertyId, request, user);
        }
        return "redirect:/properties/view/" + propertyId;
    }

    @PostMapping("/delete/{reviewId}/{propertyId}")
    public String deleteReview(@PathVariable Long reviewId, @PathVariable Long propertyId) {
        User user = SecurityUtil.getCurrentUser();
        reviewService.deleteReview(reviewId, user);
        return "redirect:/properties/view/" + propertyId;
    }
}
