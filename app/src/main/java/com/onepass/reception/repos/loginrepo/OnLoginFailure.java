package com.onepass.reception.repos.loginrepo;

import com.onepass.reception.models.response.LoginResponse;

public interface OnLoginFailure {
    void onFailure(Throwable throwable);
}
