
package com.groctaurant.groctaurant.Retrofit.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class OrderResponse implements Serializable {

    private final static long serialVersionUID = -1003049943578996952L;
    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("all_order")
    @Expose
    private List<AllOrder> allOrder = null;

    /**
     * No args constructor for use in serialization
     */
    public OrderResponse() {
    }

    /**
     * @param allOrder
     * @param success
     */
    public OrderResponse(String success, List<AllOrder> allOrder) {
        super();
        this.success = success;
        this.allOrder = allOrder;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<AllOrder> getAllOrder() {
        return allOrder;
    }

    public void setAllOrder(List<AllOrder> allOrder) {
        this.allOrder = allOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("success", success).append("allOrder", allOrder).toString();
    }

}
