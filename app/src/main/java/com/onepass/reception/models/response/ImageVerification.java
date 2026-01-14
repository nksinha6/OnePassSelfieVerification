package com.onepass.reception.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImageVerification implements Serializable {
    @SerializedName("result")
    @Expose
    private ImageVerificationResult result;

    public ImageVerificationResult getResult() {
        return result;
    }

    public void setResult(ImageVerificationResult result) {
        this.result = result;
    }
}
