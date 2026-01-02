package com.bakery.user.dto;

import com.bakery.user.Role;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating an existing user.
 * All fields are optional - only update what's provided.
 *
 * TODO: Add validation annotations similar to CreateUserRequest
 * but all fields are optional (no @NotBlank)
 */
@Getter
@Setter
public class UpdateUserRequest {

    // TODO 1: Optional, but if provided must be 3-50 characters
    private String username;

    // TODO 2: Optional, but if provided must be min 6 characters
    // Note: Should be hashed before saving!
    private String password;

    // TODO 3: Optional, max 100 characters
    private String fullName;

    // TODO 4: Optional phone
    private String phone;

    // TODO 5: Optional role change
    private Role role;

    // TODO 6: Optional - for soft delete/restore
    private Boolean isActive;
}
