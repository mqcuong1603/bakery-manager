package com.bakery.user;

import com.bakery.user.dto.CreateUserRequest;
import com.bakery.user.dto.UpdateUserRequest;
import com.bakery.user.dto.UserResponse;
import com.bakery.user.exception.UserNotFoundException;
import com.bakery.user.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService.
 *
 * TODO: Implement all the methods below.
 *
 * Key concepts to learn:
 * - @Service: Marks this as a Spring service bean
 * - @RequiredArgsConstructor: Lombok generates constructor for final fields (dependency injection)
 * - @Transactional: Database operations run in a transaction
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    // TODO: Uncomment when you add Spring Security
    // private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request, Long createdById) {
        // TODO: Implement user creation
        //
        // Steps:
        // 1. Check if username already exists
        //    if (userRepository.existsByUsername(request.getUsername())) {
        //        throw new UsernameAlreadyExistsException(request.getUsername());
        //    }
        //
        // 2. Create new User entity
        //    User user = new User();
        //    user.setUsername(request.getUsername());
        //    user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password!
        //    user.setFullName(request.getFullName());
        //    user.setPhone(request.getPhone());
        //    user.setRole(request.getRole());
        //
        // 3. Set createdBy if provided
        //    if (createdById != null) {
        //        User creator = userRepository.findById(createdById)
        //            .orElseThrow(() -> new UserNotFoundException(createdById));
        //        user.setCreatedBy(creator);
        //    }
        //
        // 4. Save and return
        //    User savedUser = userRepository.save(user);
        //    return UserResponse.fromEntity(savedUser);

        return null;
    }

    @Override
    public UserResponse getUserById(Long id) {
        // TODO: Implement
        //
        // User user = userRepository.findById(id)
        //     .orElseThrow(() -> new UserNotFoundException(id));
        // return UserResponse.fromEntity(user);

        return null;
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        // TODO: Implement
        //
        // User user = userRepository.findByUsername(username)
        //     .orElseThrow(() -> new UserNotFoundException(username));
        // return UserResponse.fromEntity(user);

        return null;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        // TODO: Implement
        //
        // return userRepository.findAll()
        //     .stream()
        //     .map(UserResponse::fromEntity)
        //     .collect(Collectors.toList());

        return null;
    }

    @Override
    public List<UserResponse> getActiveUsers() {
        // TODO: Implement using findByIsActiveTrue()

        return null;
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {
        // TODO: Implement using findByRole(role)

        return null;
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        // TODO: Implement user update
        //
        // Steps:
        // 1. Find existing user or throw UserNotFoundException
        //
        // 2. If username is being changed, check it's not taken
        //    if (request.getUsername() != null &&
        //        !request.getUsername().equals(user.getUsername()) &&
        //        userRepository.existsByUsername(request.getUsername())) {
        //        throw new UsernameAlreadyExistsException(request.getUsername());
        //    }
        //
        // 3. Update only non-null fields
        //    if (request.getUsername() != null) user.setUsername(request.getUsername());
        //    if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        //    if (request.getFullName() != null) user.setFullName(request.getFullName());
        //    ... etc
        //
        // 4. Save and return
        //    return UserResponse.fromEntity(userRepository.save(user));

        return null;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // TODO: Implement soft delete
        //
        // User user = userRepository.findById(id)
        //     .orElseThrow(() -> new UserNotFoundException(id));
        // user.setIsActive(false);
        // userRepository.save(user);
    }
}
