package com.finder.pet.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.finder.pet.Main.SplashActivity;
import com.finder.pet.R;
import com.google.android.material.button.MaterialButton;

public class NoNavigationActivity extends AppCompatActivity {

    MaterialButton btnExit, btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_navigation);

        btnExit=findViewById(R.id.btnExitApp);
        btnContinue=findViewById(R.id.btnContinueApp);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NoNavigationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
