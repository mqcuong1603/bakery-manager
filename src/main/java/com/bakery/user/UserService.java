package com.bakery.user;

import com.bakery.user.dto.CreateUserRequest;
import com.bakery.user.dto.UpdateUserRequest;
import com.bakery.user.dto.UserResponse;

import java.util.List;

/**
 * Service interface for User operations.
 * Defines the business logic contract.
 *
 * TODO: This interface is complete.
 * You need to implement these methods in UserServiceImpl.
 */
public interface UserService {

    /**
     * Create a new user.
     * @param request User creation data
     * @param createdById ID of user creating this user (can be null for first user)
     * @return Created user data
     * @throws UsernameAlreadyExistsException if username taken
     */
    UserResponse createUser(CreateUserRequest request, Long createdById);

    /**
     * Get user by ID.
     * @param id User ID
     * @return User data
     * @throws UserNotFoundException if not found
     */
    UserResponse getUserById(Long id);

    /**
     * Get user by username (for login).
     * @param username Username
     * @return User data
     * @throws UserNotFoundException if not found
     */
    UserResponse getUserByUsername(String username);

    /**
     * Get all users.
     * @return List of all users
     */
    List<UserResponse> getAllUsers();

    /**
     * Get all active users.
     * @return List of active users
     */
    List<UserResponse> getActiveUsers();

    /**
     * Get all users by role.
     * @param role OWNER or STAFF
     * @return List of users with specified role
     */
    List<UserResponse> getUsersByRole(Role role);

    /**
     * Update an existing user.
     * @param id User ID to update
     * @param request Update data
     * @return Updated user data
     * @throws UserNotFoundException if not found
     * @throws UsernameAlreadyExistsException if new username taken
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Soft delete a user (set isActive = false).
     * @param id User ID
     * @throws UserNotFoundException if not found
     */
    void deleteUser(Long id);
}
