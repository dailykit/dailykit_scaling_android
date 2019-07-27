package com.groctaurant.groctaurant.Adapter;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Room.Entity.OrderEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.util.List;

/**
 * Created by Danish Rafique on 09-10-2018.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.SingleItemRowHolder> {

    public static final String TAG = "OrderAdapter";
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    int currentPosition;
    OrderInternalAdapter adapter;
    private List<OrderEntity> allDetailModelArrayList;
    private Activity activity;
    GroctaurantDatabase groctaurantDatabase;
    private List<ItemEntity> itemEntityList;

    public OrderAdapter(Activity activity, List<OrderEntity> allDetailModelArrayList) {
        this.allDetailModelArrayList = allDetailModelArrayList;
        this.activity = activity;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent,false);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final OrderEntity singleItem = allDetailModelArrayList.get(position);
        holder.orderList.setMinimumWidth(sharedpreferences.getInt(Constants.WIDTH_OF_ORDER_LIST, 1500));
        holder.orderList.setHasFixedSize(true);
        itemEntityList = groctaurantDatabase.itemDao().loadItemsByOrderId(singleItem.getOrderId());
        adapter = new OrderInternalAdapter(activity, itemEntityList, position, singleItem.getOrderId());
        itemEntityList = groctaurantDatabase.itemDao().loadItems();
        holder.orderList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        holder.orderList.setAdapter(adapter);
        holder.orderName.setText(singleItem.getOrderId());
        adapter.notifyDataSetChanged();

    }

    public void updateList() {
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != allDetailModelArrayList ? allDetailModelArrayList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected RecyclerView orderList;
        protected TextView orderName;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.orderList = (RecyclerView) itemView.findViewById(R.id.order_internal_list);
            this.orderName = (TextView) itemView.findViewById(R.id.order_name);
        }
    }
}

