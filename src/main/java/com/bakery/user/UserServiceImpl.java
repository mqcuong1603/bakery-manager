package com.bakery.user;

import com.bakery.user.dto.ChangePasswordRequest;
import com.bakery.user.dto.CreateUserRequest;
import com.bakery.user.dto.UpdateUserRequest;
import com.bakery.user.dto.UserResponse;
import com.bakery.user.exception.InvalidPasswordException;
import com.bakery.user.exception.UserNotFoundException;
import com.bakery.user.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse createStaff(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(Role.STAFF);

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    public List<UserResponse> findAllStaff() {
        List<User> staffList = userRepository.findByRole(Role.STAFF);

        return staffList.stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (request.getUsername() != null
                && !request.getUsername().equals(user.getUsername())) {

            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException(request.getUsername());
            }

            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            if (!request.getCurrentPassword().equals(user.getPassword())) {
                throw new InvalidPasswordException();
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setPassword(request.getNewPassword());
            userRepository.save(user);
        }
    }
