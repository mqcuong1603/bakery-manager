//package com.bakery.auth;
//
//import com.bakery.auth.dto.LoginRequest;
//import com.bakery.auth.dto.LoginResponse;
//import com.bakery.user.User;
//import com.bakery.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthServiceImpl implements AuthService {
//
//    private final AuthenticationManager authenticationManager;
//    private final UserRepository userRepository;
//    // TODO: Inject your JwtService/JwtUtil here
//    // private final JwtService jwtService;
//
//    @Override
//    public LoginResponse login(LoginRequest request) {
//        // Authenticate user
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getUsername(),
//                        request.getPassword()
//                )
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Get user details
//        User user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // TODO: Generate JWT token using your JwtService
//        // String token = jwtService.generateToken(user);
//        String token = "TODO_GENERATE_JWT_TOKEN";
//
//        return LoginResponse.builder()
//                .accessToken(token)
//                .tokenType("Bearer")
//                .userId(user.getId())
//                .username(user.getUsername())
//                .fullName(user.getFullName())
//                .role(user.getRole())
//                .build();
//    }
//
//    @Override
//    public void logout(String token) {
//        // Clear security context
//        SecurityContextHolder.clearContext();
//
//        // TODO: If using token blacklist, add token to blacklist here
//        // jwtService.invalidateToken(token);
//    }
//}
