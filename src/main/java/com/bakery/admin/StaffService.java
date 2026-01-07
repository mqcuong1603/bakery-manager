package com.bakery.admin;

import com.bakery.admin.dto.CreateStaffRequest;
import com.bakery.admin.dto.ResetPasswordRequest;
import com.bakery.admin.dto.StaffResponse;
import com.bakery.admin.dto.UpdateStaffRequest;

import java.util.List;

public interface StaffService {

    StaffResponse createStaff(CreateStaffRequest request);

    List<StaffResponse> findAllStaff();

    List<StaffResponse> findAllActiveStaff();

    StaffResponse findById(Long id);

    StaffResponse updateStaff(Long id, UpdateStaffRequest request);

    void deactivateStaff(Long id);

    void activateStaff(Long id);

    void resetPassword(Long id, ResetPasswordRequest request);
}
