package com.bakery.user.exception;

/**
 * Thrown when trying to create a user with a username that already exists.
 *
 * TODO: This exception is complete.
 *
 * Usage in service:
 * if (userRepository.existsByUsername(username)) {
 *     throw new UsernameAlreadyExistsException(username);
 * }
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String username) {
        super("Username already exists: " + username);
    }
}
