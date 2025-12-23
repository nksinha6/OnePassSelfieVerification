package com.onepass.reception.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenProvider {

    private static volatile TokenProvider INSTANCE;
    private SharedPreferences prefs;

    private TokenProvider(Context context) {
        prefs = AppUtils.getSharedPreferences(context);
    }

    // Called ONCE
    public static void init(Context context) {
        if (INSTANCE == null) {
            synchronized (TokenProvider.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TokenProvider(context);
                }
            }
        }
    }

    // Used everywhere — NO context needed
    public static TokenProvider getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "TokenProvider is not initialized. Call init() in Application."
            );
        }
        return INSTANCE;
    }

    public String getAccessToken() {
        return prefs.getString("access_token", null);
    }

    public void saveAccessToken(String token) {
        prefs.edit().putString("access_token", token).apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
