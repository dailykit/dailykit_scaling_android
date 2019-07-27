package com.groctaurant.groctaurant.LegacyCode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.groctaurant.groctaurant.Model.AllDetailModel;
import com.groctaurant.groctaurant.Model.OrderDetailModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;


/**
 * Created by Danish Rafique on 26-07-2018.
 */
public class OldOrderInternalAdapter extends RecyclerView.Adapter<OldOrderInternalAdapter.SingleItemRowHolder>  {

    private AllDetailModel allDetailModel;
    private Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String TAG ="OldOrderInternalAdapter";
    int currentPosition;

    public OldOrderInternalAdapter(Activity activity, AllDetailModel allDetailModel, int currentPosition) {
        this.allDetailModel = allDetailModel;
        this.activity = activity;
        this.currentPosition=currentPosition;
    }

    @Override
    public OldOrderInternalAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_element, null);
        OldOrderInternalAdapter.SingleItemRowHolder rowHolder = new OldOrderInternalAdapter.SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final OrderDetailModel singleItem = allDetailModel.getAllOrderArrayList().get(position);
        holder.llOrderDetail.setMinimumWidth(sharedpreferences.getInt(Constants.WIDTH_OF_ORDER_LIST,1500));

        holder.orderText.setText((currentPosition+1)+"/"+(position+1)+" - "+singleItem.getOrderName()+" - "+singleItem.getServing()+" Servings ("+singleItem.getPendingRequest()+"/"+singleItem.getAllRequest()+")\t x "+singleItem.getQuantity());
        holder.orderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(Constants.CURRENT_ORDER_MODEL, new Gson().toJson(singleItem));
                editor.commit();
                Intent intentOrderDetail=new Intent(activity, OldOrderDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable(Constants.CURRENT_ORDER,singleItem);
                intentOrderDetail.putExtra(Constants.INTENT_CURRENT_ORDER_DETAIL,bundle);
                activity.startActivity(intentOrderDetail);

            }
        });

    }
    public void updateList() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != allDetailModel.getAllOrderArrayList() ? allDetailModel.getAllOrderArrayList().size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView orderText;
        protected LinearLayout llOrderDetail;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.orderText = (TextView) itemView.findViewById(R.id.order_text);
            this.llOrderDetail=(LinearLayout)itemView.findViewById(R.id.ll_order_detail);
        }
    }
}
