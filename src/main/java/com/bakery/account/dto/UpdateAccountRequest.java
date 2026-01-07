package com.bakery.account.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccountRequest {

    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @Pattern(regexp = "^0\\d{9}$", message = "Phone must be 10 digits and start with 0")
    private String phone;
}
