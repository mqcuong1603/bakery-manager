package com.bakery.admin.dto;

import com.bakery.shared.user.Role;
import com.bakery.shared.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StaffResponse {

    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;

    public static StaffResponse fromEntity(User user) {
        return StaffResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdById(user.getCreatedBy() != null ? user.getCreatedBy().getId() : null)
                .build();
    }
}
