package com.groctaurant.groctaurant.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lf_usbprinter.Prt_USB;
import com.example.longfly_sdk_rf.Longfly_RF;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.groctaurant.groctaurant.Adapter.IngredientAdapter;
import com.groctaurant.groctaurant.Adapter.PlanningIngredientsAdapter;
import com.groctaurant.groctaurant.Adapter.PlanningPackingTabAdapter;
import com.groctaurant.groctaurant.Callback.IngredientListener;
import com.groctaurant.groctaurant.Callback.PlanningPackingCallback;
import com.groctaurant.groctaurant.CustomView.FuturaMediumTextView;
import com.groctaurant.groctaurant.Model.IngredientPlanningModel;
import com.groctaurant.groctaurant.Model.PlanningDetailModel;
import com.groctaurant.groctaurant.Model.PlanningModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Room.Entity.OrderEntity;
import com.groctaurant.groctaurant.Serial.SerialPort;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;
import com.groctaurant.groctaurant.ViewModel.IngredientViewModel;
import com.groctaurant.groctaurant.ViewModel.PlanningPackingViewModel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PlanningPackingActivity extends AppCompatActivity implements PlanningPackingCallback, IngredientListener {

    private static final String TAG = "PlanningPackingActivity";
    PlanningPackingViewModel planningPackingViewModel;
    RecyclerView planningTabRecyclerView,planningIngredientRecyclerView;
    PlanningPackingTabAdapter planningPackingTabAdapter;
    PlanningIngredientsAdapter planningIngredientsAdapter;
    FuturaMediumTextView ingredientName, ingredientTotalWeight;
    final String TTY_DEV = "/dev/ttymxc3";
    final int bps = 115200;
    protected OutputStream mOutputStream;
    IngredientAdapter ingredientAdapter;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    SerialPort mSerialPort = null;
    Auto_Weight weight_thread = new Auto_Weight();
    boolean auto_start = true;
    int iBiaoDingFlg = 0;
    private InputStream mInputStream;
    TextView ingredientSelected;
    double weightUpperLimit, weightLowerLimit;
    double accuracy;
    CountDownTimer countDownTimer;
    LinearLayout weightDisplayLayout;
    TextView weightStatus;
    TextView weightDifference;
    TextView weightScreenOrderId;
    TextView weightScreenItemName;
    ImageView tareButton;
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;
    EditText setWeight;
    Button sendWeight,printTest;
    TextView ingredientScreenWeight;
    LinearLayout inventoryTab, realTimeTab, settingTab;
    TextView orderName;
    Switch simulatorSwitch;
    Button simulator;
    IngredientViewModel ingredientViewModel;
    String orderId;
    Longfly_RF Longfly_RF_1=null;
    Prt_USB Longfly_UsbPrt=null;
    PlanningDetailModel planningDetailModel;
    boolean isAlreadyUpdated = false;
    RecyclerView.SmoothScroller smoothScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Log.e(TAG, planningPackingViewModel.getSelectedPlanningModel().toString());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.real_time_tab: {
                finish();
                break;
            }
            case R.id.inventory_tab: {
                Intent intentInventory = new Intent(PlanningPackingActivity.this, InventoryActivity.class);
                startActivity(intentInventory);
                finish();
                break;
            }
            case R.id.setting_tab: {
                Intent intent = new Intent(PlanningPackingActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.send_weight:{
                sendWeight();
                break;
            }
            case R.id.print_test:{
                Print_test_1( );
                break;
            }
            case  R.id.simulator_switch: {
                if(simulatorSwitch.isChecked()){
                    simulator.setVisibility(View.VISIBLE);
                    editor.putBoolean(Constants.IS_SIMULATED,true);
                    auto_start=false;
                }
                else{
                    simulator.setVisibility(View.GONE);
                    editor.putBoolean(Constants.IS_SIMULATED,false);
                    auto_start=true;
                }
                break;
            }
            case  R.id.simulator: {
                sendWeightBySimulator();
                break;
            }
            case R.id.tare_button:
                if(ingredientViewModel.isTareEnabled()){
                    ingredientViewModel.switchOffTare(this);
                }
                else{
                    Log.e(TAG,ingredientScreenWeight.getText().toString());
                    Float weightOnScreen=Float.parseFloat(ingredientScreenWeight.getText().toString());
                    ingredientViewModel.switchOnTare(this,weightOnScreen);
                }
                break;

        }
    }

    public void init() {
        setView();
        planningPackingViewModel = ViewModelProviders.of(this).get(PlanningPackingViewModel.class);
        sharedpreferences = AppUtil.getAppPreferences(this);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(getApplicationContext(), GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
        ingredientViewModel= ViewModelProviders.of(this).get(IngredientViewModel.class);
        Type type = new TypeToken<OrderEntity>() {
        }.getType();
        orderId = sharedpreferences.getString(Constants.SELECTED_ORDER_ID, "");
        PlanningModel planningModel=planningPackingViewModel.getSelectedPlanningModel();
        ingredientName.setText(planningModel.getIngredientName());
        if(planningModel.getIngredientTotalWeight()>=1000){
            ingredientTotalWeight.setText(planningModel.getIngredientTotalWeight()/1000+" kg");
        }
        else {
            ingredientTotalWeight.setText(planningModel.getIngredientTotalWeight()+" gm");
        }
        Longfly_UsbPrt = new Prt_USB(this, 0);
        Longfly_RF_1 = new Longfly_RF( this );
        smoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        //weight_thread.start();
        updateTabList();
    }

    public void setView(){
        setContentView(R.layout.activity_planning_packing);
        planningPackingViewModel = ViewModelProviders.of(this).get(PlanningPackingViewModel.class);
        planningTabRecyclerView = findViewById(R.id.planning_process_tab);
        planningIngredientRecyclerView=findViewById(R.id.planning_ingredient_list);
        ingredientName=findViewById(R.id.ingredient_name);
        ingredientTotalWeight=findViewById(R.id.ingredient_total_weight);
        weightScreenItemName=findViewById(R.id.weight_screen_item_name);
        weightScreenOrderId=findViewById(R.id.weight_screen_order_id);
        ingredientScreenWeight = (TextView) findViewById(R.id.ingredient_screen_weight);
        inventoryTab = (LinearLayout) findViewById(R.id.inventory_tab);
        realTimeTab = (LinearLayout) findViewById(R.id.real_time_tab);
        settingTab =(LinearLayout)findViewById(R.id.setting_tab);
        ingredientSelected = (TextView) findViewById(R.id.ingredient_selected);
        weightDisplayLayout = (LinearLayout) findViewById(R.id.weight_display_layout);
        weightStatus = (TextView) findViewById(R.id.weight_status);
        weightDifference = (TextView) findViewById(R.id.weight_difference);
        setWeight = (EditText) findViewById(R.id.set_weight);
        sendWeight = (Button) findViewById(R.id.send_weight);
        printTest = (Button) findViewById(R.id.print_test);
        tareButton=(ImageView) findViewById(R.id.tare_button);
        orderName = (TextView) findViewById(R.id.order_name);
        simulatorSwitch=(Switch)findViewById(R.id.simulator_switch);
        simulator=(Button)findViewById(R.id.simulator);
    }

    @Override
    public void updateTabList() {
        planningTabRecyclerView.setHasFixedSize(true);
        planningPackingTabAdapter = new PlanningPackingTabAdapter(this, planningPackingViewModel.getSelectedPlanningModel());
        planningTabRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        planningTabRecyclerView.setAdapter(planningPackingTabAdapter);
        planningPackingTabAdapter.notifyDataSetChanged();
    }

    public void setSelectedPlanningPosition(int position){
        planningPackingViewModel.setSelectedPlanningPosition(this,position);
    }

    @Override
    public void updatePackingList(IngredientPlanningModel ingredientPlanningModel) {
        if(ingredientPlanningModel.getIngredientPlanningSelectedPosition()==-2){
            Toast.makeText(this, "All Ingredient Packed", Toast.LENGTH_LONG).show();
        }
        Log.e(TAG,"Selected Position : "+ingredientPlanningModel.getIngredientPlanningSelectedPosition());
        planningIngredientRecyclerView.setHasFixedSize(true);
        planningIngredientsAdapter = new PlanningIngredientsAdapter(this,ingredientPlanningModel);
        planningIngredientRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        planningIngredientRecyclerView.setAdapter(planningIngredientsAdapter);
        planningIngredientsAdapter.notifyDataSetChanged();
        if(ingredientPlanningModel.getIngredientPlanningSelectedPosition()>0){
            smoothScroller.setTargetPosition(ingredientPlanningModel.getIngredientPlanningSelectedPosition());
            planningIngredientRecyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
        }
    }

    public void wanglei_loop(int search_interval) {
        try {
            Thread.sleep(search_interval);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String e_scale_Algorithm(String string, String string2, int i32BiaoDingValue, int i32FenduValue) {
        // TODO Auto-generated method stub
        double value = 0;
        int i32Value, i32Tmp;
        String p = "" + 0;
        try {
            double AD = Integer.parseInt(string, 16);
            double kg_AD = Integer.parseInt(string2, 16);

//			value=(float) AD*i32BiaoDingValue/kg_AD*1000;//* 1000;
            value = (float) AD * i32BiaoDingValue / kg_AD;//* 1000;
            BigDecimal d = new BigDecimal(value);
            value = d.setScale(4, RoundingMode.HALF_UP).doubleValue() * 10000;
            i32Value = (int) value;
            i32Value = i32Value % 100;
            i32Tmp = i32FenduValue * 10 / 2;

            if (i32Value > (i32FenduValue * 10)) {
                i32Value = i32Value - (i32FenduValue * 10);
            }
            value = value - i32Value;
            if (i32Value >= i32Tmp) {
                value = value + (i32FenduValue * 10);
            }
            i32Value = (int) value;
            value = (float) i32Value * 0.0001;
            DecimalFormat decimalFormat = new DecimalFormat(".000");
            p = decimalFormat.format(value);
        } catch (Exception e) {
            // TODO: handle exception
            DecimalFormat decimalFormat = new DecimalFormat(".0");
            p = decimalFormat.format(value);
        }

        return "" + p;
    }

    private String Add_0(String String) {
        // TODO Auto-generated method stub
        String myString = String;
        int my_num = Integer.parseInt(myString, 16);
        if (my_num < 16) {
            myString = "0" + String;
        }
        return myString;
    }

    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    @Override
    protected void onPause() {
        auto_start = false;
        super.onPause();
    }

    public void sendWeight() {
        Message msg1 = new Message();
        Bundle bundle1 = new Bundle();
        msg1.what = 0;
        bundle1.putString("get_weight", String.valueOf(0));
        msg1.setData(bundle1);
        //Log.e(TAG,"Handler Called 6");
        handler.sendMessage(msg1);

        String weightToSend = setWeight.getText().toString();
        if(weightToSend.equals("")){
            weightToSend="0";
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 0;
        bundle.putString("get_weight", String.valueOf(Double.parseDouble(weightToSend) / 1000));
        msg.setData(bundle);
        //Log.e(TAG,"Handler Called 3");
        handler.sendMessage(msg);
    }

    public void sendWeightBySimulator() {
        //Log.e(TAG," sendWeightBySimulator Called");
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 0;
        bundle.putString("get_weight", String.valueOf(0));
        msg.setData(bundle);
        //Log.e(TAG,"Handler Called 4");
        handler.sendMessage(msg);

        Message msg1 = new Message();
        Bundle bundle1 = new Bundle();
        msg1.what = 0;
        PlanningDetailModel planningDetailModel=planningPackingViewModel.getSelectedPlanningDetail();
        bundle1.putString("get_weight", String.valueOf(planningDetailModel.getIngredientWeight()/1000));
        msg1.setData(bundle1);
        //Log.e(TAG,"Handler Called 5");
        handler.sendMessage(msg1);

    }

    @Override
    protected void onResume() {
        //auto_start = true;
        super.onResume();
        //Log.e(TAG,"ChangeIngredientList Called 6");
        //orderName.setText(ingredientViewModel.getSelectedOrderNumber());
        if(planningDetailModel!=null) {
            weightScreenItemName.setText(planningDetailModel.getItemName());
            weightScreenOrderId.setText(planningDetailModel.getIngredientId().substring(0,planningDetailModel.getIngredientId().indexOf("_")));
           /* ItemEntity currentItemEntity = ingredientViewModel.getCurrentItemEntity();
            if(planningDetailModel.getSelectedPosition()==-2){
                Toast.makeText(this, "All Ingredient Packed for this Item", Toast.LENGTH_LONG).show();
            }*/
        }
        if(ingredientViewModel.isSimulated()){
            //Log.e(TAG,"is Simulated");
            simulator.setVisibility(View.VISIBLE);
        }
        else{
            //Log.e(TAG,"Is Not Simulated");
            simulator.setVisibility(View.GONE);
        }

        if(ingredientViewModel.isSimulated() || ingredientViewModel.isManualSetWeightEnabled()){
            auto_start=false;
        }
        else{
            auto_start=true;
        }


        if(ingredientViewModel.isManualSetWeightEnabled()){
            setWeight.setVisibility(View.VISIBLE);
            sendWeight.setVisibility(View.VISIBLE);
        }
        else{
            setWeight.setVisibility(View.GONE);
            sendWeight.setVisibility(View.GONE);
        }

        if(ingredientViewModel.isPrinterTestEnabled()){
            printTest.setVisibility(View.VISIBLE);
        }
        else{
            printTest.setVisibility(View.GONE);
        }


    }

    @Override
    public void setScreenDetail(PlanningDetailModel planningDetailModel){
        ingredientSelected.setText(planningDetailModel.getSlipName() + " - " + planningDetailModel.getIngredientWeight() + " gm");
        weightScreenItemName.setText(planningDetailModel.getItemName());
        weightScreenOrderId.setText(planningDetailModel.getIngredientId().substring(0,planningDetailModel.getIngredientId().indexOf("_")));
    }


    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    if(ingredientViewModel.isTareEnabled()) {
                        ingredientScreenWeight.setText(ingredientViewModel.getIngredientWeightForTare(msg.getData().getString("get_weight")));
                    }
                    else {
                        ingredientScreenWeight.setText(String.valueOf(Double.parseDouble(msg.getData().getString("get_weight")) * 1000));
                    }
                    Type type = new TypeToken<PlanningDetailModel>() {}.getType();
                    planningDetailModel = new Gson().fromJson(sharedpreferences.getString(Constants.SELECTED_PLANNING_INGREDIENT_MODEL, ""), type);
                    accuracy = Float.parseFloat(sharedpreferences.getString(Constants.WEIGHT_ACCURACY,"0.0"));
                    if(planningDetailModel!=null){
                        weightLowerLimit = planningDetailModel.getIngredientWeight() - planningDetailModel.getIngredientWeight() * accuracy / 100;
                        weightUpperLimit = planningDetailModel.getIngredientWeight() + planningDetailModel.getIngredientWeight() * accuracy / 100;
                    }

                    if (planningDetailModel == null) {
                        weightDisplayLayout.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                    } else if (Double.parseDouble(ingredientScreenWeight.getText().toString()) > weightUpperLimit) {
                        weightDisplayLayout.setBackgroundColor(getResources().getColor(R.color.cardview_red));

                        weightDifference.setText("Reduce " + (Double.parseDouble(ingredientScreenWeight.getText().toString()) - planningDetailModel.getIngredientWeight()) + " gm");

                        weightStatus.setText("Status : High");

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                    } else if (Double.parseDouble(ingredientScreenWeight.getText().toString()) <= weightUpperLimit && Double.parseDouble(ingredientScreenWeight.getText().toString()) >= weightLowerLimit) {
                        weightDisplayLayout.setBackgroundColor(getResources().getColor(R.color.cardview_green));
                        weightDifference.setText("Matched");

                        if (countDownTimer == null) {


                            countDownTimer = new CountDownTimer(Integer.parseInt(sharedpreferences.getString(Constants.COUNTDOWN_TO_PRINT,"2"))*1000, 1000) {

                                public void onTick(long millisUntilFinished) {

                                    weightStatus.setText("Status : Printing the Label in " + String.valueOf((millisUntilFinished / 1000) + 1) + " sec");
                                }

                                public void onFinish() {
                                    weightStatus.setText("Status : Printing the Label ");
                                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                                    groctaurantDatabase.ingredientDetailDao().updateTimestamp(planningDetailModel.getIngredientDetailId(), timeStamp);
                                    groctaurantDatabase.ingredientDetailDao().isPackedUpdate(planningDetailModel.getIngredientDetailId(), true);
                                    groctaurantDatabase.ingredientDetailDao().setMeasuredWeight(planningDetailModel.getIngredientDetailId(), Double.parseDouble(ingredientScreenWeight.getText().toString()));
                                    groctaurantDatabase.planningDetailDao().isPackedUpdate(planningDetailModel.getIngredientDetailId(), true);

                                    int countIngredientDetail = groctaurantDatabase.ingredientDetailDao().countIngredientDetailPerIngredientId(planningDetailModel.getIngredientId());
                                    isAlreadyUpdated = false;
                                    Log.e(TAG,"Measured Weight :"+ingredientScreenWeight.getText().toString());
                                    Log.e(TAG,"Number of Ingredient :"+countIngredientDetail);
                                    if (countIngredientDetail == 1) {
                                        Log.e(TAG,"Packing Completed");
                                        Log.e(TAG,"Ingredient Packed : "+planningDetailModel.toString());
                                        groctaurantDatabase.ingredientDao().isPackedCompleteUpdate(planningDetailModel.getIngredientId(), true);
                                        groctaurantDatabase.ingredientDao().setMeasuredTotalWeight(planningDetailModel.getIngredientId(), Double.parseDouble(ingredientScreenWeight.getText().toString()));
                                        Log.e(TAG,"Detail after Update :"+ingredientViewModel.getIngredientById(planningDetailModel.getIngredientId()));
                                        planningPackingViewModel.setNextSelectIndex(PlanningPackingActivity.this);
                                        ingredientViewModel.switchOffTare(PlanningPackingActivity.this);
                                        printLabel(planningDetailModel,timeStamp,ingredientScreenWeight.getText().toString());
                                    }
                                    weightDifference.setText("Start weighing");
                                    //changeIngredientList();
                                }

                            }.start();
                        }
                    } else if (Double.parseDouble(ingredientScreenWeight.getText().toString()) < weightLowerLimit) {

                        weightDisplayLayout.setBackgroundColor(getResources().getColor(R.color.cardview_yellow));
                        weightDifference.setText("Add " + (planningDetailModel.getIngredientWeight() - Double.parseDouble(ingredientScreenWeight.getText().toString())) + " gm");


                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        weightStatus.setText("Status : Low");
                    }





                    /*

                                public void onFinish() {
                                    weightStatus.setText("Status : Printing the Label ");
                                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                                    groctaurantDatabase.ingredientDetailDao().updateTimestamp(ingredientDetailEntity.getIngredientDetailId(), timeStamp);
                                    groctaurantDatabase.ingredientDetailDao().isPackedUpdate(ingredientDetailEntity.getIngredientDetailId(), true);
                                    groctaurantDatabase.ingredientDetailDao().setMeasuredWeight(ingredientDetailEntity.getIngredientDetailId(), Double.parseDouble(ingredientScreenWeight.getText().toString()));

                                    int countIngredientDetail = groctaurantDatabase.ingredientDetailDao().countIngredientDetailPerIngredientId(ingredientDetailEntity.getIngredientId());
                                    int selectPosition;
                                    isAlreadyUpdated = false;
                                    Log.e(TAG,"Measured Weight :"+ingredientScreenWeight.getText().toString());
                                    if (countIngredientDetail == 1) {
                                        Log.e(TAG,"Packing Completed");
                                        Log.e(TAG,"Ingredient Packed : "+ingredientDetailEntity.toString());
                                        groctaurantDatabase.ingredientDao().isPackedCompleteUpdate(ingredientDetailEntity.getIngredientId(), true);
                                        groctaurantDatabase.ingredientDao().setMeasuredTotalWeight(ingredientDetailEntity.getIngredientId(), Double.parseDouble(ingredientScreenWeight.getText().toString()));
                                        Log.e(TAG,"Detail after Update :"+ingredientViewModel.getIngredientById(ingredientDetailEntity.getIngredientId()));
                                        selectPosition = itemEntity.getSelectedPosition() + 1;
                                        ingredientViewModel.switchOffTare(IngredientActivity.this);
                                        printLabel(ingredientDetailEntity,itemEntity,timeStamp,ingredientScreenWeight.getText().toString());
                                    }
                                    else
                                    {
                                        groctaurantDatabase.ingredientDetailDao().isPackedUpdate(ingredientDetailEntity.getIngredientDetailId(), true);
                                        int nextIngredientPosition = ingredientViewModel.checkAllProductPacked(ingredientDetailEntity);
                                        Log.e(TAG,"Next Ingredient ID: "+nextIngredientPosition);
                                        if(nextIngredientPosition==-1){
                                            groctaurantDatabase.ingredientDao().setMeasuredTotalWeight(ingredientDetailEntity.getIngredientId(),ingredientViewModel.getTotalWeightForTare(ingredientScreenWeight.getText().toString()) );
                                            printLabel(ingredientDetailEntity,itemEntity,timeStamp,ingredientScreenWeight.getText().toString());
                                            groctaurantDatabase.ingredientDao().isPackedCompleteUpdate(ingredientDetailEntity.getIngredientId(), true);
                                            selectPosition = itemEntity.getSelectedPosition() + 1;
                                            ingredientViewModel.switchOffTare(IngredientActivity.this);
                                        }
                                        else{
                                            ingredientViewModel.setTareWeight(ingredientScreenWeight.getText().toString());
                                            selectPosition = itemEntity.getSelectedPosition();
                                            ingredientViewModel.setIngredientByPositionAndDetailId(ingredientDetailEntity,nextIngredientPosition);
                                            isAlreadyUpdated =true;
                                        }
                                    }
                                    updateLowerTabList();
                                    if(Integer.parseInt(itemEntity.getItemNoOfIngredient())<=selectPosition){
                                        Log.e(TAG,selectPosition+" Code Ran");
                                        selectPosition=0;
                                    }
                                    if(!isAlreadyUpdated) {
                                        groctaurantDatabase.itemDao().setSelectedItem(itemEntity.getItemOrderId(), selectPosition);
                                        itemEntity.setSelectedPosition(selectPosition);
                                        ingredientViewModel.setIngredientDetailByItemEntity(itemEntity);
                                    }
                                    itemEntity=ingredientViewModel.getCurrentItemEntity();
                                    if(itemEntity.getSelectedPosition()==-2){
                                        ingredientSelected.setText("All Ingredient Packed Successfully for this Item");
                                    }
                                    else {
                                        IngredientDetailEntity selectedIngredientDetailEntity = ingredientViewModel.getSelectedIngredientDetailEntity();
                                        ingredientSelected.setText(selectedIngredientDetailEntity.getIngredientName() + " - " + selectedIngredientDetailEntity.getIngredientQuantity() + " gm");
                                    }
                                    weightDifference.setText("Start weighing");
                                    changeIngredientList();
                                }

                            }.start();
                        }
                    }
*/
                }
                break;
                case 1: {
                    Toast.makeText(PlanningPackingActivity.this, msg.getData().getString("get_state"), Toast.LENGTH_SHORT).show();
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Longfly_UsbPrt.Stop( );
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void changeTareDisplay() {
        if(ingredientViewModel.isTareEnabled()){
            tareButton.setBackground(getResources().getDrawable(R.drawable.round_green));
        }
        else{
            tareButton.setBackground(getResources().getDrawable(R.drawable.round_white));
        }
    }

    public void callSwitchToTare(){
        ingredientViewModel.switchOnTare(this,0);
    }


    class Auto_Weight extends Thread {
        @Override
        public void run() {
            try {
                mSerialPort = new SerialPort(new File(TTY_DEV), bps, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                //Log.e(TAG, "SecurityException " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                //Log.e(TAG, "IOException " + e.toString());
                e.printStackTrace();
            }
            int buf_num = 0, i32BiaoDing = 0, i32Fendu = 0, iNum = 0, iStateFlg = 0;
            byte[] receive_buf = new byte[20];
            boolean receive_state = false;

            while (true) {
                if (auto_start) {

                    byte[] buf = new byte[1];
                    int size;
                    try {
                        size = mInputStream.read(buf);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    if (receive_state || buf[0] == -86) {
                        receive_buf[buf_num] = (byte) (0xff & buf[0]);
                        buf_num++;
                        receive_state = true;
                        if (buf_num == 19) {
                            if (iBiaoDingFlg > 0) {
                                iStateFlg = 0;
                                if ((iBiaoDingFlg == 1 && receive_buf[1] == 104) || (iBiaoDingFlg == 2 && receive_buf[1] == 102) ||
                                        (iBiaoDingFlg == 3 && receive_buf[1] == -120)) {
                                    iStateFlg = 1;
                                }
                                if (iStateFlg == 0) {
                                    iNum++;
                                    if (iNum > 10) {
                                        iStateFlg = 2;
                                    }
                                }
                                if (iStateFlg > 0) {
                                    Message msg = new Message();


                                    Bundle bundle = new Bundle();
                                    msg.what = 1;
                                    if (iStateFlg == 1) {
                                        bundle.putString("get_state", "Success");
                                    } else {
                                        bundle.putString("get_state", "Fail");
                                    }

                                    msg.setData(bundle);

                                    Log.e(TAG,"Handler Called 1");

                                    handler.sendMessage(msg);

                                    iBiaoDingFlg = 0;
                                    iNum = 0;
                                }
                            } else if (receive_buf[1] == 85) {
                                String receice_string = byte2HexStr(receive_buf);
                                ;

                                String my_num_string = null;

                                i32BiaoDing = receive_buf[11] & 0xff;
                                i32BiaoDing = i32BiaoDing << 8;
                                i32BiaoDing = i32BiaoDing + (receive_buf[10] & 0xff);

                                i32Fendu = receive_buf[17] & 0xff;
                                i32Fendu = i32Fendu << 8;
                                i32Fendu = i32Fendu + (receive_buf[16] & 0xff);

                                String get_weight = e_scale_Algorithm(
                                        Add_0(Integer.toHexString(receive_buf[9] & 0xFF)) +
                                                Add_0(Integer.toHexString(receive_buf[8] & 0xFF)) +
                                                Add_0(Integer.toHexString(receive_buf[7] & 0xFF)) +
                                                Add_0(Integer.toHexString(receive_buf[6] & 0xFF)),

                                        Add_0(Integer.toHexString(receive_buf[5] & 0xFF)) +
                                                Add_0(Integer.toHexString(receive_buf[4] & 0xFF)) +
                                                Add_0(Integer.toHexString(receive_buf[3] & 0xFF)) +
                                                Add_0(Integer.toHexString(receive_buf[2] & 0xFF)),
                                        i32BiaoDing, i32Fendu);
                                Message msg = new Message();


                                Bundle bundle = new Bundle();
                                msg.what = 0;
                                bundle.putString("get_weight", get_weight);
                                // Log.e(TAG, "Weight : " + get_weight);
                                msg.setData(bundle);

                                //Log.e(TAG,"Handler Called 2");

                                handler.sendMessage(msg);
                            } else {
                                buf_num = 0;
                                receive_state = false;
                            }

                            buf_num = 0;
                            receive_state = false;
                        } else {

                        }
                    } else {

                    }
                } else {
                    buf_num = 0;
                    receive_state = false;
                    wanglei_loop(100);
                }
            }
        }

        public void stopThread() {
            interrupt();
        }
    }

    private	void Print_test_1( )
    {
        int i=0;
        int ipaper = 80;

        byte[] ba_SelfTest = {(byte) 0x16, 0x01, };
        byte[] ba_Prt = {(byte) 0x0a, };
        String line1_32 ="             ½áÕËµ¥             ";
        String line2_32 ="--------------------------------";
        String line3_32 ="NO.00001";
        String line4_32 ="Ê±¼ä: 2017-04-01 09:53:11";
        String line5_32 ="×ÀºÅ:01             ²Ù×÷Ô±: TT01";
        String line6_32 ="²ËÆ·            µ¥¼Û ÊýÁ¿   ½ð¶î";
        String line7_32 ="--------------------------------";
        String line8_32 ="¹ûÖ­             5.0    1   5.00";
        String line9_32 ="¹ûÖ­             5.0    1   5.00";
        String line10_32="--------------------------------";
        String line11_32="Ïû·Ñ:  10.00      ÓÅ»Ý:     0.00";
        String line12_32="ÔùËÍ:  10.00      Ä¨Áã:     0.00";
        String line13_32="Ãâµ¥:  10.00      ÕÒÁã:     0.00";
        String line14_32="Ó¦ÊÕ:  10.00      ¸¶¿î:     0.00";
        String line15_32="------------Ö§¸¶·½Ê½------------";
        String line16_32="ÈËÃñ±Ò: 10.00";

        String line1_48 ="                     ½áÕËµ¥                     ";
        String line2_48 ="------------------------------------------------";
        String line3_48 ="NO.00001";
        String line4_48 ="Ê±¼ä: 2017-04-01 09:53:11";
        String line5_48 ="×ÀºÅ:01             ²Ù×÷Ô±: TT01";
        String line6_48 ="²ËÆ·                    µ¥¼Û       ÊýÁ¿     ½ð¶î";
        String line7_48 ="------------------------------------------------";
        String line8_48 ="¹ûÖ­                     5.0          1     5.00";
        String line9_48 ="¹ûÖ­                     5.0          1     5.00";
        String line10_48="------------------------------------------------";
        String line11_48="Ïû·Ñ:   10.00           ÓÅ»Ý:     0.00";
        String line12_48="ÔùËÍ:   10.00           Ä¨Áã:     0.00";
        String line13_48="Ãâµ¥:   10.00           ÕÒÁã:     0.00";
        String line14_48="Ó¦ÊÕ:   10.00           ¸¶¿î:     0.00";
        String line15_48="--------------------Ö§¸¶·½Ê½--------------------";
        String line16_48="ÈËÃñ±Ò: 10.00";
        if( ipaper==80 )
        {
            for( i=0; i<1; i++ )
            {
                Longfly_UsbPrt.PrintInitSet_LF();
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line1_48 ,"GBK");
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line2_48 ,"GBK");
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line3_48,"GBK");
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line4_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line5_48,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line6_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line7_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line10_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line11_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line12_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line13_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line14_48 ,"GBK");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line15_48 ,"GBK");
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line16_48 ,"GBK");
                Longfly_UsbPrt.FeedPaper_LF((byte)150);
            }
        }

        if( ipaper==80 )
        {
            for( i=0; i<1; i++ )
            {
                Longfly_UsbPrt.PrintInitSet_LF();
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line1_48 ,"UTF-8");
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line2_48 ,"UTF-8");
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line3_48,"UTF-8");
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line4_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line5_48,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line6_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line7_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line10_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line11_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line12_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line13_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line14_48 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line15_48 ,"UTF-8");
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line16_48 ,"UTF-8");
                Longfly_UsbPrt.FeedPaper_LF((byte)150);
            }
        }

    }

    private void printLabel(PlanningDetailModel planningDetailModel, String timeStamp, String measuredWeight){
        IngredientEntity ingredientEntity=groctaurantDatabase.ingredientDao().getIngredientById(planningDetailModel.getIngredientId());
        IngredientDetailEntity ingredientDetailEntity=groctaurantDatabase.ingredientDetailDao().getIngredientByIngredientDetailId(planningDetailModel.getIngredientDetailId());
        Log.e(TAG,"printLabel Called "+measuredWeight);

        if(planningDetailModel!=null && ingredientEntity!=null && ingredientDetailEntity!=null) {
            String line1 = "            DailyKit            ";
            String line2 = "--------------------------------";
            String line3 = "Order Id. : " + planningDetailModel.getIngredientId().substring(0,planningDetailModel.getIngredientId().indexOf("_"));
            String line4 = "--------------------------------";
            String line5 = "Date : " + timeStamp.substring(0, 10);
            String line6 = "Time : " + timeStamp.substring(11);
            String line7 = "";
            String line8 = "Item Number : " + ingredientDetailEntity.getIngredientDetailIndex();
            String line9 = "";
            String line10 = "Ingredient Name : " + planningDetailModel.getSlipName();
            Log.e(TAG, "Measured Weight : " + ingredientEntity.getIngredientMeasuredTotalWeight());
            String line15 = "Measured Weight : " + ingredientEntity.getIngredientMeasuredTotalWeight();
            String line12 = "Processing : " + ingredientDetailEntity.getIngredientProcess();
            String line13 = "";
            String line14 = "--------------------------------";

            Longfly_UsbPrt.PrintInitSet_LF();
            Longfly_UsbPrt.SetDHFontCmd_LF();
            Longfly_UsbPrt.PrintTicket_Decode_LF(line1, "UTF-8");
            Longfly_UsbPrt.SetDefFontCmd_LF();
            Longfly_UsbPrt.PrintTicket_Decode_LF(line2, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line3, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line4, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line5, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line6, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line7, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line8, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line9, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line10, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line15, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line12, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line13, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line14, "UTF-8");
            Bitmap bitmap = null;
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.encodeBitmap(ingredientDetailEntity.getIngredientId(), BarcodeFormat.QR_CODE, 300, 100);
            } catch (Exception e) {
                Log.e(TAG, "BarCode Exception :" + e);
            }
            Longfly_UsbPrt.Prt_BitMap_LF(bitmap, 80);
            Longfly_UsbPrt.FeedPaper_LF((byte) 150);
            Longfly_UsbPrt.CutPaper_LF();
        }
    }


}
