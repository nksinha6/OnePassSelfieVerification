package com.onepass.reception.mocks;

import com.onepass.reception.models.response.ImageVerification;
import com.onepass.reception.models.response.ImageVerificationResult;

public class VerifyQrMock {

    public ImageVerification onSuccess(){
        ImageVerification verification = new ImageVerification();

        ImageVerificationResult result = new ImageVerificationResult();
        result.setFaceMatchResult("YES");
        result.setFaceMatchScore(100);
        result.setRefId(123456);
        result.setStatus("SUCCESS");
        result.setVerificationId("1234567890");
        verification.setResult(result);

        return  verification;
    }
}
