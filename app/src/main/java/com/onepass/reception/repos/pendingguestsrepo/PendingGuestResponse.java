package com.onepass.reception.repos.pendingguestsrepo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.onepass.reception.models.response.PendingGuests;

import java.io.Serializable;
import java.util.List;

public class PendingGuestResponse implements Serializable {
    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public List<PendingGuests> getPendingGuests() {
        return pendingGuests;
    }

    public void setPendingGuests(List<PendingGuests> pendingGuests) {
        this.pendingGuests = pendingGuests;
    }

    @SerializedName("tenantId")
    @Expose
    private Integer tenantId;
    @SerializedName("propertyId")
    @Expose
    private Integer propertyId;
    @SerializedName("items")
    @Expose
    private List<PendingGuests> pendingGuests;
}
