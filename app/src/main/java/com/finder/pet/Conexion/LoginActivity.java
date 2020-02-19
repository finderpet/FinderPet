package com.finder.pet.Conexion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.finder.pet.Main.MainActivity;
import com.finder.pet.Main.RegisterActivity;
import com.finder.pet.R;

public class LoginActivity extends AppCompatActivity {

    Button btn, btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide(); //Hide the ActionBar

        btn = findViewById(R.id.email_sign_in_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnRegister = findViewById(R.id.btn_register_email);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Fragment miFrag = new RegisterFragment();
                //getSupportFragmentManager().beginTransaction()
                        //.replace(R.id.loginActivity, miFrag).addToBackStack(null).commit();

                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}
