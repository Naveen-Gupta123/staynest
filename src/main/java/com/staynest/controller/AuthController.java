package com.staynest.controller;

import com.staynest.dto.RegisterRequest;
import com.staynest.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AuthController serves the login and registration PAGES, and handles
 * the registration FORM submission.
 *
 * NOTE: the actual LOGIN submission is NOT handled here - Spring
 * Security's formLogin() (configured in SecurityConfig) intercepts
 * POST /auth/login automatically before it ever reaches a controller
 * method. We only need a GET mapping to SHOW the login page.
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login"; // resolves to templates/auth/login.html
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    /**
     * @Valid triggers Bean Validation on the RegisterRequest DTO (the
     * @NotBlank, @Email, @Size annotations we wrote in RegisterRequest).
     * BindingResult MUST be the parameter immediately after the @Valid
     * object - Spring captures any validation failures into it instead
     * of throwing an exception, so we can re-show the form with error
     * messages rather than crashing.
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register"; // re-show form; Thymeleaf displays field errors
        }

        try {
            authService.register(request);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }

        return "redirect:/auth/login?registered=true";
    }
}
