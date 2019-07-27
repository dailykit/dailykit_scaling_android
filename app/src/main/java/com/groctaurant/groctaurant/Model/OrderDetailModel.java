package com.groctaurant.groctaurant.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Navin Stark on 7/22/2018.
 */

public class OrderDetailModel implements Serializable{

    private String orderId;
    private String orderName;
    private String orderSKU;
    private String orderStatus;
    private int orderNumber;
    private int serving;
    private int quantity;
    private int pendingRequest;
    private int allRequest;
    private ArrayList<AllIngredientModel> allIngredientModelArrayList;

    public ArrayList<AllIngredientModel> getAllIngredientModelArrayList() {
        return allIngredientModelArrayList;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setAllIngredientModelArrayList(ArrayList<AllIngredientModel> allIngredientModelArrayList) {
        this.allIngredientModelArrayList = allIngredientModelArrayList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderSKU() {
        return orderSKU;
    }

    public void setOrderSKU(String orderSKU) {
        this.orderSKU = orderSKU;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getServing() {
        return serving;
    }

    public void setServing(int serving) {
        this.serving = serving;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPendingRequest() {
        return pendingRequest;
    }

    public void setPendingRequest(int pendingRequest) {
        this.pendingRequest = pendingRequest;
    }

    public int getAllRequest() {
        return allRequest;
    }

    public void setAllRequest(int allRequest) {
        this.allRequest = allRequest;
    }
}
