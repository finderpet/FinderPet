package com.finder.pet.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesApp {

    public static final String COUNTRY = "country";
    public static final String CITY_STATE = "city_state";
    public static final String LANGUAGE = "language";
    public static final String ADVERTS = "adverts";
    public static final String ACCOUNT = "account";
    public static final String ABOUT = "about";

    public static String country;
    public static String city_state;
    public static String language;
    public static double lanDefault, lngDefault;

    public static void getPreferences(SharedPreferences preferences, Context context){

        country = preferences.getString(COUNTRY, "Colombia");
        city_state = preferences.getString(CITY_STATE, "Medellín");
        language = preferences.getString(LANGUAGE, "Español");

        if (city_state.equals("Arauca")){lanDefault = 6.667873; lngDefault = -70.994794;}
        if (city_state.equals("Armenia")){lanDefault = 4.536307; lngDefault = -75.6723751;}
        if (city_state.equals("Barranquilla")){lanDefault = 10.9799669; lngDefault = -75.573553;}
        if (city_state.equals("Bogotá")){lanDefault = 4.639217; lngDefault = -74.121491;}
        if (city_state.equals("Bucaramanga")){lanDefault = 7.1114611; lngDefault = -73.1172869;}
        if (city_state.equals("Cali")){lanDefault = 3.4517923; lngDefault = -76.5324943;}
        if (city_state.equals("Cartagena")){lanDefault = 10.4195841; lngDefault = -75.5271224;}
        if (city_state.equals("Cúcuta")){lanDefault = 7.8971458; lngDefault = -72.5080387;}
        if (city_state.equals("Florencia")){lanDefault = 1.617939 ; lngDefault = -75.604965;}
        if (city_state.equals("Ibagué")){lanDefault = 4.434871; lngDefault = -75.212356;}
        if (city_state.equals("Inírida")){lanDefault = 3.871218; lngDefault = -67.922565;}
        if (city_state.equals("Leticia")){lanDefault = -4.207246; lngDefault = -69.937824;}
        if (city_state.equals("Manizales")){lanDefault = 5.0681104; lngDefault = -75.5173198;}
        if (city_state.equals("Medellín")){lanDefault = 6.2443382; lngDefault = -75.573553;}
        if (city_state.equals("Mitú")){lanDefault = 1.2523268; lngDefault = -70.2308883;}
        if (city_state.equals("Mocoa")){lanDefault = 1.1466295; lngDefault = -76.6482327;}
        if (city_state.equals("Montería")){lanDefault = 8.7558921; lngDefault = -75.887029;}
        if (city_state.equals("Neiva")){lanDefault = 2.9263127; lngDefault = -75.2891733;}
        if (city_state.equals("Pasto")){lanDefault = 1.2146286; lngDefault = -77.2782516;}
        if (city_state.equals("Pereira")){lanDefault = 4.8142912; lngDefault = -75.6946451;}
        if (city_state.equals("Popayán")){lanDefault = 2.4420181; lngDefault = -76.6062744;}
        if (city_state.equals("Puerto Carreño")){lanDefault = 6.1879711; lngDefault = -67.4894667;}
        if (city_state.equals("Quibdó")){lanDefault = 5.6845709; lngDefault = -76.6540463;}
        if (city_state.equals("Riohacha")){lanDefault = 11.52525; lngDefault = -72.915082;}
        if (city_state.equals("San Andrés")){lanDefault = 12.577226; lngDefault = -81.709289;}
        if (city_state.equals("San José del Guaviare")){lanDefault = 2.5716141; lngDefault = -72.6426515;}
        if (city_state.equals("Santa Marta")){lanDefault = 11.2422289; lngDefault = -74.2055606;}
        if (city_state.equals("Sincelejo")){lanDefault = 9.301742; lngDefault = -75.394743;}
        if (city_state.equals("Tunja")){lanDefault = 5.5323632; lngDefault = -73.361362;}
        if (city_state.equals("Valledupar")){lanDefault = 10.4650361; lngDefault = -73.2528426;}
        if (city_state.equals("Villavicencio")){lanDefault = 4.1315113; lngDefault = -73.6206667;}
        if (city_state.equals("Yopal")){lanDefault = 5.3356662; lngDefault = -72.3936931;}

    }
}
