package com.onepass.reception.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PendingGuests implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("bookingId")
    @Expose
    private String bookingId;
    @SerializedName("phoneCountryCode")
    @Expose
    private String phoneCountryCode;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("tenantId")
    @Expose
    private Integer tenantId;
    @SerializedName("propertyId")
    @Expose
    private Integer propertyId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("fullName")
    @Expose
    private String name;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
