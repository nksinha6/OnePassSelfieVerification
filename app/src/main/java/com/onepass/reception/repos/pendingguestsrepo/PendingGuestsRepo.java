package com.onepass.reception.repos.pendingguestsrepo;

import com.onepass.reception.models.response.PendingGuests;
import com.onepass.reception.network.ApiClient;
import com.onepass.reception.network.ApiService;
import com.onepass.reception.utils.AppUtils;
import com.onepass.reception.utils.HttpStatusCodes;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingGuestsRepo {
    public static void getPendingRequests(
            PendingGuestParams params,
            OnSuccess onSuccess,
            OnError onError
    ){

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getPendingImages(
                AppUtils.getHeaders(),
                params.getBookingId()
        ).enqueue(new Callback<List<PendingGuests>>() {
            @Override
            public void onResponse(Call<List<PendingGuests>> call, Response<List<PendingGuests>> response) {
                if(response.code()== HttpStatusCodes.OK){
                    onSuccess.onSuccess(response.body());
                }else {
                    try {
                        onError.onError(new Throwable(response.errorBody().string()));
                    } catch (Exception e){
                        onError.onError(new Throwable("Error fetching pending images!"));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PendingGuests>> call, Throwable throwable) {
                onError.onError(throwable);
            }
        });
    }
}
