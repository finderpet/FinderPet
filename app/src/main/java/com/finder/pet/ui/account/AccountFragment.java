package com.finder.pet.ui.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.finder.pet.Authentication.LoginActivity;
import com.finder.pet.R;
import com.finder.pet.Utilities.commonMethods;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;

    // [START declare_auth ]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    Button btnSgnOut, btnDeleteAccount, btnUpdateUser;
    ImageButton btnUpdateProfile;
    private TextInputEditText txtName,txtEmail, txtPhone;
    private CircleImageView imgPhoto;
    private static final String TAG = "Update user";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);
        return inflater.inflate(R.layout.fragment_account, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtName = view.findViewById(R.id.txtProfileName);
        txtEmail = view.findViewById(R.id.txtProfileEmail);
        txtPhone = view.findViewById(R.id.txtProfilePhone);
        imgPhoto = view.findViewById(R.id.imgAccount);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);


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
        btnUpdateUser = view.findViewById(R.id.btnUpdateUser);
        btnUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();

            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtName.setEnabled(true);
                txtPhone.setEnabled(true);
                txtName.setTextColor(getResources().getColor(R.color.colorBlack));
                txtPhone.setTextColor(getResources().getColor(R.color.colorBlack));
                btnUpdateUser.setVisibility(View.VISIBLE);
            }
        });
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

    public void updateProfile() {
        // [START update_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Johnnatan Velasquez")
                .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/finderpet-2cd1d.appspot.com/o/users%2Ffoto%20perfil.PNG?alt=media&token=682dbe24-5f5a-4d3b-a76c-8fc538cf5c58"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Toast.makeText(getContext(), R.string.updated_profile,Toast.LENGTH_LONG).show();
                        }
                    }
                });
        txtName.setEnabled(false);
        txtPhone.setEnabled(false);
        txtName.setTextColor(getResources().getColor(R.color.colorLetterHint));
        txtPhone.setTextColor(getResources().getColor(R.color.colorLetterHint));
        btnUpdateUser.setVisibility(View.VISIBLE);
        btnUpdateUser.setVisibility(View.GONE);
        // [END update_profile]
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