package com.groctaurant.groctaurant.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Navin Stark on 7/22/2018.
 */

public class AllDetailModel implements Serializable{

    private ArrayList<OrderDetailModel> allOrderArrayList;
    private String orderStatus;

    public ArrayList<OrderDetailModel> getAllOrderArrayList() {
        return allOrderArrayList;
    }

    public void setAllOrderArrayList(ArrayList<OrderDetailModel> allOrderArrayList) {
        this.allOrderArrayList = allOrderArrayList;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }






}
