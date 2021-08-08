package com.example.fermata;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static RetrofitAPI getApiService(){return getInstance().create(RetrofitAPI.class);}

    private static Retrofit getInstance(){
        return new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000") // localhost
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
