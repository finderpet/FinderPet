package com.finder.pet.Utilities;

import android.net.Uri;

public class Utilities {

    /**variabla para controlar el swipe**/
    public static int rotation = 0;
    public static boolean validaPantalla = true;

    /**Constantes de la tabla usuarios*/
    public static final String TABLE_USERS ="usuarios";
    public static final String FIELD_EMAIL="email";
    public static final String FIELD_NAME="Name";
    public static final String FIELD_PASSWORD="password";

    /**Constantes de la tabla platos*/
    public static final String TABLE_PLATES ="platos";
    public static final String FIELD_P_NAME="nombre";
    public static final String FIELD_P_TYPE="tipo";
    public static final String FIELD_P_PRICE="precio";
    public static final String FIELD_P_TIME="tiempo_preparacion";

    /**Constantes de la tabla foods*/
    public static final String TABLE_FOODS ="foods";
    public static final String FIELD_F_NAME="nombre";
    public static final String FIELD_F_DESCRIPTION="descripción";
    public static final String FIELD_F_IMAGE="imagen";
    public static final String FIELD_F_TYPE="tipo";
    public static final String FIELD_F_TIME="tiempo";
    public static final String FIELD_F_PRICE="precio";

    /**Constantes de la tabla bebidas*/
    public static final String TABLE_DRINKS ="bebidas";
    public static final String FIELD_D_NAME="nombre";
    public static final String FIELD_D_DESCRIPTION="descripción";
    public static final String FIELD_D_PRICE="precio";
    public static final String FIELD_D_IMAGE="imagen";

    public static final String CREATE_TABLE_USER="CREATE TABLE "+ TABLE_USERS +" ("+FIELD_EMAIL+" TEXT, "+FIELD_NAME+" TEXT, "+FIELD_PASSWORD+" TEXT)";
    public static final String CREATE_TABLE_FOODS="CREATE TABLE "+ TABLE_FOODS +" ("+FIELD_F_NAME+" TEXT NOT NULL PRIMARY KEY, "+FIELD_F_DESCRIPTION+" TEXT, "+FIELD_F_IMAGE+" TEXT, "+FIELD_F_TYPE+" TEXT, "+FIELD_F_TIME+" TEXT, "+FIELD_F_PRICE+" TEXT)";
    public static final String CREATE_TABLE_PLATES="CREATE TABLE "+ TABLE_PLATES +" ("+FIELD_P_NAME+" TEXT, "+FIELD_P_TYPE+" TEXT, "+FIELD_P_PRICE+" TEXT, "+FIELD_P_TIME+" TEXT)";
    public static final String CREATE_TABLE_DRINKS="CREATE TABLE "+ TABLE_DRINKS +" ("+FIELD_D_NAME+" TEXT, "+FIELD_D_DESCRIPTION+" TEXT, "+FIELD_D_PRICE+" TEXT, "+FIELD_D_IMAGE+" TEXT)";

    /**Variables del Perfil de Usuario*/
    public static String USER_NAME ="";
    public static String USER_EMAIL ="";
    public static Uri USER_PHOTO_URL =null;
    public static String USER_IMAGE = "";
    public static String USER_PROFILE = "close";

    /**Datos para widget**/
    public static String WG_NAME ="";
    public static String WG_TYPE ="";
    public static String WG_TIME ="";
    public static String WG_PRICE ="";

}
