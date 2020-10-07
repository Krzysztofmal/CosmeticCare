package com.jkero.blackhawk.testaparatuocr.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jkero.blackhawk.testaparatuocr.Constants;
import com.jkero.blackhawk.testaparatuocr.activities.MenuActivity;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    private static final String BASE_URL = Constants.ROOT_URL+ MenuActivity.myLanguage;
    private static Retrofit retrofit;

    public static Retrofit getApiClient() {

        Gson gson = new GsonBuilder().setLenient().create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
