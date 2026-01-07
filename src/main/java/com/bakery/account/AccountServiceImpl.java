package com.bakery.account;

import com.bakery.account.dto.AccountResponse;
import com.bakery.account.dto.ChangePasswordRequest;
import com.bakery.account.dto.UpdateAccountRequest;
import com.bakery.shared.user.User;
import com.bakery.shared.user.UserRepository;
import com.bakery.shared.user.UserSecurity;
import com.bakery.shared.user.exception.InvalidPasswordException;
import com.bakery.shared.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final UserSecurity userSecurity;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AccountResponse getCurrentAccount() {
        User currentUser = getCurrentUserOrThrow();
        return AccountResponse.fromEntity(currentUser);
    }

    @Override
    @Transactional
    public AccountResponse updateCurrentAccount(UpdateAccountRequest request) {
        User currentUser = getCurrentUserOrThrow();

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException(currentUser.getId()));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User updatedUser = userRepository.save(user);
        return AccountResponse.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = getCurrentUserOrThrow();

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException(currentUser.getId()));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User getCurrentUserOrThrow() {
        User user = userSecurity.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("No authenticated user found");
        }
        return user;
    }
}
