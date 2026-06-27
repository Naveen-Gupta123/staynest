package com.staynest.controller;

import com.staynest.dto.ChangePasswordRequest;
import com.staynest.dto.ProfileUpdateRequest;
import com.staynest.entity.User;
import com.staynest.service.UserService;
import com.staynest.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String viewProfile(Model model) {
        User user = SecurityUtil.getCurrentUser();
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFullName(user.getFullName());
        request.setPhone(user.getPhone());
        request.setBio(user.getBio());

        model.addAttribute("user", user);
        model.addAttribute("profileUpdateRequest", request);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "profile/view";
    }

    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("profileUpdateRequest") ProfileUpdateRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            Model model
    ) {
        User user = SecurityUtil.getCurrentUser();
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
            return "profile/view";
        }
        userService.updateProfile(user, request, profileImage);
        return "redirect:/profile?updated=true";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        User user = SecurityUtil.getCurrentUser();
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("profileUpdateRequest", new ProfileUpdateRequest());
            return "profile/view";
        }
        try {
            userService.changePassword(user, request);
        } catch (Exception ex) {
            model.addAttribute("user", user);
            model.addAttribute("profileUpdateRequest", new ProfileUpdateRequest());
            model.addAttribute("passwordError", ex.getMessage());
            return "profile/view";
        }
        return "redirect:/profile?passwordChanged=true";
    }
}
