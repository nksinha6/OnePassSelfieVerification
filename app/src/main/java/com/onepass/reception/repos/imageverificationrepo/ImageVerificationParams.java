package com.onepass.reception.repos.imageverificationrepo;

import java.io.File;

public class ImageVerificationParams {
    private String countryCode;
    private String phoneNumber;
    private String bookingId;
    private File selfieImage;


    public File getSelfieImage() {
        return selfieImage;
    }

    public void setSelfieImage(File selfieImage) {
        this.selfieImage = selfieImage;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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
}
