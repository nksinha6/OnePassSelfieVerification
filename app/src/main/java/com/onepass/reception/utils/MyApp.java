package com.onepass.reception.utils;

import android.app.Application;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TokenProvider.init(this);
    }
}

