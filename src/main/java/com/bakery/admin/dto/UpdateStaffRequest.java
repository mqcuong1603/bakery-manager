package com.bakery.admin.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStaffRequest {

    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @Pattern(regexp = "^0\\d{9}$", message = "Phone must be 10 digits and start with 0")
    private String phone;

    private Boolean isActive;
}
