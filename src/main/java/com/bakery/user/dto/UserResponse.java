package com.bakery.user.dto;

import com.bakery.user.Role;
import com.bakery.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for returning user data to client.
 * Note: Never include password in response!
 *
 * TODO: Implement the static factory method fromEntity()
 */
@Getter
@Setter
public class UserResponse {

    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;  // Just the ID, not the full User object

    /**
     * Convert User entity to UserResponse DTO.
     *
     * TODO: Implement this method
     * - Create new UserResponse
     * - Copy all fields from User entity
     * - For createdBy, just get the ID (if not null)
     * - Return the UserResponse
     *
     * Example:
     * UserResponse response = new UserResponse();
     * response.setId(user.getId());
     * response.setUsername(user.getUsername());
     * ... etc
     * return response;
     */
    public static UserResponse fromEntity(User user) {
        // TODO: Implement this conversion
        return null;
    }
}
