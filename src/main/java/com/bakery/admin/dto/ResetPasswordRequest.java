package com.bakery.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 20, message = "New password must be 6-20 characters")
    private String newPassword;
}
