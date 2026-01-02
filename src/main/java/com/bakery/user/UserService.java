package com.bakery.user;

import com.bakery.user.dto.ChangePasswordRequest;
import com.bakery.user.dto.CreateUserRequest;
import com.bakery.user.dto.UpdateUserRequest;
import com.bakery.user.dto.UserResponse;

import java.util.List;

public interface UserService {

    /**
     * Create a new staff member.
     * Role is always STAFF (OWNER is seeded).
     */
    UserResponse createStaff(CreateUserRequest request);

    /**
     * Get all staff members.
     */
    List<UserResponse> findAllStaff();

    /**
     * Get user by ID.
     */
    UserResponse findById(Long id);

    /**
     * Update user information.
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Soft delete - set isActive = false.
     */
    void deactivateUser(Long id);

    /**
     * Restore user - set isActive = true.
     */
    void activateUser(Long id);

    /**
     * Change user password.
     * Requires current password verification.
     */
    void changePassword(Long id, ChangePasswordRequest request);
}