package com.bakery.admin;

import com.bakery.admin.dto.CreateStaffRequest;
import com.bakery.admin.dto.ResetPasswordRequest;
import com.bakery.admin.dto.StaffResponse;
import com.bakery.admin.dto.UpdateStaffRequest;
import com.bakery.shared.user.Role;
import com.bakery.shared.user.User;
import com.bakery.shared.user.UserRepository;
import com.bakery.shared.user.UserSecurity;
import com.bakery.shared.user.exception.UserNotFoundException;
import com.bakery.shared.user.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final UserSecurity userSecurity;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public StaffResponse createStaff(CreateStaffRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        User staff = new User();
        staff.setUsername(request.getUsername());
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setFullName(request.getFullName());
        staff.setPhone(request.getPhone());
        staff.setRole(Role.STAFF);
        staff.setIsActive(true);

        // Set created_by to current owner
        User currentUser = userSecurity.getCurrentUser();
        if (currentUser != null) {
            staff.setCreatedBy(currentUser);
        }

        User savedStaff = userRepository.save(staff);
        return StaffResponse.fromEntity(savedStaff);
    }

    @Override
    public List<StaffResponse> findAllStaff() {
        List<User> staffList = userRepository.findByRole(Role.STAFF);
        return staffList.stream()
                .map(StaffResponse::fromEntity)
                .toList();
    }

    @Override
    public List<StaffResponse> findAllActiveStaff() {
        List<User> staffList = userRepository.findByRoleAndIsActiveTrue(Role.STAFF);
        return staffList.stream()
                .map(StaffResponse::fromEntity)
                .toList();
    }

    @Override
    public StaffResponse findById(Long id) {
        User staff = findStaffById(id);
        return StaffResponse.fromEntity(staff);
    }

    @Override
    @Transactional
    public StaffResponse updateStaff(Long id, UpdateStaffRequest request) {
        User staff = findStaffById(id);

        if (request.getUsername() != null
                && !request.getUsername().equals(staff.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException(request.getUsername());
            }
            staff.setUsername(request.getUsername());
        }

        if (request.getFullName() != null) {
            staff.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            staff.setPhone(request.getPhone());
        }

        if (request.getIsActive() != null) {
            staff.setIsActive(request.getIsActive());
        }

        User updatedStaff = userRepository.save(staff);
        return StaffResponse.fromEntity(updatedStaff);
    }

    @Override
    @Transactional
    public void deactivateStaff(Long id) {
        User staff = findStaffById(id);
        staff.setIsActive(false);
        userRepository.save(staff);
    }

    @Override
    @Transactional
    public void activateStaff(Long id) {
        User staff = findStaffById(id);
        staff.setIsActive(true);
        userRepository.save(staff);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, ResetPasswordRequest request) {
        User staff = findStaffById(id);
        staff.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(staff);
    }

    private User findStaffById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Ensure it's a staff member, not owner
        if (user.getRole() != Role.STAFF) {
            throw new UserNotFoundException(id);
        }

        return user;
    }
}
