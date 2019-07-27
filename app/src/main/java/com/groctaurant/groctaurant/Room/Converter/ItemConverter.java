package com.groctaurant.groctaurant.Room.Converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Danish Rafique on 30-08-2018.
 */
public class ItemConverter {

    @TypeConverter
    public static ArrayList<ItemEntity> stringToItemEntityList(String value) {
        Type listType = new TypeToken<ArrayList<ItemEntity>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String itemEntityListToString(ArrayList<ItemEntity> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
