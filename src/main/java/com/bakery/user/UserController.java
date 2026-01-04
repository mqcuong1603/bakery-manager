package com.bakery.user;

import com.bakery.user.dto.ChangePasswordRequest;
import com.bakery.user.dto.CreateUserRequest;
import com.bakery.user.dto.UpdateUserRequest;
import com.bakery.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Staff Management Controller
 * <p>
 * Endpoints:
 * - POST   /api/staff              - U-03: Create Staff Account (OWNER)
 * - GET    /api/staff              - U-04: View All Staff (OWNER)
 * - GET    /api/staff/{id}         - Get single staff (OWNER)
 * - PUT    /api/staff/{id}         - U-05: Update Staff Info (OWNER)
 * - DELETE /api/staff/{id}         - U-06: Deactivate Staff (OWNER)
 * - PUT    /api/staff/{id}/activate - Activate Staff (OWNER)
 * - PUT    /api/staff/{id}/password - U-07: Change Password (OWNER or SELF)
 */
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * @param request Staff creation data
     * @return Created staff with 201 status
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponse> createStaff(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * U-04: View All Staff
     * Only OWNER can view all staff members.
     *
     * @return List of all staff members
     */
    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<UserResponse>> getAllStaff() {
        List<UserResponse> staffList = userService.findAllStaff();
        return ResponseEntity.ok(staffList);
    }

    /**
     * Get Staff by ID
     * Only OWNER can view staff details.
     *
     * @param id Staff ID
     * @return Staff details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponse> getStaffById(@PathVariable Long id) {
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * U-05: Update Staff Info
     * Only OWNER can update staff information.
     *
     * @param id      Staff ID
     * @param request Update data
     * @return Updated staff
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponse> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * U-06: Deactivate Staff (Soft Delete)
     * Only OWNER can deactivate staff accounts.
     *
     * @param id Staff ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deactivateStaff(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activate Staff
     * Only OWNER can reactivate staff accounts.
     *
     * @param id Staff ID
     * @return 204 No Content
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> activateStaff(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * U-07: Change Password
     * - OWNER can change any staff's password
     * - STAFF can only change their own password
     *
     * @param id      Staff ID
     * @param request Old and new password
     * @return 204 No Content
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('OWNER') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
