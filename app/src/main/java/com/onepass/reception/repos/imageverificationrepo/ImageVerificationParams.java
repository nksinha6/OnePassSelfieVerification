package com.onepass.reception.repos.imageverificationrepo;

import java.io.File;

public class ImageVerificationParams {
    private String countryCode;
    private String phoneNumber;
    private Integer id;
    private File selfieImage;
    private Double latitude;
    private Double longitude;




    public File getSelfieImage() {
        return selfieImage;
    }

    public void setSelfieImage(File selfieImage) {
        this.selfieImage = selfieImage;
    }

    public Integer getBookingId() {
        return id;
    }

    public void setBookingId(Integer bookingId) {
        this.id = bookingId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
