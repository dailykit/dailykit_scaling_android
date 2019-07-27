package com.groctaurant.groctaurant.LegacyCode;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groctaurant.groctaurant.Model.AllDetailModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by Danish Rafique on 26-07-2018.
 */
public class OldOrderAdapter extends RecyclerView.Adapter<OldOrderAdapter.SingleItemRowHolder>  {

    private ArrayList<AllDetailModel> allDetailModelArrayList;
    private Activity activity;
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String TAG ="OldOrderAdapter";
    int currentPosition;
    OldOrderInternalAdapter adapter;

    public OldOrderAdapter(Activity activity, ArrayList<AllDetailModel> allDetailModelArrayList) {
        this.allDetailModelArrayList = allDetailModelArrayList;
        this.activity = activity;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_list, null);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final AllDetailModel singleItem = allDetailModelArrayList.get(position);
        holder.orderInternalList.setMinimumWidth(sharedpreferences.getInt(Constants.WIDTH_OF_ORDER_LIST,1500));

        holder.orderInternalList.setHasFixedSize(true);

        adapter = new OldOrderInternalAdapter(activity,singleItem,position);
        holder.orderInternalList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        holder.orderInternalList.setAdapter(adapter);
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

        protected RecyclerView orderInternalList;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.orderInternalList = (RecyclerView) itemView.findViewById(R.id.order_internal_list);
        }
    }
}
