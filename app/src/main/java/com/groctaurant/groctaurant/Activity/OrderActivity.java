package com.groctaurant.groctaurant.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.arch.lifecycle.LiveData;
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
import android.support.v7.widget.LinearLayoutManager;
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


import com.amitshekhar.DebugDB;
import com.example.lf_usbprinter.Prt_USB;
import com.example.longfly_sdk_rf.Longfly_RF;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.groctaurant.groctaurant.Adapter.OrderAdapter;
import com.groctaurant.groctaurant.Adapter.TabAdapter;
import com.groctaurant.groctaurant.Callback.OrderCallback;
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
import com.groctaurant.groctaurant.ViewModel.OrderViewModel;
import com.groctaurant.groctaurant.ViewModel.PlanningViewModel;
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

public class OrderActivity extends AppCompatActivity implements OrderCallback {

    private static final String TAG = "OrderActivity";
    final String TTY_DEV = "/dev/ttymxc3";
    final int bps = 115200;
    protected OutputStream mOutputStream;
    RecyclerView orderList;
    RecyclerView tabList;
    GroctaurantDatabase db;
    LiveData<List<OrderEntity>> orderEntityArrayList;
    List<TabEntity> tabEntityArrayList;
    OrderAdapter orderScreenAdapter;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    TextView orderScreenWeight;
    SerialPort mSerialPort = null;
    Auto_Weight weight_thread = new Auto_Weight();
    boolean auto_start = true;
    int iBiaoDingFlg = 0;
    private InputStream mInputStream;
    TabAdapter tabAdapter;
    IngredientDetailEntity ingredientDetailEntity;
    TextView ingredientSelected;
    double accuracy;
    double weightUpperLimit, weightLowerLimit;
    LinearLayout weightDisplayLayout;
    CountDownTimer countDownTimer;
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;
    TextView weightStatus, weightDifference,weightScreenItemName,weightScreenOrderId;
    ItemEntity itemEntity;
    LinearLayout setting,planningTab,inventoryTab;
    Longfly_RF Longfly_RF_1=null;
    Prt_USB Longfly_UsbPrt=null;
    OrderViewModel orderViewModel;
    Button simulator;
    EditText setWeight;
    Button sendWeight,printTest;
    TextView orderName;
    Switch simulatorSwitch;
    ImageView tareButton;
    TextView ingredientScreenWeight;
    boolean isAlreadyUpdated;
    PlanningViewModel planningViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_screen);
        orderViewModel= ViewModelProviders.of(this).get(OrderViewModel.class);
        planningViewModel= ViewModelProviders.of(this).get(PlanningViewModel.class);
        orderList = (RecyclerView) findViewById(R.id.order_list);
        tabList = (RecyclerView) findViewById(R.id.tab_list);
        ingredientSelected = (TextView) findViewById(R.id.ingredient_selected);
        weightDisplayLayout = (LinearLayout) findViewById(R.id.weight_display_layout);
        orderScreenWeight = (TextView) findViewById(R.id.ingredient_screen_weight);
        weightStatus = (TextView) findViewById(R.id.weight_status);
        weightDifference = (TextView) findViewById(R.id.weight_difference);
        weightScreenItemName=findViewById(R.id.weight_screen_item_name);
        weightScreenOrderId=findViewById(R.id.weight_screen_order_id);
        setWeight = (EditText) findViewById(R.id.set_weight);
        sendWeight = (Button) findViewById(R.id.send_weight);
        printTest = (Button) findViewById(R.id.print_test);
        simulator=(Button)findViewById(R.id.simulator);
        setting=(LinearLayout)findViewById(R.id.setting);
        inventoryTab=(LinearLayout)findViewById(R.id.inventory_tab);
        planningTab=(LinearLayout)findViewById(R.id.planning_tab);
        tareButton=(ImageView) findViewById(R.id.tare_button);
        orderName = (TextView) findViewById(R.id.order_name);
        simulatorSwitch=(Switch)findViewById(R.id.simulator_switch);
        simulator=(Button)findViewById(R.id.simulator);
        tareButton=(ImageView) findViewById(R.id.tare_button);
        ingredientScreenWeight = (TextView) findViewById(R.id.ingredient_screen_weight);
        db = Room.databaseBuilder(getApplicationContext(), GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        sharedpreferences = AppUtil.getAppPreferences(this);
        groctaurantDatabase = Room.databaseBuilder(getApplicationContext(), GroctaurantDatabase.class, "Development")
                .allowMainThreadQueries()
                .build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
        editor = sharedpreferences.edit();
        accuracy = Float.parseFloat(sharedpreferences.getString(Constants.WEIGHT_ACCURACY,"0.0"));
        Log.e(TAG,"Accuracy :"+accuracy);
        /*Runnable r = new Runnable() {
            @Override
            public void run() {
                orderEntityArrayList = db.orderDao().loadAllOrder();
                //Log.e(TAG, "Order Entity List : " + orderEntityArrayList.toString());
                observeOrder();
            }
        };

        Thread newThread = new Thread(r);
        newThread.start();*/

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Log.e(TAG, "Width of List :" + orderList.getWidth());
            editor.putInt(Constants.WIDTH_OF_ORDER_LIST, orderList.getWidth());
            editor.commit();
        }, 100);

        observeTab();

        Type type = new TypeToken<ItemEntity>() {
        }.getType();
        itemEntity = new Gson().fromJson(sharedpreferences.getString(Constants.SELECTED_ITEM_ENTITY, ""), type);

        logCount();

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        sendWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendWeight();
            }
        });

        Longfly_UsbPrt = new Prt_USB(this, 0);
        Longfly_RF_1 = new Longfly_RF( this );

        inventoryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentInventory = new Intent(OrderActivity.this, InventoryActivity.class);
                startActivity(intentInventory);
            }
        });

        planningTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPlanning = new Intent(OrderActivity.this, PlanningActivity.class);
                startActivity(intentPlanning);
            }
        });

        //weight_thread.start();
    }
    
    public void sendWeight() {
        Message msg1 = new Message();
        Bundle bundle1 = new Bundle();
        msg1.what = 0;
        bundle1.putString("get_weight", String.valueOf(0));
        msg1.setData(bundle1);
        Log.e(TAG,"Handler Called 6");
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
        handler.sendMessage(msg);
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {

                    if(orderViewModel.isTareEnabled()) {
                        //Log.e(TAG,"Tare Enabled :  Screen");
                        ingredientScreenWeight.setText(orderViewModel.getIngredientWeightForTare(msg.getData().getString("get_weight")));
                    }
                    else {
                        //Log.e(TAG,"Tare Disabled :  Screen");
                        ingredientScreenWeight.setText(String.valueOf(Double.parseDouble(msg.getData().getString("get_weight")) * 1000));
                    }
                    Type type = new TypeToken<IngredientDetailEntity>() {}.getType();
                    ingredientDetailEntity = new Gson().fromJson(sharedpreferences.getString(Constants.SELECTED_INGREDIENT_ENTITY, ""), type);
                    if(ingredientDetailEntity==null){
                        IngredientDetailEntity ingredientDetailEntity1 = new IngredientDetailEntity();
                        ingredientDetailEntity.setIngredientName("");
                        ingredientDetailEntity.setIngredientQuantity(0);
                        ingredientDetailEntity=ingredientDetailEntity1;
                    }

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
                                        Log.e(TAG,"Detail after Update :"+orderViewModel.getIngredientById(ingredientDetailEntity.getIngredientId()));
                                        selectPosition = itemEntity.getSelectedPosition() + 1;
                                        orderViewModel.switchOffTare(OrderActivity.this);
                                        printLabel(ingredientDetailEntity,itemEntity,timeStamp,ingredientScreenWeight.getText().toString());
                                    }
                                    else
                                    {
                                        groctaurantDatabase.ingredientDetailDao().isPackedUpdate(ingredientDetailEntity.getIngredientDetailId(), true);
                                        int nextIngredientPosition = orderViewModel.checkAllProductPacked(ingredientDetailEntity);
                                        Log.e(TAG,"Next Ingredient ID: "+nextIngredientPosition);
                                        if(nextIngredientPosition==-1){
                                            groctaurantDatabase.ingredientDao().setMeasuredTotalWeight(ingredientDetailEntity.getIngredientId(),orderViewModel.getTotalWeightForTare(ingredientScreenWeight.getText().toString()) );
                                            printLabel(ingredientDetailEntity,itemEntity,timeStamp,ingredientScreenWeight.getText().toString());
                                            groctaurantDatabase.ingredientDao().isPackedCompleteUpdate(ingredientDetailEntity.getIngredientId(), true);
                                            selectPosition = itemEntity.getSelectedPosition() + 1;
                                            orderViewModel.switchOffTare(OrderActivity.this);
                                        }
                                        else{
                                            orderViewModel.setTareWeight(ingredientScreenWeight.getText().toString());
                                            selectPosition = itemEntity.getSelectedPosition();
                                            orderViewModel.setIngredientByPositionAndDetailId(ingredientDetailEntity,nextIngredientPosition);
                                            isAlreadyUpdated =true;
                                        }
                                    }
                                    if(Integer.parseInt(itemEntity.getItemNoOfIngredient())<=selectPosition){
                                        Log.e(TAG,selectPosition+" Code Ran");
                                        selectPosition=0;
                                    }
                                    if(!isAlreadyUpdated) {
                                        groctaurantDatabase.itemDao().setSelectedItem(itemEntity.getItemOrderId(), selectPosition);
                                        itemEntity.setSelectedPosition(selectPosition);
                                        orderViewModel.setIngredientDetailByItemEntity(itemEntity);
                                    }
                                    itemEntity=orderViewModel.getCurrentItemEntity();
                                    if(itemEntity.getSelectedPosition()==-2){
                                        ingredientSelected.setText("All Ingredient Packed Successfully for this Item");
                                    }
                                    else {
                                        IngredientDetailEntity selectedIngredientDetailEntity = orderViewModel.getSelectedIngredientDetailEntity();
                                        ingredientSelected.setText(selectedIngredientDetailEntity.getIngredientName() + " - " + selectedIngredientDetailEntity.getIngredientQuantity() + " gm");
                                    }
                                    weightDifference.setText("Start weighing");
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
                    Toast.makeText(OrderActivity.this, msg.getData().getString("get_state"), Toast.LENGTH_SHORT).show();
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void logCount() {
        Log.e(TAG, "Count of Order :" + groctaurantDatabase.orderDao().countOrder());
        Log.e(TAG, "Count of Item :" + groctaurantDatabase.itemDao().countItemDao());
        Log.e(TAG, "Count of Ingredient :" + groctaurantDatabase.ingredientDao().countIngredientDao());
        Log.e(TAG, "Count of Ingredient Detail :" + groctaurantDatabase.ingredientDetailDao().countIngredientDetailDao());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void changeTareDisplay() {
        if(orderViewModel.isTareEnabled()){
            tareButton.setBackground(getResources().getDrawable(R.drawable.round_green));
        }
        else{
            tareButton.setBackground(getResources().getDrawable(R.drawable.round_white));
        }
    }

    public void callSwitchToTare(){
        orderViewModel.switchOnTare(this,0);
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
                if(orderViewModel.isTareEnabled()){
                    orderViewModel.switchOffTare(this);
                }
                else{
                    Log.e(TAG,ingredientScreenWeight.getText().toString());
                    Float weightOnScreen=Float.parseFloat(ingredientScreenWeight.getText().toString());
                    orderViewModel.switchOnTare(this,weightOnScreen);
                }
                break;
        }
    }

    public void observeOrder() {
        /*orderEntityArrayList.observe(this, orderEntities -> {
            orderList.setHasFixedSize(true);
            orderScreenAdapter = new OrderAdapter(this, orderEntities);
            orderList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            orderList.setAdapter(orderScreenAdapter);
            orderScreenAdapter.notifyDataSetChanged();
        });*/
        List<OrderEntity> orderEntities=db.orderDao().loadAllOrderList();
        orderList.setHasFixedSize(true);
        orderScreenAdapter = new OrderAdapter(this, orderEntities);
        orderList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        orderList.setAdapter(orderScreenAdapter);
        orderScreenAdapter.notifyDataSetChanged();

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


    public void observeTab() {
        tabEntityArrayList = db.tabDao().load();
        tabList.setHasFixedSize(true);
        tabAdapter = new TabAdapter(this, tabEntityArrayList);
        tabList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tabList.setAdapter(tabAdapter);
        tabAdapter.notifyDataSetChanged();
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
        auto_start = true;
        super.onResume();
        planningViewModel.getStats();
        tabEntityArrayList = db.tabDao().load();
        observeTab();
        Type type = new TypeToken<ItemEntity>() {}.getType();
        itemEntity = new Gson().fromJson(sharedpreferences.getString(Constants.SELECTED_ITEM_ENTITY, ""), type);
        observeOrder();


        if(orderViewModel.isSimulated()){
            //Log.e(TAG,"is Simulated");
            simulator.setVisibility(View.VISIBLE);
        }
        else{
            //Log.e(TAG,"Is Not Simulated");
            simulator.setVisibility(View.GONE);
        }

        if(orderViewModel.isSimulated() || orderViewModel.isManualSetWeightEnabled()){
            auto_start=false;
        }
        else{
            auto_start=true;
        }


        if(orderViewModel.isManualSetWeightEnabled()){
            setWeight.setVisibility(View.VISIBLE);
            sendWeight.setVisibility(View.VISIBLE);
        }
        else{
            setWeight.setVisibility(View.GONE);
            sendWeight.setVisibility(View.GONE);
        }
        if(orderViewModel.getCurrentItemEntity()!=null && orderViewModel.getSelectedOrderId()!=null) {
            weightScreenItemName.setText(orderViewModel.getCurrentItemEntity().getItemName());
            weightScreenOrderId.setText(orderViewModel.getSelectedOrderId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Longfly_UsbPrt.Stop( );
    }

    public void sendWeightBySimulator() {
        Log.e(TAG," sendWeightBySimulator Called");
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 0;
        bundle.putString("get_weight", String.valueOf(0));
        msg.setData(bundle);
        Log.e(TAG,"Handler Called 4");
        handler.sendMessage(msg);


        if(orderViewModel.getSelectedIngredientDetailEntity()!=null) {

            Message msg1 = new Message();
            Bundle bundle1 = new Bundle();
            msg1.what = 0;
            IngredientDetailEntity currentIngredientDetailEntity = orderViewModel.getSelectedIngredientDetailEntity();
            bundle1.putString("get_weight", String.valueOf(currentIngredientDetailEntity.getIngredientQuantity() / 1000));
            Log.e(TAG,"Getting value in sendWeightBySimulator "+currentIngredientDetailEntity.getIngredientQuantity());
            msg1.setData(bundle1);
            Log.e(TAG, "Handler Called 5");
            handler.sendMessage(msg1);
        }
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
    }


    private void printLabel(IngredientDetailEntity ingredientDetailEntity,ItemEntity itemEntity,String timeStamp,String measuredWeight){
        IngredientEntity ingredientEntity=groctaurantDatabase.ingredientDao().getIngredientById(ingredientDetailEntity.getIngredientId());
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
