
package com.groctaurant.groctaurant.Retrofit.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class AllOrder implements Serializable {

    private final static long serialVersionUID = 1091828444748061961L;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("order")
    @Expose
    private List<Order> order = null;

    /**
     * No args constructor for use in serialization
     */
    public AllOrder() {
    }

    /**
     * @param order
     * @param orderId
     */
    public AllOrder(String orderId, List<Order> order) {
        super();
        this.orderId = orderId;
        this.order = order;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("orderId", orderId).append("order", order).toString();
    }

}
