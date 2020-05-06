package com.finder.pet.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.finder.pet.Authentication.LoginActivity;
import com.finder.pet.R;

public class SplashActivity extends AppCompatActivity {

    ImageView imgSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgSplash=findViewById(R.id.Img_Splash);

        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.anim);
        imgSplash.startAnimation(animation);

        //getSupportActionBar().hide(); //Hide the ActionBar

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
