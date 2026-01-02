package com.bakery.user.exception;

/**
 * Thrown when the provided current password doesn't match.
 */
public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super("Current password is incorrect");
    }
}
