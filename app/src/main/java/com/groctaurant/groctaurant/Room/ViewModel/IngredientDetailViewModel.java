package com.groctaurant.groctaurant.Room.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;


import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 24-08-2018.
 */
public class IngredientDetailViewModel extends AndroidViewModel {

    private LiveData<List<IngredientDetailEntity>> ingredientDetailList;


    public IngredientDetailViewModel(@NonNull Application application, GroctaurantDatabase groctaurantDatabase , String ingredientId) {
        super(application);
        ingredientDetailList = groctaurantDatabase.ingredientDetailDao().loadAllIngredientDetail(ingredientId);
    }

    public LiveData<List<IngredientDetailEntity>> getIngredientDetailList() {
        return ingredientDetailList;
    }

}
