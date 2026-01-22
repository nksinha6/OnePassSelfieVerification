package com.onepass.reception.interceptors;

import android.content.Context;
import android.content.Intent;
import android.media.MediaDrm;

import androidx.annotation.NonNull;

import com.onepass.reception.utils.AppUtils;
import com.onepass.reception.utils.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context appContext;

    public AuthInterceptor(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == 401) {
            SessionManager.markExpired();
            AppUtils.showLog("Session Expired");
            notifySessionExpired();
        }

        return response;
    }

    private void notifySessionExpired() {
        Intent intent = new Intent("SESSION_EXPIRED");
        intent.setPackage(appContext.getPackageName());
        appContext.sendBroadcast(intent);
        AppUtils.showLog("Session Expired notified");
    }


}

