package com.groctaurant.groctaurant.LegacyCode;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groctaurant.groctaurant.Model.AllIngredientModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by Danish Rafique on 31-07-2018.
 */
public class OldIngredientAdapter extends RecyclerView.Adapter<OldIngredientAdapter.SingleItemRowHolder>  {

    private ArrayList<AllIngredientModel> allIngredientModelArrayList;
    private Activity activity;
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String TAG ="OldIngredientAdapter";
    int currentPosition;
    OldIngredientInternalAdapter adapter;

    public OldIngredientAdapter(Activity activity, ArrayList<AllIngredientModel> allIngredientModelArrayList) {
        this.allIngredientModelArrayList = allIngredientModelArrayList;
        this.activity = activity;

    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ingredient_list, null);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(v);
        sharedpreferences = AppUtil.getAppPreferences(activity);
        editor = sharedpreferences.edit();
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        final AllIngredientModel singleItem = allIngredientModelArrayList.get(position);
        //Log.e(TAG,sharedpreferences.getInt(Constants.WIDTH_OF_INGREDIENT_LIST,1188)+" ");
        holder.ingredientInternalList.setMinimumWidth(sharedpreferences.getInt(Constants.WIDTH_OF_INGREDIENT_LIST,1188));
        final Handler handler = new Handler();
        handler.postDelayed (() -> {
            //Log.e(TAG,"PostDelayed Width of List :"+holder.ingredientInternalList.getWidth());
            holder.ingredientInternalList.setHasFixedSize(true);
            adapter = new OldIngredientInternalAdapter(activity,singleItem,position,this);
            holder.ingredientInternalList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            holder.ingredientInternalList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //Log.e(TAG,"2 PostDelayed Width of List :"+holder.ingredientInternalList.getWidth());
        }, 100);


    }
    public void updateList() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != allIngredientModelArrayList ? allIngredientModelArrayList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected RecyclerView ingredientInternalList;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.ingredientInternalList = (RecyclerView) itemView.findViewById(R.id.ingredient_internal_list);
        }
    }
}
