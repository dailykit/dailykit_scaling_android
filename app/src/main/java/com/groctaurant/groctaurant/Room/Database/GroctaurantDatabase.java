package com.groctaurant.groctaurant.Room.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.groctaurant.groctaurant.Model.IngredientPlanningModel;
import com.groctaurant.groctaurant.Room.Converter.IngredientConverter;
import com.groctaurant.groctaurant.Room.Converter.IngredientDetailConverter;
import com.groctaurant.groctaurant.Room.Converter.ItemConverter;
import com.groctaurant.groctaurant.Room.DAO.IngredientDao;
import com.groctaurant.groctaurant.Room.DAO.IngredientDetailDao;
import com.groctaurant.groctaurant.Room.DAO.IngredientPlanningDao;
import com.groctaurant.groctaurant.Room.DAO.ItemDao;
import com.groctaurant.groctaurant.Room.DAO.OrderDao;
import com.groctaurant.groctaurant.Room.DAO.PlanningDao;
import com.groctaurant.groctaurant.Room.DAO.PlanningDetailDao;
import com.groctaurant.groctaurant.Room.DAO.TabDao;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientPlanningEntity;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Room.Entity.OrderEntity;
import com.groctaurant.groctaurant.Room.Entity.PlanningDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.PlanningEntity;
import com.groctaurant.groctaurant.Room.Entity.TabEntity;

/**
 * Created by Danish Rafique on 24-08-2018.
 */

@Database(entities = {OrderEntity.class, ItemEntity.class, IngredientEntity.class, IngredientDetailEntity.class, TabEntity.class, PlanningEntity.class, IngredientPlanningEntity.class, PlanningDetailEntity.class}, version = 1, exportSchema = false)
@TypeConverters({ItemConverter.class, IngredientConverter.class, IngredientDetailConverter.class})
public abstract class GroctaurantDatabase extends RoomDatabase {

    private static final String LOG_TAG = GroctaurantDatabase.class.getSimpleName();

    public abstract OrderDao orderDao();

    public abstract ItemDao itemDao();

    public abstract IngredientDao ingredientDao();

    public abstract IngredientDetailDao ingredientDetailDao();

    public abstract TabDao tabDao();

    public abstract PlanningDao planningDao();

    public abstract IngredientPlanningDao ingredientPlanningDao();

    public abstract PlanningDetailDao planningDetailDao();

    private static GroctaurantDatabase INSTANCE;


    static GroctaurantDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GroctaurantDatabase.class) {
                Log.d(LOG_TAG, "Creating new database instance");
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GroctaurantDatabase.class, "groctaurant_database")
                            .build();

                }
            }
        }
        Log.d(LOG_TAG, "Getting the old database instance");
        return INSTANCE;
    }

}
