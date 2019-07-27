package com.groctaurant.groctaurant.LegacyCode;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class OldOrderActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String LOG_TAG = "OldOrderActivity";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    ArrayList<AllDetailModel> allDetailModelArrayList=new ArrayList<>();
    ArrayList<AllDetailModel> allDetailModelArrayListForListView=new ArrayList<>();
    ArrayList<OrderDetailModel> orderDetailModelArrayList=new ArrayList<>();
    ArrayList<AllIngredientModel> allIngredientModelArrayList=new ArrayList<>();
    ArrayList<IngredientModel> ingredientModelArrayList=new ArrayList<>();
    AllDetailModel allDetailModel;
    OrderDetailModel orderDetailModel;
    AllIngredientModel allIngredientModel;
    IngredientModel ingredientModel;
    String[] orderNameList,orderQuantityList,orderServingList,orderSKU;
    JSONArray orderIngredientNumber;
    JSONObject ingredientSlipName,ingredientName,ingredientQuantity,ingredientMeasure,ingredientSection,ingredientProcess;
    JSONArray ingredientSlipNameStr,ingredientNameStr,ingredientQuantityStr,ingredientMeasureStr,ingredientSectionStr,ingredientProcessStr;
    String[] ingredientNameList,ingredientQuantityList,ingredientMeasureList,ingredientSectionList,ingredientProcessList;
    RecyclerView orderList;
    OldOrderAdapter orderAdapter;
    TextView timeTextView;
    TextView orderWeightDisplay;

    final String  TTY_DEV = "/dev/ttymxc3";
    final int bps = 115200;
    SerialPort mSerialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    Auto_Weight weight_thread = new Auto_Weight();
    boolean auto_start = true;
    int	iBiaoDingFlg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        pDialog = new ProgressDialog(this);
        FetchOrder fetchOrder=new FetchOrder();
        fetchOrder.execute();
        orderList=(RecyclerView)findViewById(R.id.order_list);
        orderWeightDisplay=(TextView)findViewById(R.id.order_weight_display);
        sharedpreferences = AppUtil.getAppPreferences(this);
        editor = sharedpreferences.edit();
        Type type = new TypeToken<ArrayList<AllDetailModel>>() {}.getType();
        allDetailModelArrayListForListView = new Gson().fromJson(sharedpreferences.getString(Constants.ALL_ORDER_LIST, ""), type);
        orderList.setHasFixedSize(true);
        orderAdapter = new OldOrderAdapter(this,allDetailModelArrayListForListView);
        orderList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        orderList.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged();
        Date date = new Date();
        String strDateFormat = "hh:mm a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedTime= dateFormat.format(date);
        timeTextView=(TextView)findViewById(R.id.time_textView);
        timeTextView.setText(formattedTime);
        final Handler handler = new Handler();
        handler.postDelayed (() -> {
            Log.e(LOG_TAG,"Width of List :"+orderList.getWidth());
            editor.putInt(Constants.WIDTH_OF_ORDER_LIST,orderList.getWidth());
            editor.commit();
        }, 100);
        //weight_thread.start();
    }

    class FetchOrder extends AsyncTask {

        @Override
        protected void onPreExecute() {
            Log.e(LOG_TAG,"In FetchOrder onPreExecute");
            super.onPreExecute();
            pDialog.show();
            pDialog.setMessage("Please Wait");
            pDialog.setCancelable(true);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            BufferedReader bufferedReader = null;
            try {
                Log.e(LOG_TAG,"In FetchOrder doInBackground");
                URL url= new URL(Constants.URL_FETCH_ORDER);
                Log.e(LOG_TAG,Constants.URL_FETCH_ORDER);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
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
                    return new String("false : " + responseCode);
                }
            } catch (Exception ex) {
                return ex;
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
            pDialog.dismiss();
            Log.e(LOG_TAG,"In FetchOrder onPostExecute");
            if(o!=null) {
                JSONObject jObject = null;
                try {
                    //Log.e(LOG_TAG, "JSON: " + o.toString());
                    jObject = new JSONObject(o.toString());
                    if(jObject.getString("success").equals("true")) {
                        JSONArray allOrderJsonArray=jObject.getJSONArray("all_order");
                        allDetailModelArrayList=new ArrayList<>();
                        for(int i=0;i<allOrderJsonArray.length();i++){
                            JSONObject jsonObject=allOrderJsonArray.getJSONObject(i);
                            allDetailModel=new AllDetailModel();
                            allDetailModel.setOrderStatus(jsonObject.getString("Order_Status "));
                            orderDetailModelArrayList=new ArrayList<>();
                            orderNameList=jsonObject.getString("Recipe_Name").split(", ");
                            orderQuantityList=jsonObject.getString("Recipe_Quantity").split(", ");
                            orderServingList=jsonObject.getString("Recipe_Servings").split(", ");
                            orderSKU=jsonObject.getString("Recipe_SKU").split(", ");
                            orderIngredientNumber=jsonObject.getJSONArray("IngridientsNo");
                            ingredientSlipName=jsonObject.getJSONObject("Slip_name");
                            ingredientName=jsonObject.getJSONObject("ing_name");
                            ingredientQuantity=jsonObject.getJSONObject("ing_qty");
                            ingredientMeasure=jsonObject.getJSONObject("ing_msr");
                            ingredientSection=jsonObject.getJSONObject("ing_section");
                            ingredientProcess=jsonObject.getJSONObject("ing_process");
                            for(int j=0;j<orderNameList.length;j++){
                                orderDetailModel=new OrderDetailModel();
                                orderDetailModel.setOrderId(jsonObject.getString("Order_Id"));
                                orderDetailModel.setOrderSKU(orderSKU[j].trim());
                                orderDetailModel.setOrderName(orderNameList[j].trim());
                                orderDetailModel.setServing(Integer.parseInt(orderServingList[j].trim()));
                                orderDetailModel.setQuantity(Integer.parseInt(orderQuantityList[j].trim()));
                                orderDetailModel.setOrderStatus(jsonObject.getString("Order_Status "));
                                orderDetailModel.setOrderNumber(Integer.parseInt(jsonObject.getString("Order_Number")));
                                orderDetailModel.setPendingRequest(0);
                                orderDetailModel.setAllRequest(orderIngredientNumber.getInt(j));
                                allIngredientModelArrayList=new ArrayList<>();
                                allIngredientModelArrayList.clear();
                                ingredientSlipNameStr=ingredientSlipName.getJSONArray(orderNameList[j].trim());
                                ingredientNameStr=ingredientName.getJSONArray(orderNameList[j].trim());
                                ingredientQuantityStr=ingredientQuantity.getJSONArray(orderNameList[j].trim());
                                ingredientMeasureStr=ingredientMeasure.getJSONArray(orderNameList[j].trim());
                                ingredientSectionStr=ingredientSection.getJSONArray(orderNameList[j].trim());
                                ingredientProcessStr=ingredientProcess.getJSONArray(orderNameList[j].trim());

                                for(int k=0;k<orderIngredientNumber.getInt(j);k++){
                                    allIngredientModel=new AllIngredientModel();
                                    allIngredientModel.setIngredientSlipName((String)ingredientSlipNameStr.get(k));
                                    ingredientModelArrayList=new ArrayList<>();
                                    ingredientModelArrayList.clear();
                                    ingredientNameList=((String)ingredientNameStr.get(k)).split(", ");
                                    ingredientQuantityList=((String)ingredientQuantityStr.get(k)).split(", ");
                                    ingredientMeasureList=((String)ingredientMeasureStr.get(k)).split(", ");
                                    ingredientSectionList=((String)ingredientSectionStr.get(k)).split(", ");
                                    ingredientProcessList=((String)ingredientProcessStr.get(k)).split(", ");
                                    ingredientModelArrayList=new ArrayList<>();
                                    ingredientModelArrayList.clear();
                                    for(int l =0;l<ingredientNameList.length;l++){
                                        ingredientModel=new IngredientModel();
                                        ingredientModel.setIngredientId(jsonObject.getString("Order_Id")+"_"+orderSKU[j].trim()+"_"+l);
                                        ingredientModel.setIngredientName(ingredientNameList[l]);
                                        ingredientModel.setIngredientQuantity(Double.parseDouble(ingredientQuantityList[l].trim()));
                                        ingredientModel.setIngredientMsr(ingredientMeasureList[l].trim());
                                        ingredientModel.setIngredientSection(ingredientSectionList[l]);
                                        ingredientModel.setIngredientProcess(ingredientProcessList[l]);
                                        ingredientModel.setPacked(false);
                                        ingredientModelArrayList.add(ingredientModel);
                                    }
                                    allIngredientModel.setIngredientModelArrayList(ingredientModelArrayList);
                                    allIngredientModelArrayList.add(allIngredientModel);
                                }
                                orderDetailModel.setAllIngredientModelArrayList(allIngredientModelArrayList);
                                orderDetailModelArrayList.add(orderDetailModel);
                            }
                            allDetailModel.setAllOrderArrayList(orderDetailModelArrayList);
                            allDetailModelArrayList.add(allDetailModel);
                        }
                        editor.putString(Constants.ALL_ORDER_LIST, new Gson().toJson(allDetailModelArrayList));
                        editor.commit();
                        orderAdapter = new OldOrderAdapter(OldOrderActivity.this,allDetailModelArrayList);
                        orderList.setLayoutManager(new LinearLayoutManager(OldOrderActivity.this, LinearLayoutManager.VERTICAL, false));
                        orderList.setAdapter(orderAdapter);
                        orderAdapter.notifyDataSetChanged();

                    }
                    else{
                        Toast.makeText(OldOrderActivity.this, "Order List Fetching Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(OldOrderActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        }
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
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
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
                }
                break;
                case 1:
                {
                    Toast.makeText(OldOrderActivity.this, msg.getData().getString("get_state"), Toast.LENGTH_SHORT).show();
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onPause() {
        auto_start = false;
        super.onPause();
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        auto_start = true;
        super.onResume();
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
}
