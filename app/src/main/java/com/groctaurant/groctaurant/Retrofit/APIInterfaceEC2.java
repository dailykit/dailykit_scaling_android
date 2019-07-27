package com.groctaurant.groctaurant.Retrofit;

import com.groctaurant.groctaurant.Model.PackingRequestModel;
import com.groctaurant.groctaurant.Model.StatusResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIInterfaceEC2 {

    @POST("packing-update")
    @Headers("Content-Type: application/json")
    Call<StatusResponseModel> updatePackingStatus(@Body PackingRequestModel packingRequestModel);

}
