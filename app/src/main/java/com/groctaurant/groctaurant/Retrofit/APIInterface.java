package com.groctaurant.groctaurant.Retrofit;

import com.groctaurant.groctaurant.Model.PackingRequestModel;
import com.groctaurant.groctaurant.Model.StatusResponseModel;
import com.groctaurant.groctaurant.Retrofit.Response.OrderResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Danish Rafique on 26-08-2018.
 */
public interface APIInterface {

    @POST("orders_back_new")
    Call<OrderResponse> getOrderListing();

}
