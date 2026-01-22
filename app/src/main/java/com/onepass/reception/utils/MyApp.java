package com.onepass.reception.utils;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        TokenProvider.init(this);
    }
}

