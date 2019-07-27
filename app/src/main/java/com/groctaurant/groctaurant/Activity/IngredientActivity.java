package com.groctaurant.groctaurant.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.groctaurant.groctaurant.Adapter.IngredientLowerTabAdapter;
import com.groctaurant.groctaurant.Adapter.TabIngredientAdapter;
import com.groctaurant.groctaurant.Callback.IngredientListener;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Room.Entity.OrderEntity;
import com.groctaurant.groctaurant.Room.Entity.TabEntity;
import com.groctaurant.groctaurant.Serial.SerialPort;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;
import com.groctaurant.groctaurant.ViewModel.IngredientViewModel;
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
import java.util.Date;
import java.util.List;

public class IngredientActivity extends AppCompatActivity implements IngredientListener {

    private static final String TAG = "IngredientActivity";
    final String TTY_DEV = "/dev/ttymxc3";
    final int bps = 115200;
    protected OutputStream mOutputStream;
    Bundle bundle;
    RecyclerView ingredientTabList, ingredientDetailList;
    TextView ingredientScreenWeight;
    IngredientLowerTabAdapter ingredientTabAdapter;
    IngredientAdapter ingredientAdapter;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    SerialPort mSerialPort = null;
    Auto_Weight weight_thread = new Auto_Weight();
    boolean auto_start = true;
    int iBiaoDingFlg = 0;
    private InputStream mInputStream;
    ImageView inventoryTab, planningTab,setting;
    TextView ingredientSelected;
    IngredientDetailEntity ingredientDetailEntity;
    double weightUpperLimit, weightLowerLimit;
    double accuracy;
    CountDownTimer countDownTimer;
    ItemEntity itemEntity;
    LinearLayout weightDisplayLayout;
    TextView weightStatus;
    TextView weightDifference;
    TextView weightScreenOrderId;
    TextView weightScreenItemName;
    ImageView tareButton;
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;
    RecyclerView tabList;
    TabIngredientAdapter tabIngredientAdapter;
    List<TabEntity> tabEntityArrayList;
    TextView allOrderTab;
    EditText setWeight;
    Button sendWeight,printTest;
    String orderId;
    List<ItemEntity> itemEntityList;
    List<IngredientEntity> ingredientEntityList;
    Longfly_RF Longfly_RF_1=null;
    Prt_USB Longfly_UsbPrt=null;
    IngredientViewModel ingredientViewModel;
    boolean isAlreadyUpdated;
    TextView orderName;
    Switch simulatorSwitch;
    Button simulator;
    ImageView qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        setView();
        sharedpreferences = AppUtil.getAppPreferences(this);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(getApplicationContext(), GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
        ingredientViewModel= ViewModelProviders.of(this).get(IngredientViewModel.class);
        bundle = getIntent().getBundleExtra(Constants.INTENT_CURRENT_ORDER_DETAIL);
        Type type = new TypeToken<OrderEntity>() {
        }.getType();
        orderId = sharedpreferences.getString(Constants.SELECTED_ORDER_ID, "");
        Log.e(TAG, "Order Id : " + orderId);

        ingredientTabList.setHasFixedSize(true);
        ingredientDetailList.setHasFixedSize(true);
        itemEntityList = groctaurantDatabase.itemDao().loadItemsByOrderId(orderId);
        ingredientTabAdapter = new IngredientLowerTabAdapter(this, itemEntityList);
        ingredientTabList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        ingredientTabList.setAdapter(ingredientTabAdapter);
        ingredientTabAdapter.notifyDataSetChanged();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            editor.putInt(Constants.WIDTH_OF_INGREDIENT_LIST, ingredientDetailList.getWidth());
            editor.commit();
        }, 100);
        inventoryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentInventory = new Intent(IngredientActivity.this, InventoryActivity.class);
                startActivity(intentInventory);
                finish();
            }
        });

        planningTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPlanning = new Intent(IngredientActivity.this, PlanningActivity.class);
                startActivity(intentPlanning);
                finish();
            }
        });

        observeTab();

        allOrderTab.setText("All Order - " + groctaurantDatabase.orderDao().countOrder());

        allOrderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendWeight();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        printTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Print_test_1( );
            }
        });

        Longfly_UsbPrt = new Prt_USB(this, 0);
        Longfly_RF_1 = new Longfly_RF( this );

        //weight_thread.start();
    }

    public void setView(){
        weightScreenItemName=findViewById(R.id.weight_screen_item_name);
        weightScreenOrderId=findViewById(R.id.weight_screen_order_id);
        ingredientTabList = (RecyclerView) findViewById(R.id.list_ingredient_tab);
        ingredientScreenWeight = (TextView) findViewById(R.id.ingredient_screen_weight);
        tabList = (RecyclerView) findViewById(R.id.tab_list);
        inventoryTab = (ImageView) findViewById(R.id.inventory_tab);
        planningTab = (ImageView) findViewById(R.id.planning_tab);
        setting=(ImageView)findViewById(R.id.setting);
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
        ingredientDetailList = (RecyclerView) findViewById(R.id.ingredient_detail_list);
        allOrderTab = (TextView) findViewById(R.id.all_order_tab);
        qr=(ImageView)findViewById(R.id.qr);
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    if(ingredientViewModel.isTareEnabled()) {
                        //Log.e(TAG,"Tare Enabled :  Screen");
                        ingredientScreenWeight.setText(ingredientViewModel.getIngredientWeightForTare(msg.getData().getString("get_weight")));
                    }
                    else {
                        //Log.e(TAG,"Tare Disabled :  Screen");
                        ingredientScreenWeight.setText(String.valueOf(Double.parseDouble(msg.getData().getString("get_weight")) * 1000));
                    }
                    Type type = new TypeToken<IngredientDetailEntity>() {}.getType();
                    ingredientDetailEntity = new Gson().fromJson(sharedpreferences.getString(Constants.SELECTED_INGREDIENT_ENTITY, ""), type);

                    //Log.e(TAG, "Selected Ingredient in Handler : " + ingredientDetailEntity.toString());

                    ingredientSelected.setText(ingredientDetailEntity.getIngredientName() + " - " + ingredientDetailEntity.getIngredientQuantity() + " gm");
                    accuracy = Float.parseFloat(sharedpreferences.getString(Constants.WEIGHT_ACCURACY,"0.0"));
                    if (ingredientDetailEntity != null) {

                        weightLowerLimit = ingredientDetailEntity.getIngredientQuantity() - ingredientDetailEntity.getIngredientQuantity() * accuracy / 100;
                        weightUpperLimit = ingredientDetailEntity.getIngredientQuantity() + ingredientDetailEntity.getIngredientQuantity() * accuracy / 100;

                    }

                    if (ingredientDetailEntity == null) {
                        weightDisplayLayout.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                    } else if (Double.parseDouble(ingredientScreenWeight.getText().toString()) > weightUpperLimit) {
                        weightDisplayLayout.setBackgroundColor(getResources().getColor(R.color.cardview_red));

                        weightDifference.setText("Reduce " + (Double.parseDouble(ingredientScreenWeight.getText().toString()) - ingredientDetailEntity.getIngredientQuantity()) + " gm");

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
                    } else if (Double.parseDouble(ingredientScreenWeight.getText().toString()) < weightLowerLimit) {

                        weightDisplayLayout.setBackgroundColor(getResources().getColor(R.color.cardview_yellow));
                        weightDifference.setText("Add " + (ingredientDetailEntity.getIngredientQuantity() - Double.parseDouble(ingredientScreenWeight.getText().toString())) + " gm");


                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        weightStatus.setText("Status : Low");
                    }

                }
                break;
                case 1: {
                    Toast.makeText(IngredientActivity.this, msg.getData().getString("get_state"), Toast.LENGTH_SHORT).show();
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    void updateLowerTabList() {
        orderId = sharedpreferences.getString(Constants.SELECTED_ORDER_ID, "");
        itemEntityList = groctaurantDatabase.itemDao().loadItemsByOrderId(orderId);
        ingredientTabAdapter = new IngredientLowerTabAdapter(this, itemEntityList);
        ingredientTabList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        ingredientTabList.setAdapter(ingredientTabAdapter);
        ingredientTabAdapter.notifyDataSetChanged();
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
        IngredientDetailEntity currentIngredientDetailEntity = ingredientViewModel.getSelectedIngredientDetailEntity();
        bundle1.putString("get_weight", String.valueOf(currentIngredientDetailEntity.getIngredientQuantity()/1000));
        msg1.setData(bundle1);
        //Log.e(TAG,"Handler Called 5");
        handler.sendMessage(msg1);

    }


    public void observeTab() {
        tabEntityArrayList = groctaurantDatabase.tabDao().load();
        //Log.e(TAG, "Size of Tab List :" + tabEntityArrayList.size());
        tabList.setHasFixedSize(true);
        tabIngredientAdapter = new TabIngredientAdapter(this, tabEntityArrayList);
        tabList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tabList.setAdapter(tabIngredientAdapter);
        tabIngredientAdapter.notifyDataSetChanged();
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

    @Override
    protected void onResume() {
        //auto_start = true;
        super.onResume();
        //Log.e(TAG,"ChangeIngredientList Called 6");
        changeIngredientList();
        orderName.setText(ingredientViewModel.getSelectedOrderNumber());
        weightScreenItemName.setText(ingredientViewModel.getCurrentItemEntity().getItemName());
        weightScreenOrderId.setText(ingredientViewModel.getSelectedOrderId());
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

        ItemEntity currentItemEntity = ingredientViewModel.getCurrentItemEntity();
        if(currentItemEntity.getSelectedPosition()==-2){
            Toast.makeText(this, "All Ingredient Packed for this Item", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Longfly_UsbPrt.Stop( );
    }

    public void onClick(View view) {
        switch (view.getId()) {
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

    public void updateWeightScreen(){
        weightScreenItemName.setText(ingredientViewModel.getCurrentItemEntity().getItemName());
    }

    public void changeIngredientList() {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        ingredientEntityList = groctaurantDatabase.ingredientDao().loadIngredientByItemId(sharedpreferences.getString(Constants.SELECTED_ITEM_ID, ""));
        itemEntity = ingredientViewModel.getCurrentItemEntity();
        if(itemEntity.getSelectedPosition()==-2){
            Toast.makeText(this, "All Ingredient Packed for this Item", Toast.LENGTH_LONG).show();
        }
        editor.putString(Constants.SELECTED_ITEM_ENTITY, new Gson().toJson(itemEntity));
        editor.commit();

        Log.e(TAG, "Selected Item : " + itemEntity.toString());

        ingredientDetailList.invalidate();
        ingredientAdapter = new IngredientAdapter(this, ingredientEntityList, itemEntity);
        ingredientDetailList.setLayoutManager(null);
        ingredientDetailList.setAdapter(null);
        ingredientDetailList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ingredientDetailList.setAdapter(ingredientAdapter);
        ingredientAdapter.notifyDataSetChanged();

        if(itemEntity.getSelectedPosition()!=-1) {
            smoothScroller.setTargetPosition(itemEntity.getSelectedPosition());
            ingredientDetailList.getLayoutManager().startSmoothScroll(smoothScroller);
        }
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

//		int status;
//		String StrContent;
//
//		String	sPrtCmd = Prt_USB.GetPrtCmdString_LF();
//		String sDefFontcmd = Prt_USB.GetDefFontCmdString_LF();
//		String sDWFontcmd = Prt_USB.GetDWFontCmdString_LF();
//		String sDHFontcmd = Prt_USB.GetDHFontCmdString_LF();
//		String sDHWFontcmd = Prt_USB.GetDHWFontCmdString_LF();


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
        else
        {
            for( i=0; i<2; i++ )
            {
                Longfly_UsbPrt.PrintInitSet_LF();
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line1_32 ,"UTF-8");
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line2_32 ,"UTF-8");
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line3_32 ,"UTF-8");
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line4_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line5_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line6_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line7_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line8_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line9_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line10_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line11_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line12_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line13_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line14_32 ,"UTF-8");
                Longfly_UsbPrt.PrintTicket_Decode_LF( line15_32 ,"UTF-8");
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_Decode_LF( line16_32 ,"UTF-8");
                Longfly_UsbPrt.FeedPaper_LF((byte)150);
            }
        }

    }

    /*private	void Print_test_1( )
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

//		int status;
//		String StrContent;
//
//		String	sPrtCmd = Prt_USB.GetPrtCmdString_LF();
//		String sDefFontcmd = Prt_USB.GetDefFontCmdString_LF();
//		String sDWFontcmd = Prt_USB.GetDWFontCmdString_LF();
//		String sDHFontcmd = Prt_USB.GetDHFontCmdString_LF();
//		String sDHWFontcmd = Prt_USB.GetDHWFontCmdString_LF();

        if( ipaper==80 )
        {
            for( i=0; i<50; i++ )
            {
                Longfly_UsbPrt.PrintInitSet_LF();
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line1_48 );
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line2_48 );
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line3_48 );
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line4_48 );
                Longfly_UsbPrt.PrintTicket_LF( line5_48 );
                Longfly_UsbPrt.PrintTicket_LF( line6_48 );
                Longfly_UsbPrt.PrintTicket_LF( line7_48 );
                Longfly_UsbPrt.PrintTicket_LF( line8_48 );
                Longfly_UsbPrt.PrintTicket_LF( line9_48 );
                Longfly_UsbPrt.PrintTicket_LF( line8_48 );
                Longfly_UsbPrt.PrintTicket_LF( line9_48 );
                Longfly_UsbPrt.PrintTicket_LF( line8_48 );
                Longfly_UsbPrt.PrintTicket_LF( line9_48 );
                Longfly_UsbPrt.PrintTicket_LF( line8_48 );
                Longfly_UsbPrt.PrintTicket_LF( line9_48 );
                Longfly_UsbPrt.PrintTicket_LF( line8_48 );
                Longfly_UsbPrt.PrintTicket_LF( line9_48 );
                Longfly_UsbPrt.PrintTicket_LF( line8_48 );
                Longfly_UsbPrt.PrintTicket_LF( line9_48 );
                Longfly_UsbPrt.PrintTicket_LF( line8_48 );
                Longfly_UsbPrt.PrintTicket_LF( line9_48 );
                Longfly_UsbPrt.PrintTicket_LF( line8_48 );
                Longfly_UsbPrt.PrintTicket_LF( line9_48 );
                Longfly_UsbPrt.PrintTicket_LF( line10_48 );
                Longfly_UsbPrt.PrintTicket_LF( line11_48 );
                Longfly_UsbPrt.PrintTicket_LF( line12_48 );
                Longfly_UsbPrt.PrintTicket_LF( line13_48 );
                Longfly_UsbPrt.PrintTicket_LF( line14_48 );
                Longfly_UsbPrt.PrintTicket_LF( line15_48 );
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line16_48 );
                Longfly_UsbPrt.FeedPaper_LF((byte)150);
            }
        }
        else
        {
            for( i=0; i<50; i++ )
            {
                Longfly_UsbPrt.PrintInitSet_LF();
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line1_32 );
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line2_32 );
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line3_32 );
                Longfly_UsbPrt.SetDefFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line4_32 );
                Longfly_UsbPrt.PrintTicket_LF( line5_32 );
                Longfly_UsbPrt.PrintTicket_LF( line6_32 );
                Longfly_UsbPrt.PrintTicket_LF( line7_32 );
                Longfly_UsbPrt.PrintTicket_LF( line8_32 );
                Longfly_UsbPrt.PrintTicket_LF( line9_32 );
                Longfly_UsbPrt.PrintTicket_LF( line8_32 );
                Longfly_UsbPrt.PrintTicket_LF( line9_32 );
                Longfly_UsbPrt.PrintTicket_LF( line8_32 );
                Longfly_UsbPrt.PrintTicket_LF( line9_32 );
                Longfly_UsbPrt.PrintTicket_LF( line8_32 );
                Longfly_UsbPrt.PrintTicket_LF( line9_32 );
                Longfly_UsbPrt.PrintTicket_LF( line8_32 );
                Longfly_UsbPrt.PrintTicket_LF( line9_32 );
                Longfly_UsbPrt.PrintTicket_LF( line8_32 );
                Longfly_UsbPrt.PrintTicket_LF( line9_32 );
                Longfly_UsbPrt.PrintTicket_LF( line8_32 );
                Longfly_UsbPrt.PrintTicket_LF( line9_32 );
                Longfly_UsbPrt.PrintTicket_LF( line8_32 );
                Longfly_UsbPrt.PrintTicket_LF( line9_32 );
                Longfly_UsbPrt.PrintTicket_LF( line10_32 );
                Longfly_UsbPrt.PrintTicket_LF( line11_32 );
                Longfly_UsbPrt.PrintTicket_LF( line12_32 );
                Longfly_UsbPrt.PrintTicket_LF( line13_32 );
                Longfly_UsbPrt.PrintTicket_LF( line14_32 );
                Longfly_UsbPrt.PrintTicket_LF( line15_32 );
                Longfly_UsbPrt.SetDHFontCmd_LF();
                Longfly_UsbPrt.PrintTicket_LF( line16_32 );
                Longfly_UsbPrt.FeedPaper_LF((byte)150);
            }
        }

    }*/




    private void printLabel(IngredientDetailEntity ingredientDetailEntity,ItemEntity itemEntity,String timeStamp,String measuredWeight){
        IngredientEntity ingredientEntity=groctaurantDatabase.ingredientDao().getIngredientById(ingredientDetailEntity.getIngredientId());
        updatePackingStatus(ingredientEntity.getIngredientId(),true,true,true);
        Log.e(TAG,"printLabel Called "+measuredWeight);

        if(ingredientEntity!=null && ingredientDetailEntity!=null && itemEntity!=null) {
            String line1 = "            DailyKit            ";
            String line2 = "--------------------------------";
            String line3 = "Order Id. : " + itemEntity.getOrderId();
            String line4 = "--------------------------------";
            String line5 = "Date : " + timeStamp.substring(0, 10);
            String line6 = "Time : " + timeStamp.substring(11);
            String line7 = "";
            String line8 = "Item Number : " + ingredientDetailEntity.getIngredientDetailIndex();
            String line9 = "";
            String line10 = "Ingredient Name : " + ingredientEntity.getSlipName();
            Log.e(TAG, "Measured Weight : " + ingredientEntity.getIngredientMeasuredTotalWeight());
            String line15 = "Measured Weight : " + ingredientEntity.getIngredientMeasuredTotalWeight();
            //String line11 ="Labelled Weight : "+ingredientDetailEntity.getIngredientMsr();
            String line12 = "Processing : " + ingredientDetailEntity.getIngredientProcess();
            String line13 = "";
            String line14 = "--------------------------------";
            String line16 = "Ingrdeient ID : " + ingredientDetailEntity.getIngredientId();

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
            //Longfly_UsbPrt.PrintTicket_Decode_LF( line11 );
            Longfly_UsbPrt.PrintTicket_Decode_LF(line12, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line13, "UTF-8");
            Longfly_UsbPrt.PrintTicket_Decode_LF(line14, "UTF-8");
            //Longfly_UsbPrt.PrintBarcode_128_LF(50,"DailyKit");
            String qrData = ingredientDetailEntity.getIngredientId()+","+ingredientDetailEntity.getIngredientDetailIndex()+","+ingredientEntity.getSlipName()+","+ingredientEntity.getIngredientMeasuredTotalWeight()+","+ingredientDetailEntity.getIngredientProcess();
            Bitmap bitmap = null;
            //Log.e(TAG,"Scan Data : "+qrData);
            qrData = ingredientDetailEntity.getIngredientId().substring(0,ingredientDetailEntity.getIngredientId().indexOf("w"))+ingredientDetailEntity.getIngredientId().substring(ingredientDetailEntity.getIngredientId().lastIndexOf("e")+1);
            Log.e(TAG,"Scan Data : "+qrData);
            bitmap = null;
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 200, 200);
                qr.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e(TAG, "BarCode Exception :" + e);
            }
            Longfly_UsbPrt.Prt_BitMap_LF(bitmap, 80);

        }
    }



   /* private void printLabelTestImage(String imagePixel){

        String line1 ="            DailyKit            ";
        String line2 ="--------------------------------";

        Longfly_UsbPrt.PrintInitSet_LF();
        Longfly_UsbPrt.SetDHFontCmd_LF();
        Longfly_UsbPrt.PrintTicket_Decode_LF(line1,"UTF-8");
        Longfly_UsbPrt.SetDefFontCmd_LF();
        Longfly_UsbPrt.PrintTicket_Decode_LF( line2,"UTF-8");
        Longfly_UsbPrt.PrintBarcode_128_LF(50,"DailyKit");
        Longfly_UsbPrt.FeedPaper_LF((byte)150);
    }*/


    /*private void PrintTest_Draw_Square( int iPixel )
    {
        int i=0, j=0, k=0;
        int height, width, row, col, widthMax=0;

        int	iPaperLen = 58;

        if( iPaperLen==80 )
        {
            widthMax = 576;
        }
        else
        {
            widthMax = 384;
        }

        if( iPixel>widthMax )
        {
            iPixel = widthMax;
        }

        boolean[][] s = new boolean[iPixel][iPixel];

        height = iPixel;
        width = iPixel;
        row = (height + 23) / 24;
        col = iPixel;

        int PicDataLen = 3 * col;
        int DataLen = 5 + PicDataLen + 2;	//5 cmd para + buf_len + 2 end char
        byte[] aCmdBuf = new byte[DataLen];
        byte[] aCmdBuf_LineSpace = new byte[3];
        byte[] aCmdBuf_LineSpace_Default = new byte[2];

        aCmdBuf_LineSpace[0] = 27;
        aCmdBuf_LineSpace[1] = 51;
        aCmdBuf_LineSpace[2] = 0;

        aCmdBuf_LineSpace_Default[0] = 27;
        aCmdBuf_LineSpace_Default[1] = 50;

        aCmdBuf[0] = 27;	//0x1b
        aCmdBuf[1] = 42;	//0x2a
        aCmdBuf[2] = 33;	//parameter	m
        aCmdBuf[3] = (byte)(col % 256);	//parameter	nL
        aCmdBuf[4] = (byte)(col / 256);	//parameter	nH
        aCmdBuf[DataLen - 2] = 0x0d;	//change line
        aCmdBuf[DataLen - 1] = 0x0a;	//change line

        //send aCmdBuf_LineSpace to usb printer

        for (int rowi = 0; rowi < row; rowi++)
        {
            int DataOffset = 5;
            for (int coli = 0; coli < col; coli++)
            {
                k = 0;
                for (i = 0; i < 3; i++)	//
                {
                    byte temp = 0;
                    for( j=0; j<8; j++ )
                    {
                        if( (rowi*8*3+k)<iPixel )
                        {
                            if (s[rowi*8*3+k][coli])
                            {
                                temp |= (1<<(7-j));
                            }
                        }
                        k++;
                    }
                    aCmdBuf[DataOffset++]=temp;
                }
            }

            //send aCmdBuf to usb printer
        }

        //send aCmdBuf_LineSpace_Default to usb printer
    }
*/

    public void updatePackingStatus(String ingredientId,boolean isWeighed,boolean isPacked,boolean isLabelled){
        ingredientViewModel.updatePacking(ingredientId,isWeighed,isPacked,isLabelled);
    }

}
