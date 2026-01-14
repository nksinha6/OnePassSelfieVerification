package com.onepass.reception.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImageVerificationResult implements Serializable {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getFaceMatchResult() {
        return faceMatchResult;
    }

    public void setFaceMatchResult(String faceMatchResult) {
        this.faceMatchResult = faceMatchResult;
    }

    public Integer getFaceMatchScore() {
        return faceMatchScore;
    }

    public void setFaceMatchScore(Integer faceMatchScore) {
        this.faceMatchScore = faceMatchScore;
    }

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("ref_id")
    @Expose
    private Integer refId;
    @SerializedName("verification_id")
    @Expose
    private String verificationId;
    @SerializedName("face_match_result")
    @Expose
    private String faceMatchResult;
    @SerializedName("face_match_score")
    @Expose
    private Integer faceMatchScore;
}
