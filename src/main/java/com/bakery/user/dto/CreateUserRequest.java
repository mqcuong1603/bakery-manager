package com.bakery.user.dto;

import com.bakery.user.Role;
import jakarta.validation.constraints.NotBlank;
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
    @Size(min = 3, max = 50)
    private String fullName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    // TODO 5: Add validation
    // - Required, must be OWNER or STAFF
    // Hint: @NotNull
    private Role role;
}
