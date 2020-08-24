package com.finder.pet.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.share.Share;
import com.finder.pet.Authentication.LoginActivity;
import com.finder.pet.R;

public class SplashActivity extends AppCompatActivity {

    ImageView imgSplash;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //imgSplash=findViewById(R.id.Img_Splash);
        linearLayout=findViewById(R.id.linearSplash);

        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.anim);
        linearLayout.startAnimation(animation);

        //getSupportActionBar().hide(); //Hide the ActionBar

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();

                // Creamos una bandera para verificar si es la primera vez que entramos a la aplicación
                int flagTutorial = Integer.parseInt(preferences.getString("flagTutorial", "0"));
                if (flagTutorial==1){
                    Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    editor.putString("flagTutorial", "1");
                    editor.apply();
                    editor.commit();

                    Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);

                    Intent intent2=new Intent(SplashActivity.this, ContainerTutorialActivity.class);
                    startActivity(intent2);
                }
                finish();
            }
        },3000);
    }
}
