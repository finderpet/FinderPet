package com.finder.pet.Main;

import android.os.Bundle;
import android.os.Handler;

import com.finder.pet.R;

import androidx.appcompat.app.AppCompatActivity;

public class CloseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },3000);
        }

    }
}
