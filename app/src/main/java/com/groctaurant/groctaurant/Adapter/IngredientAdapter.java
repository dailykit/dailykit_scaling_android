package com.groctaurant.groctaurant.Adapter;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.groctaurant.groctaurant.Activity.IngredientActivity;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.util.List;

/**
 * Created by Danish Rafique on 15-10-2018.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.SingleItemRowHolder> {

    public static final String TAG = "IngredientAdapter";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    int currentPosition;
    IngredientInternalAdapter adapter;
    private ItemEntity itemEntity;
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;
    private IngredientActivity activity;
    private List<IngredientEntity> ingredientEntityList;
    private List<IngredientDetailEntity> ingredientDetailEntityList;
    boolean fullyPacked=false;

    public IngredientAdapter(IngredientActivity activity, List<IngredientEntity> ingredientEntityList, ItemEntity itemEntity) {
        this.ingredientEntityList = ingredientEntityList;
        this.activity = activity;
        this.itemEntity = itemEntity;
        groctaurantDatabase = Room.databaseBuilder(activity, GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_list, parent,false);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final IngredientEntity singleItem = ingredientEntityList.get(position);
        //Log.e(TAG,"Ingredient Name : "+singleItem.getIngredientEntity().get(0).getIngredientName()+" IsPacked :"+singleItem.getIngredientEntity().get(0).isPacked());
        if (itemEntity.getSelectedPosition() == position && singleItem.getIngredientEntity().size() == 1) {
            //Log.e(TAG,"Layout 1");
            holder.ingredientLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
            holder.ingredientTitle.setTextColor(activity.getResources().getColor(R.color.black));
            holder.ingredientWeight.setTextColor(activity.getResources().getColor(R.color.black));
            holder.ingredientPortioning.setTextColor(activity.getResources().getColor(R.color.black));
            holder.ingredientMoreOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_left_black));
            holder.ingredientLessOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_black));
            holder.ingredientRelabel.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_label_black));
            holder.ingredientReprint.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_print_black));
            holder.ingredientDelete.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delete_black));
        } else if (itemEntity.getSelectedPosition() == position && singleItem.getIngredientEntity().size() > 1) {
            //Log.e(TAG,"Layout 2");
            holder.ingredientInnerLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
            holder.ingredientTitle.setTextColor(activity.getResources().getColor(R.color.black));
            holder.ingredientWeight.setTextColor(activity.getResources().getColor(R.color.black));
            holder.ingredientPortioning.setTextColor(activity.getResources().getColor(R.color.black));
            holder.ingredientMoreOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_left_black));
            holder.ingredientLessOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_black));
            holder.ingredientRelabel.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_label_black));
            holder.ingredientReprint.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_print_black));
            holder.ingredientDelete.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delete_black));

        } else {
            //Log.e(TAG,"Layout 3");
            holder.ingredientLayout.setBackgroundColor(0x00000000);
            holder.ingredientTitle.setTextColor(activity.getResources().getColor(R.color.white));
            holder.ingredientWeight.setTextColor(activity.getResources().getColor(R.color.white));
            holder.ingredientPortioning.setTextColor(activity.getResources().getColor(R.color.white));
            holder.ingredientMoreOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_left));
            holder.ingredientLessOption.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_arrow));
            holder.ingredientRelabel.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_label));
            holder.ingredientReprint.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_print));
            holder.ingredientDelete.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_delete));
        }


        ingredientDetailEntityList = groctaurantDatabase.ingredientDetailDao().loadIngredientDetailByIngredientId(singleItem.getIngredientId());
        if (ingredientDetailEntityList.size() == 1) {
            if (ingredientDetailEntityList.get(0).isPacked()) {
                holder.ingredientAlphaMainImage.setVisibility(View.GONE);
            } else {
                holder.ingredientAlphaMainImage.setVisibility(View.VISIBLE);
            }
            //Log.e(TAG, "Packing Value : " + groctaurantDatabase.ingredientDetailDao().isPackedComplete(singleItem.getIngredientEntity().get(0).getIngredientDetailId()) + " " + singleItem.getIngredientEntity().get(0).isPackedComplete() + " " + singleItem.getIngredientEntity().get(0).getIngredientName());
            holder.ingredientTitle.setText(singleItem.getIngredientIndex() + ") " + singleItem.getSlipName());
            holder.ingredientWeight.setText(ingredientDetailEntityList.get(0).getIngredientQuantity() + " gms");
            holder.ingredientPortioning.setText(ingredientDetailEntityList.get(0).getIngredientProcess());
            holder.ingredientFullDetailList.setVisibility(View.GONE);
            if (groctaurantDatabase.ingredientDetailDao().isPacked(ingredientDetailEntityList.get(0).getIngredientDetailId())) {
                /*if(singleItem.getIngredientEntity().get(0).isPackedComplete()){*/
                Log.e(TAG,singleItem.getSlipName()+" isPacked");
                holder.ingredientLayout.setBackgroundColor(activity.getResources().getColor(R.color.black));
                holder.ingredientAlphaImage.setVisibility(View.GONE);
                holder.ingredientAlphaMainImage.setVisibility(View.GONE);
            }

        } else {
            holder.ingredientTitle.setText(singleItem.getIngredientIndex() + ") " + singleItem.getSlipName());
            holder.ingredientWeight.setText("");
            holder.ingredientFullDetailList.setVisibility(View.VISIBLE);
            fullyPacked = true;
            for (IngredientDetailEntity ingredientDetailEntity : ingredientDetailEntityList) {
                //Log.e(TAG,"IngredientDetailEntity Position : "+position+ " Value :"+ingredientDetailEntity.toString());
                if (!ingredientDetailEntity.isPacked()) {
                    fullyPacked = false;
                }
            }
            if (fullyPacked) {
                //Log.e(TAG,"Fully Packed Item : "+singleItem.getSlipName());
                Log.e(TAG,singleItem.getSlipName()+" Fully Packed Item ");
                holder.ingredientLayout.setBackgroundColor(activity.getResources().getColor(R.color.black));
                holder.ingredientAlphaImage.setVisibility(View.GONE);
                holder.ingredientAlphaMainImage.setVisibility(View.GONE);
            }
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                holder.ingredientFullDetailList.setHasFixedSize(true);
                adapter = new IngredientInternalAdapter(activity, singleItem, position == itemEntity.getSelectedPosition(),itemEntity,position);
                holder.ingredientFullDetailList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                holder.ingredientFullDetailList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }, 100);
        }


        holder.ingredientLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(singleItem.isPackedComplete()){
                        Toast.makeText(activity, "Ingredient Already Packed", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        groctaurantDatabase.itemDao().setSelectedItem(itemEntity.getItemOrderId(), position);
                        ingredientDetailEntityList = groctaurantDatabase.ingredientDetailDao().loadIngredientDetailByIngredientId(singleItem.getIngredientId());
                        if (ingredientDetailEntityList.size() == 1) {
                            editor.putString(Constants.SELECTED_INGREDIENT_ENTITY, new Gson().toJson(ingredientDetailEntityList.get(0)));
                            editor.commit();
                        }
                        itemEntity.setSelectedPosition(position);
                        Log.e(TAG,"ChangeIngredientList Called 1");
                        activity.changeIngredientList();
                    }
                }
            });


        holder.ingredientMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e(TAG, "More Options Clicked");
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3.0f
                );
                holder.llOptions.setLayoutParams(param);

                param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                holder.llName.setLayoutParams(param);

                param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                holder.llWeight.setLayoutParams(param);
                param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                holder.llPortioning.setLayoutParams(param);
                holder.ingredientMoreOption.setVisibility(View.GONE);
                holder.llOptionRight.setVisibility(View.VISIBLE);
            }
        });

        holder.ingredientLessOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e(TAG, "Less Options Clicked");
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                holder.llOptions.setLayoutParams(param);

                param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2.0f
                );
                holder.llName.setLayoutParams(param);
                holder.llPortioning.setLayoutParams(param);

                param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                holder.llWeight.setLayoutParams(param);
                holder.llOptionRight.setVisibility(View.GONE);
                holder.ingredientMoreOption.setVisibility(View.VISIBLE);
            }
        });


        holder.ingredientDelete.setOnClickListener(new View.OnClickListener() {
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


    }

    public void updateList() {
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != ingredientEntityList ? ingredientEntityList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected RecyclerView ingredientFullDetailList;
        protected TextView ingredientTitle, ingredientWeight, ingredientPortioning;
        protected LinearLayout ingredientLayout, ingredientInnerLayout, llOptions, llName, llWeight, llOptionRight, llPortioning;
        protected ImageView ingredientAlphaImage, ingredientAlphaMainImage, ingredientMoreOption, ingredientLessOption,
                ingredientDelete, ingredientReprint, ingredientRelabel;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.ingredientFullDetailList = (RecyclerView) itemView.findViewById(R.id.ingredient_full_detail_list);
            this.ingredientTitle = (TextView) itemView.findViewById(R.id.ingredient_title);
            this.ingredientWeight = (TextView) itemView.findViewById(R.id.ingredient_weight);
            this.ingredientPortioning = (TextView) itemView.findViewById(R.id.ingredient_processing);
            this.ingredientLayout = (LinearLayout) itemView.findViewById(R.id.item_order_layout);
            this.ingredientInnerLayout = (LinearLayout) itemView.findViewById(R.id.ingredient_inner_layout);
            this.ingredientAlphaImage = (ImageView) itemView.findViewById(R.id.ingredient_alpha_image);
            this.ingredientAlphaMainImage = (ImageView) itemView.findViewById(R.id.ingredient_alpha_main_image);
            this.ingredientMoreOption = (ImageView) itemView.findViewById(R.id.ingredient_more_options);
            this.ingredientLessOption = (ImageView) itemView.findViewById(R.id.ingredient_less_option);
            this.ingredientDelete = (ImageView) itemView.findViewById(R.id.ingredient_delete);
            this.ingredientReprint = (ImageView) itemView.findViewById(R.id.ingredient_reprint);
            this.ingredientRelabel = (ImageView) itemView.findViewById(R.id.ingredient_relabel);
            this.llOptions = (LinearLayout) itemView.findViewById(R.id.ll_options);
            this.llPortioning = (LinearLayout) itemView.findViewById(R.id.ll_ingredient_processing);
            this.llName = (LinearLayout) itemView.findViewById(R.id.ll_ingredient_name);
            this.llWeight = (LinearLayout) itemView.findViewById(R.id.ll_ingredient_weight);
            this.llOptionRight = (LinearLayout) itemView.findViewById(R.id.ll_option_right);
        }
    }
}

