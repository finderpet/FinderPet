package com.finder.pet.Authentication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.finder.pet.Main.MainActivity;
import com.finder.pet.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Arrays;

public class LoginActivity extends BaseActivity {

    // Login constants and variables
    private static final String TAG1 = "EmailPassword";
    private static final String TAG2 = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private EditText mEmailField;
    private EditText mPasswordField;
    private boolean isRegister = false;

    // Register constants and variables
    private static final String TAG = "EmailPassword";
    MaterialButton btnCancelRegister, btnSaveRegister;
    private TextInputLayout mEmailTextInput, mPasswordTextInput1, mPasswordTextInput2;
    private TextInputEditText mEmailRegister, mPasswordRegister1, mPasswordRegister2;

    TextView tvDataPolicy, tvTermsUse;



    private GoogleSignInClient mGoogleSignInClient;

    // [START declare_auth]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    // [END declare_auth]

    // [Auth Facebook]
    private CallbackManager callbackManager;
    private LoginButton btnLoginFacebook;

    private NetworkInfo networkInfo;
    private AlertDialog newDialog;

    // Declare buttons
    Button btnLogin, btnRegister, btnGoogle, btnFacebook, btnRecoverPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide(); //Hide the ActionBar

        setupViews();

        // Configure Facebook Sign In
        callbackManager = CallbackManager.Factory.create();
        btnLoginFacebook.setReadPermissions(Arrays.asList("email"));
        btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                //goMainScreen();
            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, getString(R.string.error_login_facebook),Toast.LENGTH_SHORT).show();
            }
        });


        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                updateUI(user);
            }
        };

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);  // we received the google client


        // Signal Internet
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);

                if (networkInfo != null && networkInfo.isConnected()) {
                    signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                } else {
                    Intent intent=new Intent(LoginActivity.this, NoNavigationActivity.class);
                    startActivity(intent);
                }

            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (networkInfo != null && networkInfo.isConnected()) {
//                    Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
//                    startActivity(intent);
                    dialogCreateAccount().show();
                } else {
                    Intent intent=new Intent(LoginActivity.this, NoNavigationActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Iniciar Sesión con Facebook", Toast.LENGTH_SHORT).show();
            }
        });
        btnRecoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResetPassword().show();
            }
        });
        tvDataPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDataPolicy().show();
            }
        });
        tvTermsUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUseTerms().show();
            }
        });

    }// [End OnCreate]

    private void setupViews() {
        // Views
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        tvDataPolicy = findViewById(R.id.tvDataPolicy);
        tvTermsUse = findViewById(R.id.tvTermsUse);
        setProgressBar(R.id.progressBar);

        // Buttons
        btnLogin = findViewById(R.id.emailSignInButton);
        btnRegister = findViewById(R.id.btn_register_email);
        btnGoogle = findViewById(R.id.googleSingInButton);
        btnFacebook = findViewById(R.id.facebookSignInButton);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
        btnRecoverPass = findViewById(R.id.btnRecoverPassword);

        // [Iniciamos los textos con vinculos]
        tvDataPolicy.setText(Html.fromHtml(getResources().getString(R.string.link_data_policy)));
        tvTermsUse.setText(Html.fromHtml(getResources().getString(R.string.link_terms_use)));


    }

    private void goMainScreen() {
        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Method to receive the token sent by the facebook api
     * @param accessToken Receive the access token with the facebook account
     */
    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Toast.makeText(getApplicationContext(), R.string.not_firebase_auth, Toast.LENGTH_LONG).show();
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // Signal Internet

        //firebaseAuth.addAuthStateListener(firebaseAuthListener); //Podemos activar este oyente para capturar todos los cambios que vayan ocurriendo con el usuario de firebase

        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            updateUI(currentUser);
        } else {
            Intent intent=new Intent(LoginActivity.this, NoNavigationActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }

    private void signIn(String email, String password) {
        Log.d(TAG1, "signIn:" + email);
        if (!validateFormLogin()) {
            return;
        }

        showProgressBar();

        // [START sign_in_with_email]
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG1, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //updateUI(user); //organizar todos los updateUI
                            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG1, "signInWithEmail:failure", task.getException());
                            //Toast.makeText(LoginActivity.this, "Authentication falló.",Toast.LENGTH_SHORT).show();
                            dialogAuthenticationFailed().show();
                        }

                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    // [START signinGoogle]
    private void signInGoogle() {
        showProgressBar();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signinGoogle]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG2, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
        hideProgressBar();
    }// [END onactivityresult]


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG2, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressBar();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG2, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG2, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
    }// [END auth_with_google]

//    private void sendEmailVerification() {
//        // Disable button
//        findViewById(R.id.verifyEmailButton).setEnabled(false);
//
//        // Send verification email
//        // [START send_email_verification]
//        final FirebaseUser user = mAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // [START_EXCLUDE]
//                        // Re-enable button
//                        findViewById(R.id.verifyEmailButton).setEnabled(true);
//
//                        if (task.isSuccessful()) {
//                            Toast.makeText(LoginActivity.this,
//                                    "Verification email sent to " + user.getEmail(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.e(TAG, "sendEmailVerification", task.getException());
//                            Toast.makeText(LoginActivity.this,
//                                    "Failed to send verification email.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        // [END_EXCLUDE]
//                    }
//                });
//        // [END send_email_verification]
//    }



    /**
     * Method to display the create account dialog
     * @return Window dialog
     */
    public AlertDialog dialogCreateAccountSuccessful(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(R.string.account_welcome)
                .setMessage(getString(R.string.account_successfully_created) +"\n\n"+getString(R.string.account_input_email_password))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * Method to display the create account dialog
     * @return Window dialog
     */
    public AlertDialog dialogCreateAccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_register, null);
        builder.setView(view);

        // Buttons
        btnCancelRegister = view.findViewById(R.id.btnCancelRegister);
        btnSaveRegister = view.findViewById(R.id.btnRegisterUser);

        // Views
        mEmailRegister = view.findViewById(R.id.fieldRegisterEmail);
        mPasswordRegister1 = view.findViewById(R.id.fieldRegisterPassword1);
        mPasswordRegister2 = view.findViewById(R.id.fieldRegisterPassword2);
        mEmailTextInput = view.findViewById(R.id.textInputEmail);
        mPasswordTextInput1 = view.findViewById(R.id.textInputPassword1);
        mPasswordTextInput2 = view.findViewById(R.id.textInputPassword2);

        btnSaveRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(mEmailRegister.getText().toString(), mPasswordRegister1.getText().toString());
                //LoginActivity.this.newDialog.dismiss();
            }
        });
        btnCancelRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.newDialog.dismiss();
            }
        });

        return newDialog = builder.create();
    }// [End dialogCreateAccount]


    /**
     * Method to create new account
     * @param email
     * @param password
     */
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateFormRegister()) {
            return;
        }

        showProgressBar();

        // [START create_user_with_email]
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Toast.makeText(LoginActivity.this, "Registro creado correctamente", Toast.LENGTH_LONG).show();
                            dialogCreateAccountSuccessful().show();
                            Log.d(TAG, "createUserWithEmail:success");
                            LoginActivity.this.newDialog.dismiss();//Close the dialog create account

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //String msg = task.getException().getMessage();
                            //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            dialogEmailAlreadyInUse().show();
                            LoginActivity.this.newDialog.dismiss();//Close the dialog create account
                        }

                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    /**
     * Method to display Email already in use dialog
     * @return Window dialog
     */
    public AlertDialog dialogEmailAlreadyInUse(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(getResources().getString(R.string.dialog_email_already_in_use_title))
                .setMessage(getResources().getString(R.string.dialog_email_already_in_use_message))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialogCreateAccount().show();
                    }
                });
        return builder.create();
    }

    /**
     * Method to display Authentication failed dialog
     * @return Window dialog
     */
    public AlertDialog dialogAuthenticationFailed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(getResources().getString(R.string.dialog_authentication_failed_title))
                .setMessage(getResources().getString(R.string.dialog_authentication_failed_message))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    /**
     * Method to display the password recovery dialog
     * @return Window dialog
     */
    public AlertDialog dialogResetPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.recover_password, null);
        builder.setView(view);

        MaterialButton send = view.findViewById(R.id.btnSendRecover);
        MaterialButton cancel = view.findViewById(R.id.btnCancelRecover);
        final EditText emailRecover = view.findViewById(R.id.emailRecover);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser = emailRecover.getText().toString();
                if (TextUtils.isEmpty(emailUser)) {
                    emailRecover.setError(getString(R.string.required_field));
                    Toast.makeText(getApplicationContext(), R.string.recover_input_email, Toast.LENGTH_SHORT).show();
                } else {
                    emailRecover.setError(null);
                    sendPasswordReset(emailUser);
                    LoginActivity.this.newDialog.dismiss();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.newDialog.dismiss();
            }
        });

        return newDialog = builder.create();
    }// [End dialogResetPassword]

    /**
     * Method to reset or recover password
     * @param emailAddress Email of the account that you want to recover the password
     */
    public void sendPasswordReset(String emailAddress) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginActivity", "Email sent.");
                            dialogSeePasswordRecoveryMail().show();
                        }else {
                            dialogRecoveryMailNotExist().show();
                        }
                    }
                });
    }// [END send_password_reset]

    /**
     * Method to display see password recovery email dialog
     * @return Window dialog
     */
    public AlertDialog dialogSeePasswordRecoveryMail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(getResources().getString(R.string.check_email_inbox))
                .setMessage(getResources().getString(R.string.dialog_see_recovery_password_message))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    /**
     * Method to display recovery mail does not exist dialog
     * @return Window dialog
     */
    public AlertDialog dialogRecoveryMailNotExist(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(getResources().getString(R.string.dialog_recovery_mail_title))
                .setIcon(R.drawable.ic_outline_info_32)
                .setMessage(getResources().getString(R.string.dialog_recovery_mail_message))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialogResetPassword().show();
                    }
                });
        return builder.create();
    }

    /**
     * Method to validate the information in the form
     * @return Boolean with true if correct or false if there are errors
     */
    private boolean validateFormRegister() {
        boolean valid = true;

        String email = mEmailRegister.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailTextInput.setError(getString(R.string.required_field));
            valid = false;
        } else {
            mEmailTextInput.setError(null);
        }
        String password1 = mPasswordRegister1.getText().toString();
        if (TextUtils.isEmpty(password1)) {
            mPasswordTextInput1.setError(getString(R.string.required_field));
            valid = false;
        }else if (!TextUtils.isEmpty(password1) && password1.length()<8) {
            mPasswordTextInput1.setError(getString(R.string.password_length));
            valid = false;
        }  else {
            mPasswordTextInput1.setError(null);
        }
        String password2 = mPasswordRegister2.getText().toString();
        if (TextUtils.isEmpty(password2)) {
            mPasswordTextInput2.setError(getString(R.string.required_field));
            valid = false;
        } else {
            mPasswordTextInput2.setError(null);
        }
        password1 = mPasswordRegister1.getText().toString();
        password2 = mPasswordRegister2.getText().toString();
        if (!password1.equals(password2)){
            mPasswordTextInput2.setError(getString(R.string.passwords_not_match));
            valid = false;
        } else {
            mPasswordTextInput2.setError(null);
        }
        return valid;
    }

    /**
     * Method to validate login with email and password
     * @return a boolean with false or true
     */
    private boolean validateFormLogin() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.required_field));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.required_field));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    /**
     * Method to update the view of the screen with user credential
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        //hideProgressBar();
        if (user != null) {
            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
//        else {
//            Toast.makeText(LoginActivity.this, "No hay usuario autenticado",
//                    Toast.LENGTH_SHORT).show();
//        }
    }

    public static void reinitActivity(LoginActivity activity){
        Intent intent=new Intent();
        intent.setClass(activity, activity.getClass());
        //llamamos a la actividad
        activity.startActivity(intent);
        //finalizamos la actividad actual
        activity.finish();
    }

    /**
     * Method to display data policy dialog
     * @return Window dialog
     */
    public AlertDialog dialogDataPolicy(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(getResources().getString(R.string.label_data_policy))
                .setMessage(Html.fromHtml(getResources().getString(R.string.data_policy)))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    /**
     * Method to display use terms dialog
     * @return Window dialog
     */
    public AlertDialog dialogUseTerms(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(getResources().getString(R.string.label_terms_use))
                .setMessage(Html.fromHtml(getResources().getString(R.string.terms_of_use)))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

}
