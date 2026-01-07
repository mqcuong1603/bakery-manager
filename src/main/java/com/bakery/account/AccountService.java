package com.bakery.account;

import com.bakery.account.dto.AccountResponse;
import com.bakery.account.dto.ChangePasswordRequest;
import com.bakery.account.dto.UpdateAccountRequest;


public interface AccountService {

    AccountResponse getCurrentAccount();

    AccountResponse updateCurrentAccount(UpdateAccountRequest request);

    void changePassword(ChangePasswordRequest request);
}
