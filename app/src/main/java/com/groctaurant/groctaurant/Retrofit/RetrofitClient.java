package com.groctaurant.groctaurant.Retrofit;

import com.groctaurant.groctaurant.Utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Danish Rafique on 26-08-2018.
 */
public class RetrofitClient {

    private static RetrofitClient minstance;
    private Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized RetrofitClient getClient() {
        if (minstance == null) {
            minstance = new RetrofitClient();

        }
        return minstance;

    }

    public APIInterface getApi() {
        return retrofit.create(APIInterface.class);
    }

}
