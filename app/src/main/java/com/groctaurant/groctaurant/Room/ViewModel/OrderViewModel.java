package com.groctaurant.groctaurant.Room.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Entity.OrderEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 24-08-2018.
 */
public class OrderViewModel extends AndroidViewModel {

    private LiveData<List<OrderEntity>> orderList;

    public OrderViewModel(@NonNull Application application, GroctaurantDatabase groctaurantDatabase) {
        super(application);
        orderList = groctaurantDatabase.orderDao().loadAllOrder();
    }


    public OrderViewModel(@NonNull Application application, GroctaurantDatabase groctaurantDatabase , String orderId) {
        super(application);
        //orderList = groctaurantDatabase.orderDao().loadOrder(orderId);
    }

    public LiveData<List<OrderEntity>> getOrderList() {
        return orderList;
    }
}
