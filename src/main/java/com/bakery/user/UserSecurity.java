package com.bakery.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    /**
     * Check if the given ID matches the currently authenticated user's ID.
     *
     * @param userId The user ID to check
     * @return true if the current user's ID matches the given ID
     */
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
}
