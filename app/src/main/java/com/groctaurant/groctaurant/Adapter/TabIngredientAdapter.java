package com.groctaurant.groctaurant.Adapter;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.groctaurant.groctaurant.Activity.IngredientActivity;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.TabEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.util.List;

/**
 * Created by Danish Rafique on 10-11-2018.
 */
public class TabIngredientAdapter extends RecyclerView.Adapter<TabIngredientAdapter.SingleItemRowHolder> {

    public static final String TAG = "TabIngredientAdapter";
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    int currentPosition;
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;
    private List<TabEntity> tabEntityList;
    private IngredientActivity activity;

    public TabIngredientAdapter(Activity activity, List<TabEntity> tabEntityList) {
        this.tabEntityList = tabEntityList;
        this.activity = (IngredientActivity) activity;
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tabs, parent,false);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final TabEntity singleItem = tabEntityList.get(position);
        String selectedOrderId = sharedpreferences.getString(Constants.SELECTED_ORDER_ID, "");
        if (!selectedOrderId.equalsIgnoreCase(singleItem.getOrderId())) {
            holder.tabText.setTextColor(activity.getResources().getColor(R.color.white));
            holder.tabTextCancel.setTextColor(activity.getResources().getColor(R.color.white));
            holder.tabCancel.setBackgroundColor(0x00000000);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.imageView.setBackgroundColor(0x00000000);
            }
        } else {
            holder.tabText.setTextColor(activity.getResources().getColor(R.color.black));
            holder.tabTextCancel.setTextColor(activity.getResources().getColor(R.color.black));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.tabCancel.setBackground(activity.getResources().getDrawable(R.drawable.round_circle_white));
                holder.imageView.setBackground(activity.getResources().getDrawable(R.drawable.round_circle_white));
            }
        }
        holder.tabText.setText(singleItem.getOrderNumber());
        holder.tabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groctaurantDatabase.tabDao().delete(singleItem);
                activity.observeTab();
            }
        });
        holder.tabRoundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt(Constants.ACTIVE_POSITION, singleItem.getActivePosition());
                editor.putString(Constants.SELECTED_ORDER_ENTITY, new Gson().toJson(groctaurantDatabase.orderDao().loadOrder(singleItem.getOrderId())));

                IngredientDetailEntity ingredientDetailEntity = new IngredientDetailEntity();
                ingredientDetailEntity.setIngredientName("");
                ingredientDetailEntity.setIngredientQuantity(0);
                editor.putString(Constants.SELECTED_INGREDIENT_ENTITY, new Gson().toJson(ingredientDetailEntity));
                editor.putString(Constants.SELECTED_ORDER_ID, singleItem.getOrderId());
                editor.commit();
                Intent intent = new Intent(activity, IngredientActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    public void updateList() {
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != tabEntityList ? tabEntityList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tabText, tabTextCancel;
        protected FrameLayout tabCancel;
        protected FrameLayout tabRoundLayout, tabActive, tabRoundLayoutInactive, tabInactive;
        protected ImageView imageView;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.tabText = (TextView) itemView.findViewById(R.id.tab_text);
            this.tabTextCancel = (TextView) itemView.findViewById(R.id.tab_cancel_text);
            this.tabCancel = (FrameLayout) itemView.findViewById(R.id.tab_cancel);
            this.tabRoundLayout = (FrameLayout) itemView.findViewById(R.id.tab_round_layout);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}



