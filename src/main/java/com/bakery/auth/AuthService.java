package com.bakery.auth;

import com.bakery.auth.dto.LoginRequest;
import com.bakery.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void logout(String token);
}
