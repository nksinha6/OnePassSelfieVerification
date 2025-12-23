package com.onepass.reception.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImageVerification implements Serializable {
    @SerializedName("faceVerified")
    @Expose
    private Boolean faceVerified;
    @SerializedName("faceMatchScore")
    @Expose
    private Double faceMatchScore;

    public Boolean getFaceVerified() {
        return faceVerified;
    }

    public void setFaceVerified(Boolean faceVerified) {
        this.faceVerified = faceVerified;
    }

    public Double getFaceMatchScore() {
        return faceMatchScore;
    }

    public void setFaceMatchScore(Double faceMatchScore) {
        this.faceMatchScore = faceMatchScore;
    }
}
