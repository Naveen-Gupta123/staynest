package com.staynest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;
}
