package com.groctaurant.groctaurant.Activity;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.groctaurant.groctaurant.Adapter.IngredientAdapter;
import com.groctaurant.groctaurant.Adapter.OrderAdapter;
import com.groctaurant.groctaurant.Adapter.PlanningAdapter;
import com.groctaurant.groctaurant.Callback.PlanningCallback;
import com.groctaurant.groctaurant.Model.PlanningModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;
import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Room.Entity.OrderEntity;
import com.groctaurant.groctaurant.Serial.SerialPort;
import com.groctaurant.groctaurant.Utils.Constants;
import com.groctaurant.groctaurant.ViewModel.PlanningViewModel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlanningActivity extends AppCompatActivity implements PlanningCallback {

    PlanningViewModel planningViewModel;
    RecyclerView planningRecyclerView;
    PlanningAdapter planningAdapter;
    private static final String TAG = "PlanningActivity";
    final String TTY_DEV = "/dev/ttymxc3";
    final int bps = 115200;
    protected OutputStream mOutputStream;
    IngredientAdapter ingredientAdapter;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    SerialPort mSerialPort = null;
    //Auto_Weight weight_thread = new Auto_Weight();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        //planningViewModel.getStats(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.real_time_tab: {
                finish();
                break;
            }
            case  R.id.inventory_tab: {
                Intent intentInventory = new Intent(PlanningActivity.this, InventoryActivity.class);
                startActivity(intentInventory);
                finish();
                break;
            }
            case  R.id.setting_tab: {
                Intent intent = new Intent(PlanningActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            /*case  R.id.simulator_switch: {
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
                break;*/
        }
    }

    public void init(){
        setContentView(R.layout.activity_plannings);
        planningViewModel= ViewModelProviders.of(this).get(PlanningViewModel.class);
        planningRecyclerView=findViewById(R.id.planning_recycler_view);
    }

    @Override
    public void updateList() {
        final long startTime = System.nanoTime();
        ArrayList<PlanningModel> planningModelArrayList=planningViewModel.getPlanningModelList();
        planningRecyclerView.setHasFixedSize(true);
        planningAdapter = new PlanningAdapter(this, planningModelArrayList);
        planningRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        planningRecyclerView.setAdapter(planningAdapter);
        planningAdapter.notifyDataSetChanged();
        final long duration = System.nanoTime() - startTime;
        Log.e(TAG,"Duration : "+duration);
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

   /* @Override
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

    private void printLabel(IngredientDetailEntity ingredientDetailEntity, ItemEntity itemEntity, String timeStamp, String measuredWeight){
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
    }*/

}
