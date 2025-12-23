package com.onepass.reception.network;

import com.onepass.reception.models.response.ImageVerification;
import com.onepass.reception.models.response.LoginResponse;
import com.onepass.reception.models.response.PendingGuests;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/HotelUser/login")
    Call<LoginResponse> login(
            @Body RequestBody body
    );

    @GET("/api/HotelGuestRead/pending_face_matches")
    Call<List<PendingGuests>> getPendingImages(
            @HeaderMap Map<String,String> headers,
            @Query("bookingId") String bookingId
    );

    @Multipart
    @POST("/api/FaceVerification/liveness")
    Call<ImageVerification> verifyImage(
            @HeaderMap Map<String,String> headers,
            @Part("countryCode") RequestBody countryCode,
            @Part("phoneNumber") RequestBody phoneNumber,
            @Part("bookingId") RequestBody bookingId,
            @Part MultipartBody.Part selfieImage
    );


}
