package com.groctaurant.groctaurant.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Danish Rafique on 23-07-2018.
 */
public class AppUtil {

    public static SharedPreferences getAppPreferences(Context context){
        return context.getSharedPreferences(Constants.SETTINGS_APP_SETTINGS, Context.MODE_PRIVATE);
    }
    public static String getTimestamp(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return timeStamp;
    }
    public static String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}
