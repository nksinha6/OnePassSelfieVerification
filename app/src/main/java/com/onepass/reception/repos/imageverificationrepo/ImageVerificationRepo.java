package com.onepass.reception.repos.imageverificationrepo;

import com.onepass.reception.models.response.ImageVerification;
import com.onepass.reception.network.ApiClient;
import com.onepass.reception.network.ApiService;
import com.onepass.reception.utils.AppUtils;
import com.onepass.reception.utils.SessionManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageVerificationRepo {
    public static void verifyImage(
            ImageVerificationParams params,
            OnSuccess successCallback,
            OnFailure failureCallback
    ) {
        // text parts
        RequestBody countryCode =
                RequestBody.create(params.getCountryCode(), MediaType.parse("text/plain"));

        RequestBody phoneNumber =
                RequestBody.create(params.getPhoneNumber(), MediaType.parse("text/plain"));

        RequestBody id =
                RequestBody.create(params.getId().toString(), MediaType.parse("text/plain"));

        RequestBody bookingId =
                RequestBody.create(params.getBookingId(), MediaType.parse("text/plain"));

        RequestBody latitude =
                RequestBody.create(params.getLatitude().toString(), MediaType.parse("text/plain"));

        RequestBody longitude =
                RequestBody.create(params.getLongitude().toString(), MediaType.parse("text/plain"));

        // image part
        RequestBody imageRequestBody =
                RequestBody.create(params.getSelfieImage(), MediaType.parse("image/*"));

        MultipartBody.Part selfieImage =
                MultipartBody.Part.createFormData(
                        "Selfie",
                        params.getSelfieImage().getName(),
                        imageRequestBody
                );

        ApiService service = ApiClient.getClient().create(ApiService.class);
        service
                .verifyImage(
                        AppUtils.getHeaders(),
                        countryCode,
                        phoneNumber,
                        id,
                        bookingId,
                        latitude,
                        longitude,
                        selfieImage
                )
                .enqueue(new Callback<ImageVerification>() {
                    @Override
                    public void onResponse(Call<ImageVerification> call, Response<ImageVerification> response) {

                        try {
                            ImageVerification verificationResponse = response.body();
                            successCallback.onSuccess(verificationResponse);
                        }catch (Exception e){
                            if(!SessionManager.isExpired()) {
                                failureCallback.onFailure(new Throwable("Error while verifying Image!"));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageVerification> call, Throwable throwable) {
                        if(!SessionManager.isExpired()) {
                            failureCallback.onFailure(throwable);
                        }
                    }
                });
    }
}
