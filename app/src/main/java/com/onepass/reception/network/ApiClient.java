package com.onepass.reception.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onepass.reception.interceptors.AuthInterceptor;
import com.onepass.reception.utils.MyApp;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://whale-app-tcfko.ondigitalocean.app";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // Create GSON instance
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Add logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            AuthInterceptor authInterceptor = new AuthInterceptor(MyApp.appContext);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)
                    .build();

            // Create Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
