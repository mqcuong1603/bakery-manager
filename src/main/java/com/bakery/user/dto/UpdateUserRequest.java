package com.bakery.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @Size(min = 3, max = 50)
    private String username;

    @Size(min = 6)
    private String password;

    @Size(max = 100)
    private String fullName;

    @Pattern(regexp = "^0\\d{9}$", message = "Phone must be 10 digits and start with 0")
    private String phone;

    private Boolean isActive;
}