package com.bakery.user;

import com.bakery.user.dto.CreateUserRequest;
import com.bakery.user.dto.UpdateUserRequest;
import com.bakery.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User operations.
 *
 * TODO: Implement all endpoints below.
 *
 * Key concepts:
 * - @RestController: Combines @Controller + @ResponseBody
 * - @RequestMapping: Base path for all endpoints
 * - @Valid: Triggers validation on request body
 * - ResponseEntity: Allows setting HTTP status codes
 *
 * HTTP Status codes to use:
 * - 200 OK: Successful GET, PUT
 * - 201 Created: Successful POST
 * - 204 No Content: Successful DELETE
 * - 400 Bad Request: Validation errors
 * - 404 Not Found: Resource not found
 * - 409 Conflict: Duplicate username
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * POST /api/users - Create a new user
     *
     * TODO: Implement
     * - Use @Valid to validate request body
     * - Return 201 Created with the created user
     *
     * Example:
     * @PostMapping
     * public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
     *     UserResponse response = userService.createUser(request, null);
     *     return ResponseEntity.status(HttpStatus.CREATED).body(response);
     * }
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        // TODO: Implement
        return null;
    }

    /**
     * GET /api/users - Get all users
     *
     * TODO: Implement
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        // TODO: Implement
        return null;
    }

    /**
     * GET /api/users/{id} - Get user by ID
     *
     * TODO: Implement using @PathVariable
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        // TODO: Implement
        return null;
    }

    /**
     * GET /api/users/active - Get all active users
     *
     * TODO: Implement
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        // TODO: Implement
        return null;
    }

    /**
     * GET /api/users/role/{role} - Get users by role
     *
     * TODO: Implement
     * Example: GET /api/users/role/STAFF
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        // TODO: Implement
        return null;
    }

    /**
     * PUT /api/users/{id} - Update user
     *
     * TODO: Implement
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        // TODO: Implement
        return null;
    }

    /**
     * DELETE /api/users/{id} - Soft delete user
     *
     * TODO: Implement
     * - Return 204 No Content on success
     *
     * Example:
     * userService.deleteUser(id);
     * return ResponseEntity.noContent().build();
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // TODO: Implement
        return null;
    }
}
