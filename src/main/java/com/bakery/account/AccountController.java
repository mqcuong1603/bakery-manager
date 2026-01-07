package com.bakery.account;

import com.bakery.account.dto.AccountResponse;
import com.bakery.account.dto.ChangePasswordRequest;
import com.bakery.account.dto.UpdateAccountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getCurrentAccount() {
        AccountResponse response = accountService.getCurrentAccount();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<AccountResponse> updateCurrentAccount(
            @Valid @RequestBody UpdateAccountRequest request) {
        AccountResponse response = accountService.updateCurrentAccount(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        accountService.changePassword(request);
        return ResponseEntity.noContent().build();
    }
}
