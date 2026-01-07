package com.bakery.shared.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user.getId().equals(userId);
        }

        return false;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        return null;
    }

    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}
