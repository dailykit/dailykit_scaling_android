package com.groctaurant.groctaurant.Adapter;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.groctaurant.groctaurant.Activity.IngredientActivity;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.lang.reflect.Type;

/**
 * Created by Danish Rafique on 16-10-2018.
 */
public class IngredientInternalAdapter extends RecyclerView.Adapter<IngredientInternalAdapter.SingleItemRowHolder> {

    public static final String TAG = "IngredientScInternal";
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    int currentPosition;
    private IngredientEntity ingredientEntity;
    private IngredientActivity activity;
    private boolean isSelected;
    private IngredientDetailEntity selectedIngredientDetailEntity;
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;
    ItemEntity itemEntity;
    int ingredientPosition;

    public IngredientInternalAdapter(Activity activity, IngredientEntity ingredientEntity, boolean isSelected, ItemEntity itemEntity,int ingredientPosition) {
        this.ingredientEntity = ingredientEntity;
        this.activity = (IngredientActivity)activity;
        this.isSelected = isSelected;
        this.sharedpreferences = AppUtil.getAppPreferences(activity);
        this.editor = sharedpreferences.edit();
        Type type = new TypeToken<IngredientDetailEntity>() {}.getType();
        this.selectedIngredientDetailEntity = new Gson().fromJson(sharedpreferences.getString(Constants.SELECTED_INGREDIENT_ENTITY, ""), type);
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
        this.itemEntity=itemEntity;
        this.ingredientPosition=ingredientPosition;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_element, parent,false);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final IngredientDetailEntity singleItem = ingredientEntity.getIngredientEntity().get(position);
        Type type = new TypeToken<IngredientDetailEntity>() {}.getType();
        selectedIngredientDetailEntity = new Gson().fromJson(sharedpreferences.getString(Constants.SELECTED_INGREDIENT_ENTITY, ""), type);
        //holder.ingredientInternalLayout.setMinimumWidth(sharedpreferences.getInt(Constants.WIDTH_OF_INGREDIENT_LIST, 1188));
        if(selectedIngredientDetailEntity.getIngredientDetailId()==null){
            editor.putString(Constants.SELECTED_INGREDIENT_ENTITY, new Gson().toJson(ingredientEntity.getIngredientEntity().get(0)));
            editor.commit();
        }
        Log.e(TAG,"Selected : "+selectedIngredientDetailEntity.toString());
        Log.e(TAG,"Current : "+singleItem.toString());

        if (isSelected && singleItem.getIngredientDetailId().equals(selectedIngredientDetailEntity.getIngredientDetailId())) {
            if(position==0){
                activity.callSwitchToTare();
            }
            holder.ingredientInternalLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
            holder.ingredientInternalName.setTextColor(activity.getResources().getColor(R.color.black));
            holder.ingredientInternalWeight.setTextColor(activity.getResources().getColor(R.color.black));
        } else {
            //holder.ingredientInternalLayout.setBackgroundColor(0x00000000);
            holder.ingredientInternalName.setTextColor(activity.getResources().getColor(R.color.white));
            holder.ingredientInternalWeight.setTextColor(activity.getResources().getColor(R.color.white));
        }
        holder.ingredientInternalName.setText(singleItem.getIngredientProcess() + " - " + singleItem.getIngredientName());
        holder.ingredientInternalWeight.setText(singleItem.getIngredientQuantity() + " gms");

        holder.ingredientInternalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, singleItem.getIngredientName());
                groctaurantDatabase.ingredientDao().setSelectedItem(ingredientEntity.getIngredientId(), position);
                groctaurantDatabase.itemDao().setSelectedItem(itemEntity.getItemOrderId(), ingredientPosition);
                editor.putString(Constants.SELECTED_INGREDIENT_ENTITY, new Gson().toJson(singleItem));
                editor.commit();
                itemEntity.setSelectedPosition(ingredientPosition);
                Log.e(TAG,"ChangeIngredientList Called 4");
                activity.changeIngredientList();
            }
        });
        if (singleItem.isPacked()) {
           // holder.ingredientInternalLayout.setBackgroundColor(activity.getResources().getColor(R.color.black));
        }
    }

    public void updateList() {
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != ingredientEntity.getIngredientEntity() ? ingredientEntity.getIngredientEntity().size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView ingredientInternalName, ingredientInternalWeight;
        protected LinearLayout ingredientInternalLayout;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.ingredientInternalName = (TextView) itemView.findViewById(R.id.ingredient_internal_name);
            this.ingredientInternalWeight = (TextView) itemView.findViewById(R.id.ingredient_internal_weight);
            this.ingredientInternalLayout = (LinearLayout) itemView.findViewById(R.id.ingredient_internal_layout);
        }
    }
}


