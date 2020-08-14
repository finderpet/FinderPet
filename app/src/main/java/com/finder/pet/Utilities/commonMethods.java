package com.finder.pet.Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.facebook.login.LoginManager;
import com.finder.pet.Main.CloseActivity;
import com.finder.pet.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class commonMethods {

    /**
     * Method that returns the number of days that have elapsed since publication
     * @param datePost Publication date
     * @param context Activity context
     * @return A string with the elapsed time since publication
     */
    public static String getDaysDate(String datePost, Context context){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String dateNow = getDateTime();

        Date initialDate= null;
        Date finalDate= null;
        try {
            initialDate = dateFormat.parse(datePost);
            finalDate = dateFormat.parse(dateNow);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int days =(int) ((finalDate.getTime()-initialDate.getTime())/86400000);
        String txtDays="";
        if (days==0){txtDays = context.getString(R.string.today);}
        if (days==1){ txtDays = context.getString(R.string.time_ago)+days+context.getString(R.string.time_day); }
        if (days>1 && days<30){ txtDays = context.getString(R.string.time_ago)+days+context.getString(R.string.time_days); }
        if (days>=30 && days<60){ txtDays = context.getString(R.string.time_ago_one_month); }
        if (days>=50){
            int months = days/30;
            txtDays = context.getString(R.string.time_ago)+months+context.getString(R.string.time_months);
        }
        return txtDays;
    }

    /**
     * Method to return the current date
     * @return A string with the current date
     */
    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Method to display delete account dialog
     * @param context Activity context
     * @return Window dialog
     */
    public static AlertDialog dialogDeleteAccount(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.dialog_delete_account_title)
                .setMessage(R.string.dialog_delete_account_message)
                .setPositiveButton(Html.fromHtml(context.getString(R.string.btn_delete)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Firebase delet user", "User account deleted.");
                                        }
                                    }
                                });
                        LoginManager.getInstance().logOut(); //Cerramos sesión de facebook
                        Intent intent = new Intent(context, CloseActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK); // Cerramos sesión de usuario de firebase, de google y eliminamos la pila de actividades y fragments
                        intent.putExtra("EXIT", true);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
            }
        });

        return builder.create();
    }

    /**
     * Method to display singOut account dialog
     * @param context Activity context
     * @return Window dialog
     */
    public static AlertDialog dialogSignOutUser(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);  // we received the google client

        builder.setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut(); //Cerramos sesión de firebase
                        mGoogleSignInClient.revokeAccess(); //Cerramos sesión de google
                        LoginManager.getInstance().logOut(); //Cerramos sesión de facebook
                        Intent intent = new Intent(context, CloseActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK); // Cerramos sesión de usuario de firebase, de google y eliminamos la pila de actividades y fragments
                        intent.putExtra("EXIT", true);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * Method to display close app dialog
     * @param context Activity context
     * @return Window dialog
     */
    public static AlertDialog dialogCloseApp(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.dialog_close_app)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, CloseActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK); // Eliminamos la pila de actividades y fragments
                        intent.putExtra("EXIT", true);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

}
