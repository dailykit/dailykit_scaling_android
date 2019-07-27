package com.groctaurant.groctaurant.Room.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.groctaurant.groctaurant.Room.Converter.ItemConverter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Danish Rafique on 23-08-2018.
 */

@Entity(tableName = "order")
public class OrderEntity implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "order_id")
    public String orderId;

    @ColumnInfo(name = "order_number")
    public String orderNumber;

    @ColumnInfo(name = "order")
    @TypeConverters(ItemConverter.class)
    public ArrayList<ItemEntity> orderItems;

    @NonNull
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(@NonNull String orderId) {
        this.orderId = orderId;
    }

    public ArrayList<ItemEntity> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<ItemEntity> orderItems) {
        this.orderItems = orderItems;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "orderId='" + orderId + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }
}
