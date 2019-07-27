package com.groctaurant.groctaurant.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Serial.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SerialActivity extends AppCompatActivity {

    TextView WEIGHT_DISPLAY;
    Button BTN_CLEAR_0,BTN_BIAODING_0,BTN_BIAODING_15,BTN_DEBUG,BTN_Back;
    String		InputSQM, OK, Cannel, SN;
    boolean		b_DegugFlg=false;	//false true
    int			iBiaoDingFlg = 0;

    //	final String  TTY_DEV = "/dev/ttymxc4";
    final String  TTY_DEV = "/dev/ttymxc3";
    final int 	  bps = 115200;
    SerialPort mSerialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    Auto_Weight weight_thread = new Auto_Weight();
    boolean     auto_start = true;
    int         search_interval = 800;
    public static final String TAG ="SerialActivity";

    int	 LDBD =	   0x02;
    int  RYDB =    0x03;
    int  PYGL =    0x04;
    int  ZCDQ =    0x05;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);

        find_id();
        key_things();
        init_data();
        weight_thread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        auto_start = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        auto_start = true;
        super.onResume();
    }
    private void init_data() {
        // TODO Auto-generated method stub

    }

    private void key_things() {
        // TODO Auto-generated method stub
        BTN_CLEAR_0.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //????????     aa 01 04 00 00 00 00 b0
                byte[] CMD_DATA = {(byte) 0xaa ,0x01 ,0x04 ,0x00 ,0x00 ,0x00 ,0x00 ,(byte) 0xb0 };
                iBiaoDingFlg = 1;
                print_String(CMD_DATA);

//				Toast.makeText(Scale_Activity.this, R.string.success, Toast.LENGTH_SHORT).show();
            }
        });

        BTN_BIAODING_0.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if( b_DegugFlg==true )
                {
                    byte[] CMD_DATA = {(byte) 0xaa ,0x01 ,0x02 ,0x00 ,0x00 ,0x00 ,0x00 ,(byte) 0xae };
                    iBiaoDingFlg = 2;
                    print_String(CMD_DATA);

//					Toast.makeText(Scale_Activity.this, R.string.success, Toast.LENGTH_SHORT).show();

                }
                else
                {
                    final EditText et = new EditText(SerialActivity.this);
                    new AlertDialog.Builder(SerialActivity.this).setTitle(InputSQM)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(et)
                            .setPositiveButton(OK, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    String input = et.getText().toString();
                                    if( input.equals(SN) )
                                    {

                                        byte[] CMD_DATA = {(byte) 0xaa ,0x01 ,0x02 ,0x00 ,0x00 ,0x00 ,0x00 ,(byte) 0xae };
                                        iBiaoDingFlg = 2;
                                        print_String(CMD_DATA);

//								Toast.makeText(Scale_Activity.this, R.string.success, Toast.LENGTH_SHORT).show();

                                    }
                                    else
                                    {
                                        Toast.makeText(SerialActivity.this, "No authority", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(Cannel,null)
                            .show();
                }
            }
        });

        BTN_BIAODING_15.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if( b_DegugFlg==true )
                {
                    byte[] CMD_DATA = {(byte) 0xaa ,(byte)0x01 ,(byte)0x03 ,(byte)0x0f, (byte)0x00 ,(byte) 0x05, (byte)0x00 ,(byte) 0xc3 };

                    iBiaoDingFlg = 3;
                    print_String(CMD_DATA);

                    //Toast.makeText(Scale_Activity.this, R.string.success, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final EditText et = new EditText(SerialActivity.this);
                    new AlertDialog.Builder(SerialActivity.this).setTitle(InputSQM)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(et)
                            .setPositiveButton(OK, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    String input = et.getText().toString();
                                    if( input.equals(SN) )
                                    {

                                        byte[] CMD_DATA_xww = {(byte) 0xaa ,(byte)0x01 ,(byte)0x03 ,(byte)0x0f, (byte)0x00 ,(byte) 0x05, (byte)0x00 ,(byte) 0xc3 };

                                        iBiaoDingFlg = 3;
                                        print_String(CMD_DATA_xww);

                                        //Toast.makeText(Scale_Activity.this, R.string.success, Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(SerialActivity.this, "No authority", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(Cannel,null)
                            .show();
                }
            }
        });

        BTN_DEBUG.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if( b_DegugFlg==false )
                {
                    Toast.makeText(SerialActivity.this, "No authority", Toast.LENGTH_SHORT).show();
                    return;
                }

                if( b_DegugFlg==true )
                {
                    Intent intent_page_new = new Intent();
                    intent_page_new.setClass(SerialActivity.this, LoginActivity.class);
                    startActivity(intent_page_new);
                    finish();
                }
                else
                {
                    final EditText et = new EditText(SerialActivity.this);
                    new AlertDialog.Builder(SerialActivity.this).setTitle(InputSQM)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(et)
                            .setPositiveButton(OK, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    String input = et.getText().toString();
                                    if( input.equals(SN) )
                                    {

                                        Intent intent_page_new = new Intent();
                                        intent_page_new.setClass(SerialActivity.this, LoginActivity.class);
                                        startActivity(intent_page_new);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(SerialActivity.this, "No authority", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(Cannel,null)
                            .show();
                }
            }
        });

        BTN_Back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }


    protected void print_String(byte[] prt_code_buffer) {
        try {
            byte[] buffer = prt_code_buffer;
            mOutputStream.write(buffer);
        } catch (IOException e) {
        }
    }

    private void find_id() {
        // TODO Auto-generated method stub
        WEIGHT_DISPLAY 	= 	(TextView) findViewById(R.id.date);
        BTN_BIAODING_0	=	(Button) findViewById(R.id.biaoding_0);
        BTN_BIAODING_15	=	(Button) findViewById(R.id.biaoding_15);
        BTN_DEBUG		=	(Button) findViewById(R.id.debug);
        BTN_Back		=	(Button) findViewById(R.id.back);
        BTN_CLEAR_0		=	(Button) findViewById(R.id.clear_0);

        InputSQM	=	"Input authority code";
        OK			=	"OK";
        Cannel		=	"Cannel";
        SN			=	"201721644";

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
                Log.e(TAG,"SecurityException "+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG,"IOException "+e.toString());
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

                    WEIGHT_DISPLAY.setText(msg.getData().getString("get_weight"));
                }
                break;
                case 1:
                {
                    Toast.makeText(SerialActivity.this, msg.getData().getString("get_state"), Toast.LENGTH_SHORT).show();
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void wanglei_loop(int search_interval)
    {
        try {
            Thread.sleep(search_interval);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public byte transter_byte(byte buf) {
        // TODO Auto-generated method stub
        String to_16 = Integer.toHexString(buf);
        if(to_16.length()>1){
            byte[] to_16_byte = hexStr2Bytes(to_16);
            byte to_2_byte = to_16_byte[3];
            return to_2_byte;
        }else{
            return buf;
        }
    }

    public static byte[] hexStr2Bytes(String src)
    {
        int m=0,n=0;
        int l=src.length()/2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            m=i*2+1;
            n=m+1;
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
        }
        return ret;
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

    public int getWordCount(String s)
    {
        int length = 0;
        for(int i = 0; i < s.length(); i++)
        {
            int ascii = Character.codePointAt(s, i);
            if(ascii >= 0 && ascii <=255)
                length++;
            else
                length += 2;

        }
        return length;

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

    public String e_scale_Algorithm_bak(String string, String string2, int i32BiaoDingValue, int i32FenduValue) {
        // TODO Auto-generated method stub
        double 	value = 0;
        int		i32Value, i32Tmp;
        String 	p = ""+0;
        try {
            double AD = Integer.parseInt(string,16);
            double kg_AD = Integer.parseInt(string2,16);

            value=(float) AD*i32BiaoDingValue/kg_AD*1000;//* 1000;
            i32Value = (int)value;
            i32Value = i32Value%10;
            i32Tmp = i32FenduValue/2;

            if( i32Value>i32FenduValue )
            {
                i32Value = i32Value - i32FenduValue;
            }
            value = value - i32Value;
            if( i32Value>i32Tmp )
            {
                value = value + i32FenduValue;
            }
            i32Value = (int)value;
            value = (float)i32Value * 0.001;
            DecimalFormat decimalFormat=new DecimalFormat(".000");
            p = decimalFormat.format(value);
        } catch (Exception e) {
            // TODO: handle exception
            DecimalFormat decimalFormat=new DecimalFormat(".0");
            p = decimalFormat.format(value);
        }

        return  ""+p;
    }
}


