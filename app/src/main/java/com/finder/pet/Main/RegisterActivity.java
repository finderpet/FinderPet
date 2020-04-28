package com.finder.pet.Main;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.finder.pet.Authentication.BaseActivity;
import com.finder.pet.Authentication.LoginActivity;
import com.finder.pet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "EmailPassword";
    MaterialButton btn_cancel, btn_save_register;
    private TextInputLayout mEmailTextInput, mPasswordTextInput1, mPasswordTextInput2;
    private TextInputEditText mEmailRegister, mPasswordRegister1, mPasswordRegister2;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Buttons
        btn_cancel = findViewById(R.id.btnCancelRegister);
        btn_save_register =findViewById(R.id.btnRegisterUser);
        // Views
        mEmailRegister = findViewById(R.id.fieldRegisterEmail);
        mPasswordRegister1 = findViewById(R.id.fieldRegisterPassword1);
        mPasswordRegister2 = findViewById(R.id.fieldRegisterPassword2);
        mEmailTextInput = findViewById(R.id.textInputEmail);
        mPasswordTextInput1 = findViewById(R.id.textInputPassword1);
        mPasswordTextInput2 = findViewById(R.id.textInputPassword2);

        setProgressBar(R.id.progressBar);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

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
//                Snackbar.make(view, "Aún no podemos hacer tu registro", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                createAccount(mEmailRegister.getText().toString(), mPasswordRegister1.getText().toString());

            }
        });


    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressBar();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Registro creado correctamente",
                                    Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            //Podria llamar de una vez al metodo sign in para loguear directamente al usuario despues de crear el registro
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailRegister.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailTextInput.setError("Obligatorio");
            valid = false;
        } else {
            mEmailTextInput.setError(null);
        }
        String password1 = mPasswordRegister1.getText().toString();
        if (TextUtils.isEmpty(password1)) {
            mPasswordTextInput1.setError("Obligatorio");
            valid = false;
        } else {
            mPasswordTextInput1.setError(null);
        }
        String password2 = mPasswordRegister2.getText().toString();
        if (TextUtils.isEmpty(password2)) {
            mPasswordTextInput2.setError("Obligatorio");
            valid = false;
        } else {
            mPasswordTextInput2.setError(null);
        }
        password1 = mPasswordRegister1.getText().toString();
        password2 = mPasswordRegister2.getText().toString();
        if (!password1.equals(password2)){
            mPasswordTextInput2.setError("Contraseña diferente");
            valid = false;
        } else {
            mPasswordTextInput2.setError(null);
        }
        return valid;

    }
}
