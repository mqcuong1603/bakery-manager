package com.bakery.common.response;

import java.time.Instant;

public record ApiResponse<T>(
        int status,
        String message,
        T data,
        Instant timestamp) {

    public static <T> ApiResponse<T> of(ApiStatus status, T data) {
        return new ApiResponse<>(
                status.code(),
                status.message(),
                data,
                Instant.now());
    }

    public static ApiResponse<Void> error(ApiStatus status) {
        return of(status, null);
    }

    public static ApiResponse<Void> error(ApiStatus status, String message) {
        return new ApiResponse<>(
                status.code(),
                message,
                null,
                Instant.now());
    }
}
