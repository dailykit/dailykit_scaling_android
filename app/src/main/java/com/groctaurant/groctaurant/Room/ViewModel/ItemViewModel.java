package com.groctaurant.groctaurant.Room.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 24-08-2018.
 */
public class ItemViewModel extends AndroidViewModel {

    private LiveData<List<ItemEntity>> itemList;


    public ItemViewModel(@NonNull Application application, GroctaurantDatabase groctaurantDatabase , String orderId) {
        super(application);
        itemList = groctaurantDatabase.itemDao().loadAllItem(orderId);
    }

    public LiveData<List<ItemEntity>> getItemList() {
        return itemList;
    }
}
