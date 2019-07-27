package com.groctaurant.groctaurant.Adapter;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.groctaurant.groctaurant.Activity.PlanningActivity;
import com.groctaurant.groctaurant.Activity.PlanningPackingActivity;
import com.groctaurant.groctaurant.Model.IngredientPlanningModel;
import com.groctaurant.groctaurant.Model.PlanningModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.util.List;

/**
 * Created by Danish Rafique on 10-03-2019.
 */
public class PlanningProcessAdapter extends RecyclerView.Adapter<PlanningProcessAdapter.SingleItemRowHolder> {

    public static final String TAG = "PlanningProcessAdapter";
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;
    private List<IngredientPlanningModel> ingredientPlanningModelList;
    private PlanningActivity activity;
    private PlanningModel planningModel;

    public PlanningProcessAdapter(Activity activity, List<IngredientPlanningModel> ingredientPlanningModelList, PlanningModel planningModel) {
        this.ingredientPlanningModelList = ingredientPlanningModelList;
        this.activity = (PlanningActivity) activity;
        this.planningModel=planningModel;
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
    }

    @Override
    public PlanningProcessAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_planning_process_element, parent,false);
        PlanningProcessAdapter.SingleItemRowHolder rowHolder = new PlanningProcessAdapter.SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final PlanningProcessAdapter.SingleItemRowHolder holder, final int position) {

        final IngredientPlanningModel singleItem = ingredientPlanningModelList.get(position);
        holder.planningProcessName.setText(singleItem.getIngredientProcessing());
        holder.planningProcessPending.setText(singleItem.getIngredientNumberOfUnitsPacked()+"/"+singleItem.getIngredientNumberOfUnits());
        if(singleItem.getIngredientConsolidatedWeight()>=1000){
            holder.planningProcessConsolidatedWeight.setText(singleItem.getIngredientConsolidatedWeight()/1000+" kg");
        }
        else {
            holder.planningProcessConsolidatedWeight.setText(singleItem.getIngredientConsolidatedWeight()+" gm");
        }

        holder.planningProcessLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt(Constants.SELECTED_PLANNING_MODEL_ID,singleItem.getPlanningModelId());
                editor.commit();
                groctaurantDatabase.planningDao().setSelectedProcessIndex(singleItem.getPlanningModelId(),position);
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
        return (null != ingredientPlanningModelList ? ingredientPlanningModelList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private TextView planningProcessPending,planningProcessName,planningProcessConsolidatedWeight;
        private LinearLayout planningProcessLayout,llOptions;
        private ImageView imgOnline,planningProcessAlphaImage;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.planningProcessPending = (TextView) itemView.findViewById(R.id.planning_process_pending);
            this.planningProcessName = (TextView) itemView.findViewById(R.id.planning_process_name);
            this.planningProcessConsolidatedWeight = (TextView) itemView.findViewById(R.id.planning_process_consolidated_weight);
            this.planningProcessLayout = (LinearLayout) itemView.findViewById(R.id.planning_process_layout);
            this.llOptions = (LinearLayout) itemView.findViewById(R.id.ll_options);
            this.imgOnline = (ImageView) itemView.findViewById(R.id.img_online);
            this.planningProcessAlphaImage = (ImageView) itemView.findViewById(R.id.planning_process_alpha_image);
        }
    }
}



