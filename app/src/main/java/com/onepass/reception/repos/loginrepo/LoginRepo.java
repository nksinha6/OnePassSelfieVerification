package com.onepass.reception.repos.loginrepo;

import com.onepass.reception.models.response.LoginResponse;
import com.onepass.reception.network.ApiClient;
import com.onepass.reception.network.ApiService;
import com.onepass.reception.utils.AppUtils;
import com.onepass.reception.utils.HttpStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepo {

    public static void login(LoginParams params, OnLoginSuccess onLoginSuccess, OnLoginFailure onLoginFailure){

        JSONObject obj = new JSONObject();
        try {
            obj.put("userId",params.getUserId());
            obj.put("password",params.getPassword());
            obj.put("tenantId",params.getTenantId());
        } catch (JSONException e) {
            AppUtils.showLog("Error login");
        }

        ApiService service = ApiClient.getClient().create(ApiService.class);
        service.login(
                AppUtils.getRequestBody(obj)
        ).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if(response.code() == HttpStatusCodes.OK) {
                    onLoginSuccess.onSuccess(response.body());
                }else{
                    onLoginFailure.onFailure(new Throwable("Error while logging in!"));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                onLoginFailure.onFailure(throwable);
            }
        });
    }
}
