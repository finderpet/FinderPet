package com.finder.pet.ui.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.finder.pet.Authentication.LoginActivity;
import com.finder.pet.Main.CloseActivity;
import com.finder.pet.R;
import com.finder.pet.Utilities.commonMethods;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;

    // [START declare_auth ]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    private Button btnSgnOut, btnDeleteAccount;
    private TextView txtName, txtEmail, txtPhone;
    private CircleImageView imgPhoto;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        txtName = view.findViewById(R.id.accountName);
        txtEmail = view.findViewById(R.id.accountEmail);
        txtPhone = view.findViewById(R.id.accountPhone);
        imgPhoto = view.findViewById(R.id.imgAccount);

        firebaseAuth = FirebaseAuth.getInstance();

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);  // we received the google client

        FirebaseUser user = firebaseAuth.getCurrentUser();
        setUserData(user);

        btnSgnOut = view.findViewById(R.id.btnSignOut);
        btnSgnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signOutUser();
                commonMethods.dialogSignOutUser(getContext()).show();
            }
        });

        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonMethods.dialogDeleteAccount(getContext()).show();
            }
        });

        return view;
    }


    private void setUserData(FirebaseUser user) {
        txtName.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(user.getPhoneNumber());
        Glide.with(getContext())
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.img_profile)
                .into(imgPhoto);
    }

    @Override
    public void onStart() {
        super.onStart();
        //firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    private void goLogInScreen() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}