package com.groctaurant.groctaurant.Adapter;


import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.groctaurant.groctaurant.Activity.IngredientActivity;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.util.List;

/**
 * Created by Danish Rafique on 13-10-2018.
 */
public class IngredientLowerTabAdapter extends RecyclerView.Adapter<IngredientLowerTabAdapter.SingleItemRowHolder> {

    public static final String TAG = "IngredientLowerTabAdapt";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    int currentPosition;
    GroctaurantDatabase groctaurantDatabase;
    private IngredientActivity activity;
    GroctaurantExecutor groctaurantExecutor;
    private List<ItemEntity> itemEntityList;

    public IngredientLowerTabAdapter(IngredientActivity activity, List<ItemEntity> itemEntityList) {
        this.itemEntityList = itemEntityList;
        this.activity = activity;
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_tabs, parent,false);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(v);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final ItemEntity singleItem = itemEntityList.get(position);
        int activeTab = sharedpreferences.getInt(Constants.ACTIVE_POSITION, 0);

        if (position == activeTab) {
            holder.llTabActive.setVisibility(View.VISIBLE);
            holder.llTabInactive.setVisibility(View.GONE);
            editor.putString(Constants.SELECTED_ITEM_ID, singleItem.getItemOrderId());
            editor.commit();
            //Log.e(TAG,"ChangeIngredientList Called 5");
            //activity.changeIngredientList();
        } else {
            holder.llTabActive.setVisibility(View.GONE);
            holder.llTabInactive.setVisibility(View.VISIBLE);
        }

        holder.llTabInactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(Constants.SELECTED_ITEM_ID, singleItem.getItemOrderId());
                editor.putInt(Constants.ACTIVE_POSITION, position);
                editor.commit();
                updateList();
                Log.e(TAG,"ChangeIngredientList Called 7");
                activity.changeIngredientList();
                activity.updateWeightScreen();
            }
        });

        int numberOfPackedIngredients = groctaurantDatabase.ingredientDao().countIngredientPackedDao(singleItem.getItemOrderId(), true);

        holder.ingredientTabPending.setText(numberOfPackedIngredients + "/" + singleItem.getItemNoOfIngredient());
        holder.ingredientTabPendingInactive.setText(numberOfPackedIngredients + "/" + singleItem.getItemNoOfIngredient());
        String servingValue[] = singleItem.getItemServing().split(",");
        holder.ingredientTabServing.setText(servingValue[position]);
        holder.ingredientTabRecipeName.setText(singleItem.getItemName());
    }

    public void updateList() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != itemEntityList ? itemEntityList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected LinearLayout llTabActive, llTabInactive;
        protected TextView ingredientTabPending, ingredientTabServing, ingredientTabRecipeName, ingredientTabPendingInactive;


        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.llTabActive = (LinearLayout) itemView.findViewById(R.id.ll_tab_active);
            this.llTabInactive = (LinearLayout) itemView.findViewById(R.id.ll_tab_inactive);
            this.ingredientTabPending = (TextView) itemView.findViewById(R.id.ingredient_tab_pending);
            this.ingredientTabPendingInactive = (TextView) itemView.findViewById(R.id.ingredient_tab_pending_inactive);
            this.ingredientTabServing = (TextView) itemView.findViewById(R.id.ingredient_tab_serving);
            this.ingredientTabRecipeName = (TextView) itemView.findViewById(R.id.ingredient_tab_recipe_name);

        }
    }
}
