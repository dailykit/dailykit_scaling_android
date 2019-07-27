package com.groctaurant.groctaurant.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusResponseModel {

    @SerializedName("status")
    @Expose
    private String status;

    public StatusResponseModel(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusResponseModel{" +
                "status='" + status + '\'' +
                '}';
    }
}
