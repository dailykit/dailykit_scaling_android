package com.groctaurant.groctaurant.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.groctaurant.groctaurant.R;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

public class SettingActivity extends AppCompatActivity {

    EditText weightAccuracy,printingTime;
    Button saveSetting;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Switch simulatorSwitch,manualSwitch,printTestSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = AppUtil.getAppPreferences(this);
        editor = sharedpreferences.edit();
        weightAccuracy=(EditText)findViewById(R.id.weight_accuracy_edittext);
        printingTime=(EditText)findViewById(R.id.printing_time_edittext);
        saveSetting=(Button)findViewById(R.id.save_setting);
        simulatorSwitch=(Switch)findViewById(R.id.simulator_switch);
        manualSwitch=(Switch)findViewById(R.id.manual_set_weight_switch);
        printTestSwitch=(Switch)findViewById(R.id.print_test_switch);
        weightAccuracy.setText(sharedpreferences.getString(Constants.WEIGHT_ACCURACY,""));
        printingTime.setText(sharedpreferences.getString(Constants.COUNTDOWN_TO_PRINT,""));
        saveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!weightAccuracy.getText().toString().isEmpty()){
                    editor.putString(Constants.WEIGHT_ACCURACY,weightAccuracy.getText().toString());
                    editor.commit();
                }
                if(!printingTime.getText().toString().isEmpty()) {
                    editor.putString(Constants.COUNTDOWN_TO_PRINT, printingTime.getText().toString());
                    editor.commit();
                }
                SettingActivity.this.finish();
            }
        });

        if(sharedpreferences.getBoolean(Constants.IS_SIMULATED, false)){
            simulatorSwitch.setChecked(true);
        }
        else{
            simulatorSwitch.setChecked(false);
        }

        if(sharedpreferences.getBoolean(Constants.MANUAL_SET_WEIGHT, false)){
            manualSwitch.setChecked(true);
        }
        else{
            manualSwitch.setChecked(false);
        }

        if(sharedpreferences.getBoolean(Constants.PRINTER_TEST, false)){
            printTestSwitch.setChecked(true);
        }
        else{
            printTestSwitch.setChecked(false);
        }

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.simulator_switch: {
                if(simulatorSwitch.isChecked()){
                    editor.putBoolean(Constants.IS_SIMULATED,true);
                }
                else{
                    editor.putBoolean(Constants.IS_SIMULATED,false);
                }
                editor.commit();
                break;
            }

            case  R.id.manual_set_weight_switch: {
                if(manualSwitch.isChecked()){
                    editor.putBoolean(Constants.MANUAL_SET_WEIGHT,true);
                }
                else{
                    editor.putBoolean(Constants.MANUAL_SET_WEIGHT,false);
                }
                editor.commit();
                break;
            }
            case  R.id.print_test_switch: {
                if(printTestSwitch.isChecked()){
                    editor.putBoolean(Constants.PRINTER_TEST,true);
                }
                else{
                    editor.putBoolean(Constants.PRINTER_TEST,false);
                }
                editor.commit();
                break;
            }
        }
    }

}
