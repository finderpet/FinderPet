package com.finder.pet.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class PreferencesApp {

    public static final String COUNTRY = "country";
    public static final String SEARCH_RADIUS = "search_radius";
    public static final String LANGUAGE = "language";
    public static final String ADVERTS = "adverts";
    public static final String ACCOUNT = "account";
    public static final String ABOUT = "about";

    public static final String DEFAULT_LATITUDE = "latDefault";
    public static final String DEFAULT_LONGITUDE = "lngDefault";

    public static String country;
    public static int search_radius;
    public static String language;
    public static double latDefault, lngDefault;

    public static void setPreferenceLocation(SharedPreferences preferences, Context context, double lat, double lng){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DEFAULT_LATITUDE,""+lat);
        editor.putString(DEFAULT_LONGITUDE,""+lng);
        editor.apply();
        editor.commit();
    }

    public static void getPreferences(SharedPreferences preferences, Context context){

        String msjError="ok";

        try {
            latDefault = Double.parseDouble(preferences.getString(DEFAULT_LATITUDE,"6.2443382"));
        }catch (NumberFormatException e){
            msjError="LATITUDE";
        }
        try {
            lngDefault = Double.parseDouble(preferences.getString(DEFAULT_LONGITUDE,"-75.573553"));
        }catch (NumberFormatException e){
            msjError="LONGITUDE";
        }

        country = preferences.getString(COUNTRY, "Colombia");

        language = preferences.getString(LANGUAGE, "Español");

        // Search Radius
        String radius = preferences.getString(SEARCH_RADIUS, "50 Km");
        assert radius != null;
        if (radius.equals("1 Km")){
            search_radius = 1;}
        if (radius.equals("2 Km")){
            search_radius = 2;}
        if (radius.equals("3 Km")){
            search_radius = 3;}
        if (radius.equals("4 Km")){
            search_radius = 4;}
        if (radius.equals("5 Km")){
            search_radius = 5;}
        if (radius.equals("10 Km")){
            search_radius = 10;}
        if (radius.equals("20 Km")){
            search_radius = 20;}
        if (radius.equals("30 Km")){
            search_radius = 30;}
        if (radius.equals("40 Km")){
            search_radius = 40;}
        if (radius.equals("50 Km")){
            search_radius = 50;}
        if (radius.equals("100 Km")){
            search_radius = 100;}
        if (radius.equals("200 Km")){
            search_radius = 200;}
        if (radius.equals("300 Km")){
            search_radius = 300;}
        if (radius.equals("400 Km")){
            search_radius = 400;}
        if (radius.equals("500 Km")){
            search_radius = 500;}
        if (radius.equals("1000 Km")){
            search_radius = 1000;}

        if (!msjError.equals("ok")){
            Toast.makeText(context,"Verifique la configuración en "+msjError,Toast.LENGTH_LONG).show();
        }

    }
}
