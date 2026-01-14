package com.onepass.reception.utils;

public interface LocationCallback {
    public void onLocationFetched(double latitude, double longitude);
    public void onError(String errorMessage);
}
