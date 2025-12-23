package com.onepass.reception.repos.loginrepo;

import com.onepass.reception.models.response.LoginResponse;

public interface OnLoginSuccess {
    void onSuccess(LoginResponse response);
}
