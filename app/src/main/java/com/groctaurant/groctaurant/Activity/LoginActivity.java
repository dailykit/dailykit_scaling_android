package com.groctaurant.groctaurant.Activity;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Retrofit.APIInterface;
import com.groctaurant.groctaurant.Retrofit.Response.Ingredient;
import com.groctaurant.groctaurant.Retrofit.Response.IngredientDetail;
import com.groctaurant.groctaurant.Retrofit.Response.Order;
import com.groctaurant.groctaurant.Retrofit.Response.OrderResponse;
import com.groctaurant.groctaurant.Retrofit.RetrofitClient;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Room.Entity.OrderEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText loginName, loginPassword;
    Button loginButton;
    private ProgressDialog pDialog;
    private static final String LOG_TAG = "LoginActivity";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    ArrayList<IngredientDetailEntity> ingredientDetailEntityArrayList = new ArrayList<IngredientDetailEntity>();
    ArrayList<IngredientEntity> ingredientEntityArrayList = new ArrayList<>();
    ArrayList<ItemEntity> itemEntityArrayList = new ArrayList<>();
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;
    OrderEntity orderEntity;
    ItemEntity itemEntity;
    IngredientDetailEntity ingredientDetailEntity;
    IngredientEntity ingredientEntity;
    ArrayList<OrderEntity> orderEntityArrayList = new ArrayList<>();
    ArrayList<ArrayList<IngredientDetailEntity>> ingredientDetailMainArrayList = new ArrayList<>();
    ArrayList<ArrayList<IngredientEntity>> ingredientMainArrayList = new ArrayList<>();
    ArrayList<ArrayList<ItemEntity>> itemMainArrayList = new ArrayList<>();
    private APIInterface apiInterface;
    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginName = (TextInputEditText)findViewById(R.id.login_name);
        loginPassword = (TextInputEditText)findViewById(R.id.login_password);
        loginButton =(Button)findViewById(R.id.login_button);
        sharedpreferences = AppUtil.getAppPreferences(this);
        editor = sharedpreferences.edit();
        apiInterface = RetrofitClient.getClient().getApi();
        groctaurantDatabase = Room.databaseBuilder(getApplicationContext(), GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
        pDialog = new ProgressDialog(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser loginUser=new LoginUser();
                loginUser.execute();
                Intent homeIntent = new Intent(LoginActivity.this, OrderActivity.class);
                startActivity(homeIntent);
                LoginActivity.this.finish();
            }
        });

        if(sharedpreferences.getString(Constants.COUNTDOWN_TO_PRINT,"").equals("")){
            editor.putString(Constants.COUNTDOWN_TO_PRINT,String.valueOf(Constants.DEFAULT_COUNTDOWN_TO_PRINT));
            editor.commit();
        }

        if(sharedpreferences.getString(Constants.WEIGHT_ACCURACY,"").equals("")){
            editor.putString(Constants.WEIGHT_ACCURACY,String.valueOf(Constants.DEFAULT_WEIGHT_ACCURACY));
            editor.commit();
        }

        if(sharedpreferences.getBoolean(Constants.IS_USER_LOGGED,false)){
            Intent homeIntent = new Intent(LoginActivity.this, OrderActivity.class);
            startActivity(homeIntent);
            LoginActivity.this.finish();
        }
        else {


            apiInterface.getOrderListing().enqueue(
                    new Callback<OrderResponse>() {
                        @Override
                        public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                            if (response.isSuccessful() && response.code() < 300) {
                                int x = 0;
                                OrderResponse orderResponse = response.body();
                                orderEntityArrayList = new ArrayList<>();

                                for (int i = 0; i < orderResponse.getAllOrder().size(); i++) {
                                    itemEntityArrayList = new ArrayList<>();
                                    orderEntity = new OrderEntity();
                                    orderEntity.setOrderNumber(orderResponse.getAllOrder().get(i).getOrder().get(0).getOrderNumber());
                                    orderEntity.setOrderId(orderResponse.getAllOrder().get(i).getOrderId());
                                    itemEntityArrayList.clear();
                                    for (int j = 0; j < orderResponse.getAllOrder().get(i).getOrder().size(); j++) {
                                        ingredientEntityArrayList = new ArrayList<>();
                                        itemEntity = new ItemEntity();
                                        Order order = orderResponse.getAllOrder().get(i).getOrder().get(j);
                                        itemEntity.setItemOrderId(orderEntity.getOrderId() + "_" + String.valueOf(j + 1));
                                        itemEntity.setOrderId(orderEntity.getOrderId());
                                        itemEntity.setItemSku(order.getRecipeSKU());
                                        itemEntity.setItemName(order.getRecipeName());
                                        itemEntity.setItemServing(order.getRecipeServings());
                                        itemEntity.setItemQuantity(order.getRecipeQuantity());
                                        itemEntity.setItemStatus(order.getOrderStatus());
                                        itemEntity.setItemNumber(order.getOrderNumber());
                                        itemEntity.setItemNoOfIngredient(order.getNumberOfIngredient());
                                        itemEntity.setCustomerPhone(order.getCusPhone());
                                        itemEntity.setCustomerName(order.getCusName());
                                        itemEntity.setCustomerAddress(order.getCusAddress());
                                        itemEntity.setMerId(order.getMerId());
                                        itemEntity.setRecipyCuisine(order.getRecCuisine());
                                        itemEntity.setRecipyPrice(order.getRecPrice());
                                        itemEntity.setSubTotal(order.getSubTotal());
                                        itemEntity.setDiscountPercentage(order.getDiscountPercentage());
                                        itemEntity.setDiscount(order.getDiscount());
                                        itemEntity.setNewCustomerDiscount(order.getNewCusDiscount());
                                        itemEntity.setSgst(order.getSgst());
                                        itemEntity.setCgst(order.getCgst());
                                        itemEntity.setDeliveryCharges(order.getDelCharges());
                                        itemEntity.setTotalPrice(order.getTotalPrice());
                                        itemEntity.setGrcash(order.getGrcash());
                                        itemEntity.setWalletCash(order.getWalletcash());
                                        itemEntity.setFinalAmount(order.getFinalAmount());
                                        itemEntity.setPaymentType(order.getPaymentType());
                                        itemEntity.setPaymentStatus(order.getPaymentStatus());
                                        itemEntity.setAddNotes(order.getAddNotes());
                                        itemEntity.setDeliveryTime(order.getDelTime());
                                        itemEntity.setOrderAt(order.getOrderAt());
                                        itemEntity.setOrderCancelTill(order.getOrderCancelTill());
                                        itemEntity.setDeliveryExpected(order.getDeliveryExpected());
                                        itemEntity.setDispatchReal(order.getDispatchReal());
                                        itemEntity.setOrderCancelTill(order.getOrderCancelTill());
                                        itemEntity.setSelectedPosition(-1);

                                        ingredientEntityArrayList.clear();
                                        for (int k = 0; k < order.getIngredient().size(); k++) {
                                            ingredientDetailEntityArrayList = new ArrayList<>();
                                            ingredientEntity = new IngredientEntity();
                                            Ingredient ingredient = order.getIngredient().get(k);
                                            ingredientEntity.setIngredientId(String.valueOf(itemEntity.getItemOrderId() + "_" + (k + 1)));
                                            ingredientEntity.setIngredientIndex(ingredient.getIngredientIndex());
                                            ingredientEntity.setSlipName(ingredient.getSlipName());
                                            ingredientEntity.setPackedComplete(false);
                                            ingredientEntity.setSelectedIngredientPosition(0);
                                            ingredientEntity.setDeleted(false);
                                            ingredientEntity.setLabeled(false);
                                            ingredientEntity.setIngredientItemId(itemEntity.getItemOrderId());
                                            ingredientDetailEntityArrayList.clear();
                                            for (int l = 0; l < ingredient.getIngredientDetail().size(); l++) {
                                                ingredientDetailEntity = new IngredientDetailEntity();
                                                IngredientDetail ingredientDetail = ingredient.getIngredientDetail().get(l);
                                                ingredientDetailEntity.setIngredientDetailId(String.valueOf(orderResponse.getAllOrder().get(i).getOrderId() + "_" + order.getRecipeSKU() + "_" + (((k + 1)*10) + ((j + 1)*100) + (l + 1))));
                                                //ingredientDetailEntity.setIngredientId(String.valueOf(orderResponse.getAllOrder().get(i).getOrderId() + "_" + (k + 1) * (j + 1)));
                                                ingredientDetailEntity.setIngredientId(ingredientEntity.getIngredientId());
                                                ingredientDetailEntity.setIngredientName(ingredientDetail.getIngredientName());
                                                ingredientDetailEntity.setIngredientQuantity(Double.parseDouble(ingredientDetail.getIngredientQuantity()));
                                                ingredientDetailEntity.setIngredientMsr(ingredientDetail.getIngredientMeasure());
                                                ingredientDetailEntity.setIngredientSection(ingredientDetail.getIngredientSection());
                                                ingredientDetailEntity.setIngredientProcess(ingredientDetail.getIngredientProcess());
                                                ingredientDetailEntity.setIngredientDetailIndex(ingredient.getIngredientIndex());
                                                ingredientDetailEntity.setPacked(false);
                                                ingredientDetailEntity.setWeighed(false);
                                                ingredientDetailEntity.setDeleted(false);
                                                ingredientDetailEntity.setIngredientPackTimestamp("");
                                                ingredientDetailEntity.setIngredientDetailPosition(l);
                                                ingredientDetailEntityArrayList.add(ingredientDetailEntity);
                                                Log.e(TAG,"Test : "+ingredientDetailEntity.toString());
                                                groctaurantDatabase.ingredientDetailDao().insert(ingredientDetailEntity);

                                            }
                                            ingredientEntity.setIngredientEntity(ingredientDetailEntityArrayList);

                                            ingredientEntityArrayList.add(ingredientEntity);
                                            groctaurantDatabase.ingredientDao().insert(ingredientEntity);
                                        }
                                        ingredientMainArrayList.add(ingredientEntityArrayList);
                                        itemEntity.setItemIngredient(ingredientEntityArrayList);
                                        itemEntityArrayList.add(itemEntity);
                                        groctaurantDatabase.itemDao().insert(itemEntity);
                                    }
                                    orderEntity.setOrderItems(itemEntityArrayList);
                                    orderEntityArrayList.add(orderEntity);
                                    groctaurantDatabase.orderDao().insert(orderEntity);
                                }

                                for (int i = 0; i < orderEntityArrayList.size(); i++) {
                                    groctaurantDatabase.orderDao().insert(orderEntityArrayList.get(i));
                                }
                                IngredientDetailEntity ingredientDetailEntity = new IngredientDetailEntity();
                                ingredientDetailEntity.setIngredientName("No Ingredient Selected");
                                ingredientDetailEntity.setIngredientQuantity(0);
                                editor.putString(Constants.SELECTED_INGREDIENT_ENTITY, new Gson().toJson(ingredientDetailEntity));
                                editor.putString(Constants.SELECTED_ORDER_ID, "No Order Selected");
                                editor.putBoolean(Constants.IS_USER_LOGGED, true);
                                editor.commit();
                            } else if (response.code() == 301) {
                                Toast.makeText(LoginActivity.this, "No Order Assigned", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderResponse> call, Throwable t) {
                            Log.e(LOG_TAG, "Order Detail Fetching Failed" + t);
                        }
                    }
            );
        }




    }



    class LoginUser extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog.show();
//            pDialog.setMessage("Please Wait");
//            pDialog.setCancelable(true);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(Constants.URL_USER_LOGIN);
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("user_name","");
                postDataParams.put("password","");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
                writer.write(AppUtil.getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader( conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");

                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    Log.e(LOG_TAG,sb.toString());
                    return sb.toString();

                } else {
                    return new String("{\"status\" : \"fail\"}");
                }
            } catch (Exception ex) {
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
//            pDialog.dismiss();
            if(o!=null) {
                JSONObject jObject = null;
                try {
                    Log.e(LOG_TAG, "JSON: " + o.toString());
                    jObject = new JSONObject(o.toString());
                    if(jObject.getString("status").equals("success")) {
                        editor.putString(Constants.USER_NAME, loginName.getText().toString());
                        editor.putBoolean(Constants.IS_USER_LOGGED,true);
                        editor.commit();
                        Intent homeIntent = new Intent(LoginActivity.this, OrderActivity.class);
                        startActivity(homeIntent);
                        LoginActivity.this.finish();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Incorrect Username / Password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(LoginActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        }
    }



}
