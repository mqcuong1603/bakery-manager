//package com.bakery.auth;
//
//import com.bakery.auth.dto.LoginRequest;
//import com.bakery.auth.dto.LoginResponse;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
///**
// * Authentication Controller
// * Handles login and logout operations.
// *
// * Endpoints:
// * - POST /api/auth/login  - U-01: Login
// * - POST /api/auth/logout - U-02: Logout
// */
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//
//    /**
//     * U-01: Login
//     * Authenticate user and return JWT token.
//     *
//     * @param request Login credentials (username, password)
//     * @return JWT token and user info
//     */
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
//        LoginResponse response = authService.login(request);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * U-02: Logout
//     * Invalidate current session/token.
//     *
//     * @param authHeader Authorization header containing JWT token
//     * @return 200 OK on success
//     */
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            authService.logout(token);
//        }
//        return ResponseEntity.ok().build();
//    }
//}
