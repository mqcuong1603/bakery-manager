package com.bakery.user.exception;

/**
 * Thrown when a user is not found by ID or username.
 *
 * TODO: This exception is complete. Study how it works:
 * - Extends RuntimeException (unchecked exception)
 * - Provides meaningful error messages
 *
 * Usage in service:
 * User user = userRepository.findById(id)
 *     .orElseThrow(() -> new UserNotFoundException(id));
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(String username) {
        super("User not found with username: " + username);
    }
}
