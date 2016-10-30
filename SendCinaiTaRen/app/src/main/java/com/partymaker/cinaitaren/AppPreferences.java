package com.partymaker.cinaitaren;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class AppPreferences {
    private static final String MY_LAT = "MY_LAT";
    private static final String MY_LONG = "MY_LONG";
    private static final String AGREE_PERMISSION = "AGREE_PERMISSION";

    public static String getMyLat(Context context) {
//        if(true){
//            return "60.014499";
//        }
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sPref.getString(MY_LAT, null);
    }

    public static void setMyLat(Context context, String value) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor ed = sPref.edit();
        ed.putString(MY_LAT, value);
        ed.apply();
    }
    public static String getMyLong(Context context) {
//        if(true){
//            return "30.250473";
//        }
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sPref.getString(MY_LONG, null);
    }

    public static void setMyLong(Context context, String value) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor ed = sPref.edit();
        ed.putString(MY_LONG, value);
        ed.apply();
    }

    public static Boolean getAgreePermission(Context context) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sPref.getBoolean(AGREE_PERMISSION, false);
    }

    public static void setAgreePermission(Context context) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor ed = sPref.edit();
        ed.putBoolean(AGREE_PERMISSION, true);
        ed.apply();
    }
}