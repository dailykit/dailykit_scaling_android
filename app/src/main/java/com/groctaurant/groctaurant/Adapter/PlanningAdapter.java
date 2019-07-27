package com.groctaurant.groctaurant.Adapter;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.groctaurant.groctaurant.Activity.PlanningPackingActivity;
import com.groctaurant.groctaurant.Model.PlanningModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.util.List;

/**
 * Created by Danish Rafique on 10-03-2019.
 */
public class PlanningAdapter extends RecyclerView.Adapter<PlanningAdapter.SingleItemRowHolder> {

    public static final String TAG = "PlanningAdapter";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    PlanningProcessAdapter adapter;
    private List<PlanningModel> planningModelList;
    private Activity activity;
    GroctaurantDatabase groctaurantDatabase;

    public PlanningAdapter(Activity activity, List<PlanningModel> planningModelList) {
        this.planningModelList = planningModelList;
        this.activity = activity;
    }

    @Override
    public PlanningAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_planning_element, parent,false);
        PlanningAdapter.SingleItemRowHolder rowHolder = new PlanningAdapter.SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final PlanningAdapter.SingleItemRowHolder holder, final int position) {
        final PlanningModel singleItem = planningModelList.get(position);
        adapter = new PlanningProcessAdapter(activity,singleItem.getIngredientPlanningModels(),singleItem);
        holder.planningProcessList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        holder.planningProcessList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        holder.ingredientName.setText(singleItem.getIngredientName());
        if(singleItem.getIngredientTotalWeight()>=1000){
            holder.ingredientTotalWeight.setText(singleItem.getIngredientTotalWeight()/1000+" kg");
        }
        else {
            holder.ingredientTotalWeight.setText(singleItem.getIngredientTotalWeight()+" gm");
        }
        holder.planningLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt(Constants.SELECTED_PLANNING_MODEL_ID,singleItem.getPlanningModelId());
                editor.commit();
                Intent intent=new Intent(activity, PlanningPackingActivity.class);
                activity.startActivity(intent);
            }
        });

    }

    public void updateList() {
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != planningModelList ? planningModelList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private RecyclerView planningProcessList;
        private TextView ingredientName,ingredientTotalWeight;
        private LinearLayout planningLayout;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.planningProcessList = (RecyclerView) itemView.findViewById(R.id.planning_process_list);
            this.ingredientName = (TextView) itemView.findViewById(R.id.ingredient_name);
            this.ingredientTotalWeight = (TextView) itemView.findViewById(R.id.ingredient_total_weight);
            this.planningLayout = (LinearLayout) itemView.findViewById(R.id.planning_layout);
        }
    }
}

