package com.bakery.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStaffRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be 6-20 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be 3-100 characters")
    private String fullName;

    @Pattern(regexp = "^0\\d{9}$", message = "Phone must be 10 digits and start with 0")
    private String phone;
}
