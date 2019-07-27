package com.groctaurant.groctaurant.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.groctaurant.groctaurant.Activity.PlanningPackingActivity;
import com.groctaurant.groctaurant.CustomView.FuturaMediumTextView;
import com.groctaurant.groctaurant.Model.IngredientPlanningModel;
import com.groctaurant.groctaurant.Model.PlanningDetailModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Danish Rafique on 10-03-2019.
 */
public class PlanningIngredientsAdapter extends RecyclerView.Adapter<PlanningIngredientsAdapter.SingleItemRowHolder> {

    public static final String TAG = "PlanningIngredients";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    PlanningProcessAdapter adapter;
    private List<PlanningDetailModel> planningDetailModelList;
    private PlanningPackingActivity activity;
    GroctaurantDatabase groctaurantDatabase;
    private List<IngredientDetailEntity> ingredientDetailEntityList;
    IngredientPlanningModel ingredientPlanningModel;
    PlanningDetailModel menuOptionPlanningDetailModel;

    public PlanningIngredientsAdapter(Activity activity,IngredientPlanningModel ingredientPlanningModel) {
        Log.e(TAG,"Constructor");
        this.ingredientPlanningModel=ingredientPlanningModel;
        this.planningDetailModelList = ingredientPlanningModel.getPlanningDetailModelArrayList();
        this.activity = (PlanningPackingActivity)activity;
    }

    @Override
    public PlanningIngredientsAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_planning_ingredient_element, parent,false);
        PlanningIngredientsAdapter.SingleItemRowHolder rowHolder = new PlanningIngredientsAdapter.SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        Type type = new TypeToken<PlanningDetailModel>() {}.getType();
        menuOptionPlanningDetailModel = new Gson().fromJson(sharedpreferences.getString(Constants.MENU_OPTION_PLANNING_INGREDIENT_MODEL, ""), type);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final PlanningIngredientsAdapter.SingleItemRowHolder holder, final int position) {
        final PlanningDetailModel singleItem = planningDetailModelList.get(position);
        if(singleItem.isIngredientIsPacked()){
            holder.planningIngredientLayout.setBackgroundColor(activity.getResources().getColor(R.color.black));
            holder.planningIngredientMainAlphaImage.setVisibility(View.GONE);
            holder.planningIngredientSlipName.setTextColor(activity.getResources().getColor(R.color.white));
            holder.planningIngredientOrderId.setTextColor(activity.getResources().getColor(R.color.white));
            holder.planningIngredientWeight.setTextColor(activity.getResources().getColor(R.color.white));
            holder.planningIngredientMoreOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_left));
            holder.planningIngredientLessOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_arrow));
            holder.planningIngredientRelabel.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_label));
            holder.planningIngredientReprint.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_print));
            holder.planningIngredientDelete.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delete));
        }
        else if(ingredientPlanningModel.getIngredientPlanningSelectedPosition()==position){
            holder.planningIngredientLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
            holder.planningIngredientSlipName.setTextColor(activity.getResources().getColor(R.color.black));
            holder.planningIngredientOrderId.setTextColor(activity.getResources().getColor(R.color.black));
            holder.planningIngredientWeight.setTextColor(activity.getResources().getColor(R.color.black));
            holder.planningIngredientMoreOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_left_black));
            holder.planningIngredientLessOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_black));
            holder.planningIngredientRelabel.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_label_black));
            holder.planningIngredientReprint.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_print_black));
            holder.planningIngredientDelete.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delete_black));
        }
        else{
            holder.planningIngredientLayout.setBackgroundColor(0x00000000);
            holder.planningIngredientSlipName.setTextColor(activity.getResources().getColor(R.color.white));
            holder.planningIngredientOrderId.setTextColor(activity.getResources().getColor(R.color.white));
            holder.planningIngredientWeight.setTextColor(activity.getResources().getColor(R.color.white));
            holder.planningIngredientMoreOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_left));
            holder.planningIngredientLessOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_arrow));
            holder.planningIngredientRelabel.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_label));
            holder.planningIngredientReprint.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_print));
            holder.planningIngredientDelete.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delete));
        }


        holder.planningIngredientSlipName.setText((position+1)+") "+singleItem.getItemName());
        holder.planningIngredientOrderId.setText(singleItem.getIngredientId().substring(0,singleItem.getIngredientId().indexOf("_")));
        holder.planningIngredientWeight.setText(singleItem.getIngredientWeight()+" gm");
        holder.planningIngredientLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(singleItem.isIngredientIsPacked()){
                    Toast.makeText(activity, "Ingredient Already Packed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e(TAG,singleItem.toString());
                    Log.e(TAG,"Position : "+position);
                    groctaurantDatabase.ingredientPlanningDao().setSelectedPosition(singleItem.getIngredientPlanningId(),position);
                    activity.setScreenDetail(singleItem);
                    activity.setSelectedPlanningPosition(position);
                    editor.putString(Constants.MENU_OPTION_PLANNING_INGREDIENT_MODEL,null);
                    editor.putString(Constants.SELECTED_PLANNING_INGREDIENT_MODEL,new Gson().toJson(singleItem));
                    editor.commit();

                }
            }
        });

        if(menuOptionPlanningDetailModel!=null && menuOptionPlanningDetailModel.getIngredientDetailId().equals(singleItem.getIngredientDetailId())){
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    3.0f
            );
            holder.planningIngredientOptions.setLayoutParams(param);
            holder.planningIngredientSlipNameLayout.setLayoutParams(param);
            param = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            holder.planningIngredientWeightLayout.setLayoutParams(param);
            holder.planningIngredientMoreOption.setVisibility(View.GONE);
            holder.planningIngredientOptionRight.setVisibility(View.VISIBLE);
            holder.planningIngredientOrderIdLayout.setVisibility(View.GONE);
            if(holder.planningIngredientSlipName.getText().toString().length()>25) {
                holder.planningIngredientSlipName.setText((position + 1) + ") " + singleItem.getItemName().substring(0, 23) + "...");
            }
        }


        holder.planningIngredientMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groctaurantDatabase.ingredientPlanningDao().setSelectedPosition(singleItem.getIngredientPlanningId(),position);
                activity.setScreenDetail(singleItem);
                activity.setSelectedPlanningPosition(position);
                editor.putString(Constants.SELECTED_PLANNING_INGREDIENT_MODEL,new Gson().toJson(singleItem));
                editor.putString(Constants.MENU_OPTION_PLANNING_INGREDIENT_MODEL,new Gson().toJson(singleItem));
                editor.commit();
                /*LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3.0f
                );
                holder.planningIngredientOptions.setLayoutParams(param);
                holder.planningIngredientSlipNameLayout.setLayoutParams(param);
                param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                holder.planningIngredientWeightLayout.setLayoutParams(param);
                holder.planningIngredientMoreOption.setVisibility(View.GONE);
                holder.planningIngredientOptionRight.setVisibility(View.VISIBLE);
                holder.planningIngredientOrderIdLayout.setVisibility(View.GONE);
                if(holder.planningIngredientSlipName.getText().toString().length()>25) {
                    holder.planningIngredientSlipName.setText((position + 1) + ") " + singleItem.getItemName().substring(0, 23) + "...");
                }*/

                /*if(singleItem.isIngredientIsPacked()){
                    holder.planningIngredientLayout.setBackgroundColor(activity.getResources().getColor(R.color.black));
                    holder.planningIngredientMainAlphaImage.setVisibility(View.GONE);
                    holder.planningIngredientSlipName.setTextColor(activity.getResources().getColor(R.color.white));
                    holder.planningIngredientOrderId.setTextColor(activity.getResources().getColor(R.color.white));
                    holder.planningIngredientWeight.setTextColor(activity.getResources().getColor(R.color.white));
                    holder.planningIngredientMoreOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_left));
                    holder.planningIngredientLessOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_arrow));
                    holder.planningIngredientRelabel.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_label));
                    holder.planningIngredientReprint.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_print));
                    holder.planningIngredientDelete.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delete));
                }
                else{
                    holder.planningIngredientLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
                    holder.planningIngredientSlipName.setTextColor(activity.getResources().getColor(R.color.black));
                    holder.planningIngredientOrderId.setTextColor(activity.getResources().getColor(R.color.black));
                    holder.planningIngredientWeight.setTextColor(activity.getResources().getColor(R.color.black));
                    holder.planningIngredientMoreOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_left_black));
                    holder.planningIngredientLessOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_black));
                    holder.planningIngredientRelabel.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_label_black));
                    holder.planningIngredientReprint.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_print_black));
                    holder.planningIngredientDelete.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delete_black));
                }*/
            }
        });


        holder.planningIngredientLessOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2.0f
                );

                holder.planningIngredientSlipNameLayout.setLayoutParams(param);
                param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3.0f
                );
                holder.planningIngredientOrderIdLayout.setLayoutParams(param);
                param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                holder.planningIngredientWeightLayout.setLayoutParams(param);
                holder.planningIngredientOptions.setLayoutParams(param);
                holder.planningIngredientMoreOption.setVisibility(View.VISIBLE);
                holder.planningIngredientOptionRight.setVisibility(View.GONE);
                holder.planningIngredientOrderIdLayout.setVisibility(View.VISIBLE);
                holder.planningIngredientMoreOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_left_black));
                holder.planningIngredientSlipName.setText((position + 1) + ") " + singleItem.getItemName());
                editor.putString(Constants.MENU_OPTION_PLANNING_INGREDIENT_MODEL,null);
                editor.commit();
            }
        });


        /*holder.ingredientDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }
                builder.setTitle("Delete Ingredient")
                        .setMessage("Are you sure you want to delete this ingredient?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                groctaurantDatabase.ingredientDao().isDeletedUpdate(singleItem.getIngredientId(),true);
                                Toast.makeText(activity, "Ingredient Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"ChangeIngredientList Called 2");
                                activity.changeIngredientList();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


        holder.ingredientReprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }
                builder.setTitle("Reprint Label")
                        .setMessage("Are you sure you want to reprint the label for this ingredient?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(activity, "Printing Label", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        holder.ingredientRelabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }
                builder.setTitle("Repack Ingredient")
                        .setMessage("Are you sure you want to repack this ingredient?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                groctaurantDatabase.ingredientDao().isPackedCompleteUpdate(singleItem.getIngredientId(),false);
                                groctaurantDatabase.ingredientDetailDao().isPackedUpdateByIngredientId(singleItem.getIngredientId(),false);
                                Toast.makeText(activity, "Unpacked Ingredient", Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"ChangeIngredientList Called 3");
                                activity.changeIngredientList();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
*/




    }


    public void updateList() {
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != planningDetailModelList ? planningDetailModelList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private LinearLayout planningIngredientLayout,planningIngredientInnerLayout,planningIngredientSlipNameLayout,
                planningIngredientOrderIdLayout,planningIngredientWeightLayout,planningIngredientOptions,planningIngredientOptionRight;
        private ImageView planningIngredientMainAlphaImage,planningIngredientAlphaImage,planningIngredientMoreOption,
                planningIngredientRelabel,planningIngredientReprint,planningIngredientDelete,
                planningIngredientLessOption;
        private FuturaMediumTextView planningIngredientSlipName,planningIngredientOrderId,planningIngredientWeight;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.planningIngredientLayout = (LinearLayout) itemView.findViewById(R.id.planning_ingredient_layout);
            this.planningIngredientMainAlphaImage = (ImageView) itemView.findViewById(R.id.planning_ingredient_main_alpha_image);
            this.planningIngredientInnerLayout = (LinearLayout) itemView.findViewById(R.id.planning_ingredient_inner_layout);
            this.planningIngredientSlipNameLayout = (LinearLayout) itemView.findViewById(R.id.planning_ingredient_slip_name_layout);
            this.planningIngredientSlipName = (FuturaMediumTextView) itemView.findViewById(R.id.planning_ingredient_slip_name);
            this.planningIngredientOrderIdLayout = (LinearLayout) itemView.findViewById(R.id.planning_ingredient_order_id_layout);
            this.planningIngredientOrderId = (FuturaMediumTextView) itemView.findViewById(R.id.planning_ingredient_order_id);
            this.planningIngredientWeightLayout = (LinearLayout) itemView.findViewById(R.id.planning_ingredient_weight_layout);
            this.planningIngredientWeight = (FuturaMediumTextView) itemView.findViewById(R.id.planning_ingredient_weight);
            this.planningIngredientOptions = (LinearLayout) itemView.findViewById(R.id.planning_ingredient_options);
            this.planningIngredientAlphaImage = (ImageView) itemView.findViewById(R.id.planning_ingredient_alpha_image);
            this.planningIngredientMoreOption = (ImageView) itemView.findViewById(R.id.planning_ingredient_more_options);
            this.planningIngredientOptionRight = (LinearLayout) itemView.findViewById(R.id.planning_ingredient_option_right);
            this.planningIngredientRelabel = (ImageView) itemView.findViewById(R.id.planning_ingredient_relabel);
            this.planningIngredientReprint = (ImageView) itemView.findViewById(R.id.planning_ingredient_reprint);
            this.planningIngredientDelete = (ImageView) itemView.findViewById(R.id.planning_ingredient_delete);
            this.planningIngredientLessOption = (ImageView) itemView.findViewById(R.id.planning_ingredient_less_option);
        }
    }
}


