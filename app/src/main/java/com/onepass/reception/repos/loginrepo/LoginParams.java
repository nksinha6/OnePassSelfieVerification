package com.onepass.reception.repos.loginrepo;

public class LoginParams {
    private String userId;
    private String password;

    private Integer tenantId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public LoginParams(String userId, String password, Integer tenantId) {
        this.userId = userId;
        this.password = password;
        this.tenantId = tenantId;
    }
}
