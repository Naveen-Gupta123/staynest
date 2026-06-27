package com.staynest.controller;

import com.staynest.entity.User;
import com.staynest.service.WishlistService;
import com.staynest.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public String viewWishlist(Model model) {
        User user = SecurityUtil.getCurrentUser();
        model.addAttribute("wishlists", wishlistService.getWishlistByUser(user));
        return "profile/wishlist";
    }

    @PostMapping("/add/{propertyId}")
    public String add(@PathVariable Long propertyId) {
        User user = SecurityUtil.getCurrentUser();
        wishlistService.addToWishlist(propertyId, user);
        return "redirect:/properties/view/" + propertyId;
    }

    @PostMapping("/remove/{propertyId}")
    public String remove(@PathVariable Long propertyId) {
        User user = SecurityUtil.getCurrentUser();
        wishlistService.removeFromWishlist(propertyId, user);
        return "redirect:/wishlist";
    }
}
