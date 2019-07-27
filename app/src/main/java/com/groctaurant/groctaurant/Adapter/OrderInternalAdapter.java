package com.groctaurant.groctaurant.Adapter;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.groctaurant.groctaurant.Activity.IngredientActivity;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Room.Entity.TabEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.util.List;


/**
 * Created by Danish Rafique on 06-10-2018.
 */
public class OrderInternalAdapter extends RecyclerView.Adapter<OrderInternalAdapter.SingleItemRowHolder> {

    public static final String TAG = "OrderScreenInternal";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    int currentPosition;

    private Activity activity;
    GroctaurantDatabase groctaurantDatabase;
    private List<ItemEntity> itemEntityList;
    private String orderId;
    private String currentSelectedItem;

    public OrderInternalAdapter(Activity activity, List<ItemEntity> itemEntityList, int currentPosition, String orderId) {

        this.activity = activity;
        this.currentPosition = currentPosition;
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        this.itemEntityList = itemEntityList;
        this.orderId = orderId;
        this.currentSelectedItem=sharedpreferences.getString(Constants.SELECTED_ITEM_ID,"");
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent,false);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(v);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final ItemEntity singleItem = itemEntityList.get(position);
        holder.itemOrderName.setText(singleItem.getItemName());
        String servingValue[] = singleItem.getItemServing().split(",");
        holder.itemOrderServing.setText(servingValue[position]);
        int numberOfPackedIngredients = groctaurantDatabase.ingredientDao().countIngredientPackedDao(singleItem.getItemOrderId(), true);
        holder.itemOrderPending.setText(numberOfPackedIngredients + "/" + singleItem.getItemNoOfIngredient());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(25,10,25,10);
        params.height = 80;
        holder.itemOrderLayout.setLayoutParams(params);
        LinearLayout.LayoutParams paramOption = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT);
        paramOption.weight = 1.0f;

        if(numberOfPackedIngredients==Integer.parseInt(singleItem.getItemNoOfIngredient())){
            holder.itemOrderLayout.setBackgroundColor(activity.getResources().getColor(R.color.black));
            holder.orderAlphaMainImage.setVisibility(View.GONE);
        }
        else{
            holder.itemOrderLayout.setBackgroundColor(0x00000000);
            holder.orderAlphaMainImage.setVisibility(View.VISIBLE);
        }

        holder.itemOrderOption.setLayoutParams(paramOption);
        holder.itemOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabEntity tabEntity = new TabEntity(singleItem.getOrderId(), itemEntityList.get(0).getItemNumber(), position);
                editor.putInt(Constants.ACTIVE_POSITION, position);
                IngredientDetailEntity ingredientDetailEntity = new IngredientDetailEntity();
                ingredientDetailEntity.setIngredientName("");
                ingredientDetailEntity.setIngredientQuantity(0);
                editor.putString(Constants.SELECTED_ITEM_ID, singleItem.getItemOrderId());
                editor.putString(Constants.SELECTED_INGREDIENT_ENTITY, new Gson().toJson(ingredientDetailEntity));
                editor.putString(Constants.SELECTED_ORDER_ID, orderId);
                editor.commit();
                groctaurantDatabase.tabDao().insert(tabEntity);
                Intent intent = new Intent(activity, IngredientActivity.class);
                activity.startActivity(intent);
            }
        });

        //Log.e(TAG,"Id :"+sharedpreferences.getString(Constants.SELECTED_ITEM_ID,"")+" "+singleItem.getItemOrderId());

        if(currentSelectedItem.equals(singleItem.getItemOrderId())){
            holder.imgOnline.setVisibility(View.VISIBLE);
        }
        else{
            holder.imgOnline.setVisibility(View.GONE);
        }


    }

    public void updateList() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != itemEntityList ? itemEntityList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected com.groctaurant.groctaurant.CustomView.FuturaMediumTextView itemOrderServing, itemOrderPending, itemOrderName;
        protected LinearLayout itemOrderLayout;
        protected LinearLayout itemOrderOption;
        protected ImageView orderAlphaMainImage,imgOnline;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.itemOrderServing = (com.groctaurant.groctaurant.CustomView.FuturaMediumTextView) itemView.findViewById(R.id.item_order_serving);
            this.itemOrderPending = (com.groctaurant.groctaurant.CustomView.FuturaMediumTextView) itemView.findViewById(R.id.item_order_pending);
            this.itemOrderName = (com.groctaurant.groctaurant.CustomView.FuturaMediumTextView) itemView.findViewById(R.id.item_order_name);
            this.itemOrderLayout = (LinearLayout) itemView.findViewById(R.id.item_order_layout);
            this.itemOrderOption = (LinearLayout) itemView.findViewById(R.id.ll_options);
            this.orderAlphaMainImage = (ImageView) itemView.findViewById(R.id.order_alpha_main_image);
            this.imgOnline = (ImageView) itemView.findViewById(R.id.img_online);
        }
    }
}
