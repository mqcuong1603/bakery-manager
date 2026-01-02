package com.bakery.user.dto;

import com.bakery.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * - @NotBlank: String must not be null and must have at least one non-whitespace character
 * - @NotNull: Field must not be null
 * - @Size(min, max): String length constraints
 * - @Pattern(regexp): Must match a regexp pattern
 */
@Getter
@Setter
public class CreateUserRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank
    @Size(min = 3, max = 100)
    private String fullName;

    @Pattern(regexp = "^0\\d{9}$", message = "Phone must be 10 digits and start with 0")
    private String phone;

    @NotNull
    private Role role;
}
