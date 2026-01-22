package com.onepass.reception.utils;

public class SessionManager {
    private static volatile boolean sessionExpired = false;

    public static void markExpired() {
        sessionExpired = true;
    }

    public static boolean isExpired() {
        return sessionExpired;
    }

    public static void reset() {
        sessionExpired = false;
    }
}
