package com.groctaurant.groctaurant.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.groctaurant.groctaurant.Callback.IngredientListener;
import com.groctaurant.groctaurant.Callback.OrderCallback;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Danish Rafique on 03-03-2019.
 */
public class OrderViewModel extends AndroidViewModel {

    private static final String TAG = "OrderViewModel";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        sharedpreferences = AppUtil.getAppPreferences(application);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(application, GroctaurantDatabase.class, "Development").allowMainThreadQueries().build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
    }

    public String getSelectedOrderNumber(){
        return groctaurantDatabase.tabDao().getOrderNumber(sharedpreferences.getString(Constants.SELECTED_ORDER_ID, ""));
    }

    public ItemEntity getCurrentItemEntity(){
        return groctaurantDatabase.itemDao().loadItem(sharedpreferences.getString(Constants.SELECTED_ITEM_ID, ""));
    }

    public boolean isSimulated(){
        return sharedpreferences.getBoolean(Constants.IS_SIMULATED, false);
    }

    public boolean isManualSetWeightEnabled(){
        return sharedpreferences.getBoolean(Constants.MANUAL_SET_WEIGHT, false);
    }

    public boolean isPrinterTestEnabled(){
        return sharedpreferences.getBoolean(Constants.PRINTER_TEST, false);
    }

    public String getSelectedOrderId(){
        return sharedpreferences.getString(Constants.SELECTED_ORDER_ID,"");
    }

    public IngredientDetailEntity getSelectedIngredientDetailEntity(){
        Type type = new TypeToken<IngredientDetailEntity>() {}.getType();
        IngredientDetailEntity ingredientDetailEntity=new Gson().fromJson(sharedpreferences.getString(Constants.SELECTED_INGREDIENT_ENTITY, ""), type);
        if(ingredientDetailEntity==null){
            IngredientDetailEntity ingredientDetailEntity1 = new IngredientDetailEntity();
            ingredientDetailEntity.setIngredientName("No Ingredient Selected");
            ingredientDetailEntity.setIngredientQuantity(0);
            ingredientDetailEntity=ingredientDetailEntity1;
        }
        return ingredientDetailEntity;
    }

    public void switchOnTare(OrderCallback orderCallback, float weightOnScale){
        editor.putBoolean(Constants.IS_TARE_ENABLED,true);
        editor.putFloat(Constants.TARE_WEIGHT_HOLDER,weightOnScale);
        editor.commit();
        orderCallback.changeTareDisplay();
    }

    public void switchOffTare(OrderCallback orderCallback){
        editor.putBoolean(Constants.IS_TARE_ENABLED,false);
        editor.putFloat(Constants.TARE_WEIGHT_HOLDER,0);
        editor.commit();
        orderCallback.changeTareDisplay();
    }

    public boolean isTareEnabled(){
        return sharedpreferences.getBoolean(Constants.IS_TARE_ENABLED,false);
    }


    public void setTareWeight(String tareWeight){
        Float oldTareWeight=sharedpreferences.getFloat(Constants.TARE_WEIGHT_HOLDER,0);
        Float totalWeight = oldTareWeight+Float.parseFloat(tareWeight);
        editor.putFloat(Constants.TARE_WEIGHT_HOLDER,totalWeight);
        editor.commit();
    }

    public double getTotalWeightForTare(String weightDisplayed){
        Float oldTareWeight=sharedpreferences.getFloat(Constants.TARE_WEIGHT_HOLDER,0);
        Float totalWeight = oldTareWeight+Float.parseFloat(weightDisplayed);
        return Double.parseDouble(String.valueOf(totalWeight));
    }

    public String getIngredientWeightForTare(String weightOnScale){
        Float tareWeight=sharedpreferences.getFloat(Constants.TARE_WEIGHT_HOLDER,0);
        Float weightToDisplay=Float.parseFloat(weightOnScale)*1000-tareWeight;
        return String.valueOf(weightToDisplay);
    }

    public void setIngredientDetailByItemEntity(ItemEntity itemEntity){
        IngredientEntity ingredientEntity=getIngredientById(itemEntity.getItemIngredient().get(itemEntity.getSelectedPosition()).getIngredientId());
        Log.e(TAG,"Ingredient Entity Under Consideration at setIngredientDetailByItemEntity :"+ingredientEntity.toString());
        if(ingredientEntity.isPackedComplete()){
            int leftToPack=groctaurantDatabase.ingredientDao().countIngredientPackedDao(itemEntity.getItemOrderId(),false);
            int nextIndex;
            if(leftToPack>0) {
                if(Integer.parseInt(ingredientEntity.getIngredientIndex())>=Integer.parseInt(itemEntity.getItemNoOfIngredient())){
                    nextIndex=1;
                }
                else{
                    nextIndex=itemEntity.getSelectedPosition()+1;
                }
                groctaurantDatabase.itemDao().setSelectedItem(itemEntity.getItemOrderId(), nextIndex);
                itemEntity.setSelectedPosition(nextIndex);
                setIngredientDetailByItemEntity(itemEntity);
            }
            else{
                groctaurantDatabase.itemDao().setSelectedItem(itemEntity.getItemOrderId(), -2); // -2 Represents all item packed successfully
                Toast.makeText(getApplication(), "All Ingredient Packed in this Item", Toast.LENGTH_LONG).show();
            }
        }
        else {
            if (ingredientEntity.getIngredientEntity().size() == 1) {
                Log.e(TAG, "Single Ingredient");
                editor.putString(Constants.SELECTED_INGREDIENT_ENTITY, new Gson().toJson(ingredientEntity.getIngredientEntity().get(0)));
                editor.commit();
            } else {
                Log.e(TAG, "Multiple Ingredient");
                int positionToPack = checkAllProductPacked(ingredientEntity);
                setIngredientByPositionAndIngredientEntity(ingredientEntity, positionToPack);
            }
        }
    }

    public IngredientEntity getIngredientById(String ingredientId){
        return groctaurantDatabase.ingredientDao().getIngredientById(ingredientId);
    }

    public int checkAllProductPacked(IngredientDetailEntity ingredientDetailEntity){

        List<IngredientDetailEntity> ingredientDetailEntityList = groctaurantDatabase.ingredientDetailDao().loadIngredientDetailByIngredientId(ingredientDetailEntity.getIngredientId());
        Log.e(TAG,"Size of Ingredient List :"+ingredientDetailEntityList.size());
        for(IngredientDetailEntity i:ingredientDetailEntityList){
            if(!i.isPacked()){
                return i.getIngredientDetailPosition();
            }
        }
        return -1;

    }

    public int checkAllProductPacked(IngredientEntity ingredientEntity){

        List<IngredientDetailEntity> ingredientDetailEntityList = groctaurantDatabase.ingredientDetailDao().loadIngredientDetailByIngredientId(ingredientEntity.getIngredientId());
        Log.e(TAG,"Size of Ingredient List :"+ingredientDetailEntityList.size());
        for(IngredientDetailEntity i:ingredientDetailEntityList){
            if(!i.isPacked()){
                return i.getIngredientDetailPosition();
            }
        }
        return -1;

    }

    public void setIngredientByPositionAndDetailId(IngredientDetailEntity ingredientDetailEntity,int position){
        IngredientDetailEntity i = groctaurantDatabase.ingredientDetailDao().getIngredientByPositionAndDetailId(ingredientDetailEntity.getIngredientId(),position);
        Log.e(TAG,"Selected Ingredient : "+i.toString());
        editor.putString(Constants.SELECTED_INGREDIENT_ENTITY, new Gson().toJson(i));
        editor.commit();
    }

    public void setIngredientByPositionAndIngredientEntity(IngredientEntity ingredientEntity,int position){
        IngredientDetailEntity i = groctaurantDatabase.ingredientDetailDao().getIngredientByPositionAndDetailId(ingredientEntity.getIngredientId(),position);
        Log.e(TAG,"Selected Ingredient : "+i.toString());
        editor.putString(Constants.SELECTED_INGREDIENT_ENTITY, new Gson().toJson(i));
        editor.commit();
    }
}
