package com.bakery.account.dto;

import com.bakery.shared.user.Role;
import com.bakery.shared.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AccountResponse {

    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;

    public static AccountResponse fromEntity(User user) {
        return AccountResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
