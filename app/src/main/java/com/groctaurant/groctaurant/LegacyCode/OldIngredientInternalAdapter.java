package com.groctaurant.groctaurant.LegacyCode;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.groctaurant.groctaurant.Model.AllIngredientModel;
import com.groctaurant.groctaurant.Model.IngredientModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.lang.reflect.Type;


/**
 * Created by Danish Rafique on 31-07-2018.
 */
public class OldIngredientInternalAdapter extends RecyclerView.Adapter<OldIngredientInternalAdapter.SingleItemRowHolder>  {

    private AllIngredientModel allIngredientModel;
    private Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String TAG ="IngredientInternal";
    int currentPosition;
    IngredientModel ingredientModel;
    OldIngredientAdapter ingredientAdapter;
    Dialog ingredientSettingDialog;

    public OldIngredientInternalAdapter(Activity activity, AllIngredientModel allIngredientModel, int currentPosition, OldIngredientAdapter ingredientAdapter) {
        this.allIngredientModel = allIngredientModel;
        this.activity = activity;
        this.currentPosition=currentPosition;
        this.ingredientAdapter=ingredientAdapter;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_detail, null);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final IngredientModel singleItem = allIngredientModel.getIngredientModelArrayList().get(position);
        holder.llIngredientDetail.setMinimumWidth(sharedpreferences.getInt(Constants.WIDTH_OF_INGREDIENT_LIST,1228)-40);


        final Handler handler = new Handler();
        handler.postDelayed (() -> {
            //Log.e(TAG,"Linear Width :"+holder.llIngredientDetail.getWidth());
            if(position==0){
                holder.ingredientSlipName.setText(allIngredientModel.getIngredientSlipName());
            } else{
                holder.ingredientSlipName.setText("");
            }
            holder.ingredientName.setText(singleItem.getIngredientName());
            holder.ingredientWeight.setText(String.valueOf(singleItem.getIngredientQuantity()));
            holder.ingredientVolume.setText(singleItem.getIngredientMsr());
            holder.ingredientProcess.setText(singleItem.getIngredientProcess());
            holder.ingredientSection.setText(singleItem.getIngredientSection());
        }, 100);
        Type type = new TypeToken<IngredientModel>() {}.getType();
        ingredientModel = new Gson().fromJson(sharedpreferences.getString(Constants.CURRENT_INGREDIENT, ""), type);
        if(ingredientModel!=null && ingredientModel.getIngredientName()!=null && ingredientModel.getIngredientName().equals(singleItem.getIngredientName())){
            //Log.e(TAG,ingredientModel.getIngredientName()+" "+singleItem.getIngredientName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.llIngredientDetail.setBackground(activity.getResources().getDrawable(R.drawable.ingredient_selected_border));
            }
        }

        if(singleItem.isPacked()){
            holder.llIngredientDetail.setBackgroundColor(activity.getResources().getColor(R.color.grey));
        }

        holder.llIngredientDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(Constants.CURRENT_INGREDIENT, new Gson().toJson(singleItem));
                editor.commit();
                ingredientAdapter.updateList();
            }
        });

        holder.ingredientMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openIngredientSettingDialog();
            }
        });
    }


    void openIngredientSettingDialog() {
        ingredientSettingDialog = new Dialog(activity);
        ingredientSettingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ingredientSettingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ingredientSettingDialog.setContentView(R.layout.dialog_ingredient_setting);
        ingredientSettingDialog.show();
    }
    public void updateList() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != allIngredientModel.getIngredientModelArrayList() ? allIngredientModel.getIngredientModelArrayList().size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView ingredientSlipName,ingredientName,ingredientWeight,ingredientVolume,ingredientSection,ingredientProcess;
        protected LinearLayout llIngredientDetail;
        protected ImageView ingredientMoreInfo;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.ingredientSlipName = (TextView) itemView.findViewById(R.id.ingredient_slip_name);
            this.ingredientName = (TextView) itemView.findViewById(R.id.ingredient_name);
            this.ingredientWeight = (TextView) itemView.findViewById(R.id.ingredient_weight);
            this.ingredientVolume = (TextView) itemView.findViewById(R.id.ingredient_volume);
            this.ingredientSection = (TextView) itemView.findViewById(R.id.ingredient_section);
            this.ingredientProcess = (TextView) itemView.findViewById(R.id.ingredient_processing);
            this.llIngredientDetail = (LinearLayout) itemView.findViewById(R.id.ll_ingredient_detail);
            this.ingredientMoreInfo = (ImageView) itemView.findViewById(R.id.ingredient_more_info);

        }
    }
}
