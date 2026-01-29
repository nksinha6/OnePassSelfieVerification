package com.onepass.reception.mocks;

import com.google.gson.Gson;
import com.onepass.reception.repos.pendingguestsrepo.PendingGuestResponse;

public class PendingGuestMock {
    public static PendingGuestResponse mockSuccess(){
        String resp = "{\n" +
                "  \"tenantId\": 1,\n" +
                "  \"propertyId\": 1,\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"bookingId\": \"#9876567\",\n" +
                "      \"phoneCountryCode\": \"+91\",\n" +
                "      \"phoneNumber\": \"9123997143\",\n" +
                "      \"fullName\": \"Arkaprabha Mahata\",\n" +
                "      \"status\": \"pending\",\n" +
                "      \"createdAt\": \"2026-01-28T12:15:26.443Z\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        return new Gson().fromJson(resp,PendingGuestResponse.class);
    }
}
