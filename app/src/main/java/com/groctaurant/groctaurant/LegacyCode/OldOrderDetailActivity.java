package com.groctaurant.groctaurant.LegacyCode;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.groctaurant.groctaurant.Model.AllDetailModel;
import com.groctaurant.groctaurant.Model.AllIngredientModel;
import com.groctaurant.groctaurant.Model.IngredientModel;
import com.groctaurant.groctaurant.Model.OrderDetailModel;
import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Serial.SerialPort;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OldOrderDetailActivity extends AppCompatActivity {

    Bundle bundle;
    OrderDetailModel orderDetailModel;
    private static final String LOG_TAG = "OldOrderDetailActivity";
    TextView orderDetailTime,orderDetailName,orderDetailId;
    RecyclerView ingredientList;
    CardView ingredientMeasurementCardview;
    ArrayList<AllIngredientModel> allIngredientModelArrayList;
    OldIngredientAdapter ingredientAdapter;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    TextView orderWeightDisplay;

    final String  TTY_DEV = "/dev/ttymxc3";
    final int bps = 115200;
    SerialPort mSerialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    Auto_Weight weight_thread = new Auto_Weight();
    boolean auto_start = true;
    int	iBiaoDingFlg = 0;

    IngredientModel ingredientModel;
    double weightUpperLimit , weightLowerLimit;
    double accuracy;
    CountDownTimer countDownTimer;
    TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        bundle=getIntent().getBundleExtra(Constants.INTENT_CURRENT_ORDER_DETAIL);
        orderDetailModel=(OrderDetailModel)bundle.getSerializable(Constants.CURRENT_ORDER);
        Log.e(LOG_TAG,orderDetailModel.getOrderName());
        sharedpreferences = AppUtil.getAppPreferences(this);
        editor = sharedpreferences.edit();

        accuracy=Float.parseFloat(sharedpreferences.getString(Constants.WEIGHT_ACCURACY,"0.0"));
        orderDetailTime=(TextView)findViewById(R.id.order_detail_time);
        orderDetailName=(TextView)findViewById(R.id.order_detail_name);
        orderDetailId=(TextView)findViewById(R.id.order_detail_id);
        ingredientList=(RecyclerView)findViewById(R.id.ingredient_list);
        orderWeightDisplay=(TextView)findViewById(R.id.order_weight_display);
        ingredientMeasurementCardview=(CardView)findViewById(R.id.ingredient_measurement_cardview);
        timerText =(TextView)findViewById(R.id.timer_text);

        editor.putString(Constants.CURRENT_INGREDIENT, null);
        editor.commit();

        allIngredientModelArrayList = orderDetailModel.getAllIngredientModelArrayList();
        ingredientList.setHasFixedSize(true);
        ingredientAdapter = new OldIngredientAdapter(this,allIngredientModelArrayList);
        ingredientList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ingredientList.setAdapter(ingredientAdapter);
        final Handler handler = new Handler();
        ingredientAdapter.notifyDataSetChanged();
        handler.postDelayed (() -> {
            Log.e(LOG_TAG,"Width of List :"+ingredientList.getWidth());
            editor.putInt(Constants.WIDTH_OF_INGREDIENT_LIST,ingredientList.getWidth());
            editor.commit();
        }, 100);

        Date date = new Date();
        String strDateFormat = "hh:mm a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedTime= dateFormat.format(date);
        orderDetailTime.setText(formattedTime);

        orderDetailId.setText(orderDetailModel.getOrderId());
        orderDetailName.setText(orderDetailModel.getOrderName()+" ("+orderDetailModel.getPendingRequest()+"/"+orderDetailModel.getAllRequest()+")");
        weight_thread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IngredientModel ingredientModel=new IngredientModel();
        editor.putString(Constants.CURRENT_INGREDIENT, new Gson().toJson(null));
        editor.commit();
    }


    class Auto_Weight extends Thread{
        @Override
        public void run(){
            try {
                mSerialPort = new SerialPort(new File(TTY_DEV), bps, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream  = mSerialPort.getInputStream();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                Log.e(LOG_TAG,"SecurityException "+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(LOG_TAG,"IOException "+e.toString());
                e.printStackTrace();
            }
            int buf_num = 0, i32BiaoDing=0, i32Fendu=0, iNum=0, iStateFlg=0;
            byte[] receive_buf = new byte[20];
            boolean receive_state = false;

            while(true)
            {
                if (auto_start) {

                    byte[] buf = new byte[1];
                    int size;
                    try {
                        size = mInputStream.read(buf);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    if (receive_state || buf[0]== -86 ) {
                        receive_buf[buf_num] = (byte) (0xff&buf[0]);
                        buf_num ++;
                        receive_state = true;
                        if (buf_num == 19)
                        {
                            if( iBiaoDingFlg>0 )
                            {
                                iStateFlg = 0;
                                if( (iBiaoDingFlg==1 && receive_buf[1]==104) || (iBiaoDingFlg==2 && receive_buf[1]==102) ||
                                        (iBiaoDingFlg==3 && receive_buf[1]==-120) )
                                {
                                    iStateFlg = 1;
                                }
                                if( iStateFlg==0 )
                                {
                                    iNum++;
                                    if( iNum>10 )
                                    {
                                        iStateFlg = 2;
                                    }
                                }
                                if( iStateFlg>0 )
                                {
                                    Message msg = new Message();


                                    Bundle bundle = new Bundle();
                                    msg.what = 1;
                                    if( iStateFlg==1 )
                                    {
                                        bundle.putString("get_state", "Success");
                                    }
                                    else
                                    {
                                        bundle.putString("get_state", "Fail");
                                    }

                                    msg.setData(bundle);

                                    handler.sendMessage(msg);

                                    iBiaoDingFlg = 0;
                                    iNum = 0;
                                }
                            }
                            else if( receive_buf[1]==85 )
                            {
                                String receice_string = byte2HexStr(receive_buf);;

                                String my_num_string = null;

                                i32BiaoDing = receive_buf[11] & 0xff;
                                i32BiaoDing = i32BiaoDing << 8;
                                i32BiaoDing = i32BiaoDing + (receive_buf[10] & 0xff);

                                i32Fendu = receive_buf[17] & 0xff;
                                i32Fendu = i32Fendu << 8;
                                i32Fendu = i32Fendu + (receive_buf[16] & 0xff);

                                String get_weight = e_scale_Algorithm(
                                        Add_0(Integer.toHexString(receive_buf[9]& 0xFF))+
                                                Add_0(Integer.toHexString(receive_buf[8]& 0xFF))+
                                                Add_0(Integer.toHexString(receive_buf[7]& 0xFF))+
                                                Add_0(Integer.toHexString(receive_buf[6]& 0xFF)),

                                        Add_0(Integer.toHexString(receive_buf[5]& 0xFF))+
                                                Add_0(Integer.toHexString(receive_buf[4]& 0xFF))+
                                                Add_0(Integer.toHexString(receive_buf[3]& 0xFF))+
                                                Add_0(Integer.toHexString(receive_buf[2]& 0xFF)),
                                        i32BiaoDing, i32Fendu);
                                Message msg = new Message();


                                Bundle bundle = new Bundle();
                                msg.what = 0;
                                bundle.putString("get_weight", get_weight);

                                msg.setData(bundle);

                                handler.sendMessage(msg);
                            }
                            else
                            {
                                buf_num =0;
                                receive_state = false;
                            }

                            buf_num =0;
                            receive_state = false;
                        }
                        else
                        {

                        }
                    }
                    else
                    {

                    }
                }
                else
                {
                    buf_num =0;
                    receive_state = false;
                    wanglei_loop(100);
                }
            }
        }
    }
    public void wanglei_loop(int search_interval)
    {
        try {
            Thread.sleep(search_interval);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String e_scale_Algorithm(String string, String string2, int i32BiaoDingValue, int i32FenduValue) {
        // TODO Auto-generated method stub
        double 	value = 0;
        int		i32Value, i32Tmp;
        String 	p = ""+0;
        try {
            double AD = Integer.parseInt(string,16);
            double kg_AD = Integer.parseInt(string2,16);

//			value=(float) AD*i32BiaoDingValue/kg_AD*1000;//* 1000;
            value=(float) AD*i32BiaoDingValue/kg_AD;//* 1000;
            BigDecimal d = new BigDecimal(value);
            value = d.setScale(4, RoundingMode.HALF_UP).doubleValue()*10000;
            i32Value = (int)value;
            i32Value = i32Value%100;
            i32Tmp = i32FenduValue*10/2;

            if( i32Value>(i32FenduValue*10) )
            {
                i32Value = i32Value - (i32FenduValue*10);
            }
            value = value - i32Value;
            if( i32Value>=i32Tmp )
            {
                value = value + (i32FenduValue*10);
            }
            i32Value = (int)value;
            value = (float)i32Value * 0.0001;
            DecimalFormat decimalFormat=new DecimalFormat(".000");
            p = decimalFormat.format(value);
        } catch (Exception e) {
            // TODO: handle exception
            DecimalFormat decimalFormat=new DecimalFormat(".0");
            p = decimalFormat.format(value);
        }

        return  ""+p;
    }


    public static String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        /*for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }*/
        return sb.toString().toUpperCase().trim();
    }


    private String Add_0(String String) {
        // TODO Auto-generated method stub
        String myString = String;
        int my_num = Integer.parseInt(myString,16);
        if (my_num < 16) {
            myString = "0"+ String;
        }
        return myString;
    }

    public Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                {

                    orderWeightDisplay.setText(String.valueOf(Double.parseDouble(msg.getData().getString("get_weight"))*1000));
                    Type type = new TypeToken<IngredientModel>() {}.getType();
                    ingredientModel = new Gson().fromJson(sharedpreferences.getString(Constants.CURRENT_INGREDIENT, ""), type);
                    accuracy=Float.parseFloat(sharedpreferences.getString(Constants.WEIGHT_ACCURACY,"0.0"));
                    if(ingredientModel!=null) {

                        weightLowerLimit = ingredientModel.getIngredientQuantity()-ingredientModel.getIngredientQuantity()*accuracy/100;
                        weightUpperLimit = ingredientModel.getIngredientQuantity()+ingredientModel.getIngredientQuantity()*accuracy/100;
                        Log.e(LOG_TAG,"Change:"+accuracy+" "+accuracy/100+" "+ingredientModel.getIngredientQuantity()*accuracy/100);
                    }

                    if(ingredientModel==null) {
                        ingredientMeasurementCardview.setCardBackgroundColor(getResources().getColor(R.color.lightBlue));
                    }
                    else if(Double.parseDouble(orderWeightDisplay.getText().toString())>weightUpperLimit)
                    {
                        ingredientMeasurementCardview.setCardBackgroundColor(getResources().getColor(R.color.cardview_red));
                        Log.e(LOG_TAG,"More "+orderWeightDisplay.getText().toString()+" "+ingredientModel.getIngredientQuantity());
                        timerText.setVisibility(View.GONE);
                        Log.e(LOG_TAG,"Upper Limit : "+weightUpperLimit);
                        if(countDownTimer!=null){
                            countDownTimer.cancel();
                            countDownTimer=null;
                        }
                    }
                    else if(Double.parseDouble(orderWeightDisplay.getText().toString())<=weightUpperLimit && Double.parseDouble(orderWeightDisplay.getText().toString())>=weightLowerLimit)
                    {
                        ingredientMeasurementCardview.setCardBackgroundColor(getResources().getColor(R.color.cardview_green));
                        Log.e(LOG_TAG,"Equal "+orderWeightDisplay.getText().toString()+" "+ingredientModel.getIngredientQuantity());
                        if(countDownTimer==null) {

                            timerText.setVisibility(View.VISIBLE);
                            countDownTimer = new CountDownTimer(5000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    timerText.setText(String.valueOf((millisUntilFinished/1000)+1));

                                }

                                public void onFinish() {
                                    Toast.makeText(OldOrderDetailActivity.this, "Printing the Label", Toast.LENGTH_SHORT).show();
                                    timerText.setVisibility(View.GONE);

                                    updateIngredient();
                                }

                            }.start();
                        }
                    }
                    else if(Double.parseDouble(orderWeightDisplay.getText().toString())<weightLowerLimit)
                    {
                        Log.e(LOG_TAG,"Lower Limit : "+weightLowerLimit);
                        ingredientMeasurementCardview.setCardBackgroundColor(getResources().getColor(R.color.cardview_yellow));
                        Log.e(LOG_TAG,"Less "+orderWeightDisplay.getText().toString()+" "+ingredientModel.getIngredientQuantity());
                        timerText.setVisibility(View.GONE);
                        if(countDownTimer!=null){
                            countDownTimer.cancel();
                            countDownTimer=null;
                        }
                    }



                }
                break;
                case 1:
                {
                    Toast.makeText(OldOrderDetailActivity.this, msg.getData().getString("get_state"), Toast.LENGTH_SHORT).show();
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void updateIngredient(){
        ArrayList<AllDetailModel> allDetailModelArrayList=new ArrayList<>();
        ArrayList<OrderDetailModel> orderDetailModelArrayList=new ArrayList<>();
        ArrayList<AllIngredientModel> allIngredientModelArrayList=new ArrayList<>();
        ArrayList<IngredientModel> ingredientModelArrayList=new ArrayList<>();
        OrderDetailModel orderDetailModel;
        IngredientModel ingredientModel,selectedIngredientModel;
        selectedIngredientModel=new IngredientModel();

        Type type = new TypeToken<ArrayList<AllDetailModel>>() {}.getType();
        allDetailModelArrayList= new Gson().fromJson(sharedpreferences.getString(Constants.ALL_ORDER_LIST, ""), type);
        type = new TypeToken<OrderDetailModel>() {}.getType();
        orderDetailModel = new Gson().fromJson(sharedpreferences.getString(Constants.CURRENT_ORDER_MODEL, ""), type);
        type = new TypeToken<IngredientModel>() {}.getType();
        ingredientModel = new Gson().fromJson(sharedpreferences.getString(Constants.CURRENT_INGREDIENT, ""), type);

        for(int i=0;i<allDetailModelArrayList.size();i++){
            orderDetailModelArrayList=allDetailModelArrayList.get(i).getAllOrderArrayList();
            for(int j=0;j<orderDetailModelArrayList.size();j++){
                if(orderDetailModelArrayList.get(j).getOrderId().equals(orderDetailModel.getOrderId())){
                    allIngredientModelArrayList=orderDetailModelArrayList.get(j).getAllIngredientModelArrayList();
                    for(int k=0;k<allIngredientModelArrayList.size();k++){
                        ingredientModelArrayList=allIngredientModelArrayList.get(k).getIngredientModelArrayList();
                        for(int l=0;l<ingredientModelArrayList.size();l++){

                            if(ingredientModelArrayList.get(l).getIngredientId().equals(ingredientModel.getIngredientId())){
                                ingredientModelArrayList.get(l).setPacked(true);
                                if(l<ingredientModelArrayList.size()-1) {
                                    selectedIngredientModel = ingredientModelArrayList.get(l+1);
                                }
                            }
                        }
                    }
                }

            }

        }

        editor.putString(Constants.ALL_ORDER_LIST, new Gson().toJson(allDetailModelArrayList));
        editor.putString(Constants.CURRENT_INGREDIENT, new Gson().toJson(selectedIngredientModel));
        editor.commit();
        ingredientAdapter.updateList();
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
