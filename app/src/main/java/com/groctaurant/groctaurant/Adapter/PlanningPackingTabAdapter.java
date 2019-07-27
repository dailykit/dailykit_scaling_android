package com.groctaurant.groctaurant.Adapter;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.groctaurant.groctaurant.Activity.PlanningPackingActivity;
import com.groctaurant.groctaurant.Model.IngredientPlanningModel;
import com.groctaurant.groctaurant.Model.PlanningModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

/**
 * Created by Danish Rafique on 10-03-2019.
 */
public class PlanningPackingTabAdapter extends RecyclerView.Adapter<PlanningPackingTabAdapter.SingleItemRowHolder> {

    public static final String TAG = "PlanningPackingTab";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private PlanningModel planningModel;
    private PlanningPackingActivity activity;
    GroctaurantDatabase groctaurantDatabase;

    public PlanningPackingTabAdapter(Activity activity, PlanningModel planningModel) {
        this.planningModel = planningModel;
        this.activity = (PlanningPackingActivity)activity;
    }

    @Override
    public PlanningPackingTabAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_planning_packing_tab_element, parent,false);
        PlanningPackingTabAdapter.SingleItemRowHolder rowHolder = new PlanningPackingTabAdapter.SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final IngredientPlanningModel singleItem = planningModel.getIngredientPlanningModels().get(position);
        if(planningModel.getIngredientSelectedProcessIndex()==position){
            holder.planningPackingValue.setTextColor(activity.getResources().getColor(R.color.black));
            holder.planningPackingTabProcessName.setTextColor(activity.getResources().getColor(R.color.black));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.planningPackingTabLayout.setBackground(activity.getDrawable(R.drawable.round_white_button));
            }
            activity.updatePackingList(singleItem);
        }
        else{
            holder.planningPackingValue.setTextColor(activity.getResources().getColor(R.color.white));
            holder.planningPackingTabProcessName.setTextColor(activity.getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.planningPackingTabLayout.setBackground(activity.getDrawable(R.drawable.round_transparent_button));
            }
        }
        holder.planningPackingTabLayout.setPadding(20,0,20,0);
        holder.planningPackingValue.setText(singleItem.getIngredientNumberOfUnitsPacked()+"/"+singleItem.getIngredientNumberOfUnits());
        holder.planningPackingTabProcessName.setText(singleItem.getIngredientProcessing());

        holder.planningPackingTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groctaurantDatabase.planningDao().setSelectedProcessIndex(singleItem.getPlanningModelId(),position);
                activity.updateTabList();
            }
        });
    }

    public void updateList() {
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != planningModel.getIngredientPlanningModels() ? planningModel.getIngredientPlanningModels().size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private TextView planningPackingTabProcessName,planningPackingValue;
        private LinearLayout planningPackingTabLayout;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.planningPackingTabProcessName = (TextView) itemView.findViewById(R.id.planning_packing_tab_process_name);
            this.planningPackingValue = (TextView) itemView.findViewById(R.id.planning_packing_value);
            this.planningPackingTabLayout = (LinearLayout) itemView.findViewById(R.id.planning_packing_tab_layout);
        }
    }
}


