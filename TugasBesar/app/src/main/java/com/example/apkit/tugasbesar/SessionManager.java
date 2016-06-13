package com.example.apkit.tugasbesar;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by APKIT on 3/20/2016.
 */
public class SessionManager {
    public void setPreferences(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MayersSession", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String getPreferences(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("MayersSession", Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }
}
