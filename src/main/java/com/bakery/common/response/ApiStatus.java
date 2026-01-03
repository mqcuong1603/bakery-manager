package com.bakery.common.response;

public enum ApiStatus {
    SUCCESS(200, "Success"),
    CREATED(201, "Created successfully"),
    NOT_FOUND(404, "Resource not found"),
    BAD_REQUEST(400, "Bad request"),
    INTERNAL_ERROR(500, "Internal server error"),
    NO_CONTENT(204, "No content"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    CONFLICT(409, "Conflict"),
    VALIDATION_ERROR(422, "Validation error");

    private final int code;
    private final String message;

    ApiStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
