package com.example.drogopolex.data.network.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.drogopolex.constants.AppConstant.BASE_URL;

public class RetrofitUtils {

    private static Retrofit retrofit;

    private RetrofitUtils() {
        throw new IllegalStateException("Utility class.");
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
