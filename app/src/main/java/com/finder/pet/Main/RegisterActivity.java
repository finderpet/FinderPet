package com.finder.pet.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.finder.pet.Authentication.LoginActivity;
import com.finder.pet.R;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {

    Button btn_cancel, btn_save_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Buttons
        btn_cancel = findViewById(R.id.cancel_btn_register);
        btn_save_register =findViewById(R.id.register_button);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_save_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Aún no podemos hacer tu registro", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }
}
