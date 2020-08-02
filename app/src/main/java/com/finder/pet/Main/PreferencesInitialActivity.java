package com.finder.pet.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.finder.pet.Fragments.preferencesInitialFragment;
import com.finder.pet.R;
import com.google.android.material.button.MaterialButton;

public class PreferencesInitialActivity extends AppCompatActivity {

    MaterialButton btnPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_initial);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.containerPreferences, new preferencesInitialFragment()).commit();

        btnPreferences = findViewById(R.id.btnPrefer);
        btnPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PreferencesInitialActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}