package com.groctaurant.groctaurant.Room.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 24-08-2018.
 */
public class IngredientViewModel extends AndroidViewModel {

    private LiveData<List<IngredientEntity>> ingredientList;


    public IngredientViewModel(@NonNull Application application, GroctaurantDatabase groctaurantDatabase , String itemId) {
        super(application);
        ingredientList = groctaurantDatabase.ingredientDao().loadAllIngredient(itemId);
    }

    public LiveData<List<IngredientEntity>> getIngredientList() {
        return ingredientList;
    }

}
