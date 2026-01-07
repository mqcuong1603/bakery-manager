package com.bakery.admin;

import com.bakery.admin.dto.CreateStaffRequest;
import com.bakery.admin.dto.ResetPasswordRequest;
import com.bakery.admin.dto.StaffResponse;
import com.bakery.admin.dto.UpdateStaffRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/staff")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {
        StaffResponse response = staffService.createStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        List<StaffResponse> staffList = staffService.findAllStaff();
        return ResponseEntity.ok(staffList);
    }

    @GetMapping("/active")
    public ResponseEntity<List<StaffResponse>> getAllActiveStaff() {
        List<StaffResponse> staffList = staffService.findAllActiveStaff();
        return ResponseEntity.ok(staffList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getStaffById(@PathVariable Long id) {
        StaffResponse response = staffService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStaffRequest request) {
        StaffResponse response = staffService.updateStaff(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateStaff(@PathVariable Long id) {
        staffService.deactivateStaff(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateStaff(@PathVariable Long id) {
        staffService.activateStaff(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest request) {
        staffService.resetPassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
