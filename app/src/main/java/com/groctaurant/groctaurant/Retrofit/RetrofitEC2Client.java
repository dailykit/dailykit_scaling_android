package com.groctaurant.groctaurant.Retrofit;

import com.groctaurant.groctaurant.Utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitEC2Client {

    private static RetrofitEC2Client minstance;
    private Retrofit retrofit;

    private RetrofitEC2Client() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_EC2)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized RetrofitEC2Client getClient() {
        if (minstance == null) {
            minstance = new RetrofitEC2Client();

        }
        return minstance;

    }

    public APIInterfaceEC2 getApi() {
        return retrofit.create(APIInterfaceEC2.class);
    }

}
