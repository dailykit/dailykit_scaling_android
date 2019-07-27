package com.groctaurant.groctaurant.Room.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Danish Rafique on 09-11-2018.
 */

@Entity(tableName = "tab")
public class TabEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "order_id")
    public String orderId;

    @NonNull
    @ColumnInfo(name = "order_number")
    public String orderNumber;

    @NonNull
    @ColumnInfo(name = "active_position")
    public int activePosition;


    public TabEntity(@NonNull String orderId, @NonNull String orderNumber, @NonNull int activePosition) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.activePosition = activePosition;
    }

    @NonNull
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(@NonNull String orderId) {
        this.orderId = orderId;
    }

    @NonNull
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(@NonNull String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @NonNull
    public int getActivePosition() {
        return activePosition;
    }

    public void setActivePosition(@NonNull int activePosition) {
        this.activePosition = activePosition;
    }

    @Override
    public String toString() {
        return "TabEntity{" +
                "orderId='" + orderId + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", activePosition=" + activePosition +
                '}';
    }
}
