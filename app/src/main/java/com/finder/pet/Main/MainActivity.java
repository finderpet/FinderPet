package com.finder.pet.Main;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.finder.pet.R;

import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    // [START declare_auth ]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    private CircleImageView circleImageView;
    private TextView txtName, txtEmail;
    View mView; // este view es para crear la instancia del nav_header

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //navigationView.setBackgroundColor(getResources().getColor(R.color.colorAccent)); //Cambia color de fondo del navigation view
//        NavigationView navigationView2 = findViewById(R.id.nav_view);
//        navigationView2.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_tools, R.id.nav_share,
                R.id.nav_account, R.id.id_ViewPager, R.id.nav_slideshow, R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mView = navigationView.getHeaderView(0); // Creamos la instancia del nav_header

        circleImageView = mView.findViewById(R.id.profileImage);
        txtName = mView.findViewById(R.id.profileName);
        txtEmail = mView.findViewById(R.id.profileEmail);

        firebaseAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);  // we received the google client
        FirebaseUser user = firebaseAuth.getCurrentUser();
        setUserProfile(user);

//        Intent intent = new Intent(MainActivity.this, TabsActivity.class);
//        startActivity(intent);

//        foundFragment = new FoundFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_host_fragment, foundFragment).addToBackStack(null).commit();

    }

    private void setUserProfile(FirebaseUser user) {
        txtName.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());
        Glide.with(this)
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.img_profile)
                .into(circleImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


//    // Este metodo no esta funcionando para capturar un evento de los items del navigation view
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        int title;
//        switch (menuItem.getItemId()) {
//
//            case R.id.nav_account:
//                title = R.string.menu_send;
//                Toast.makeText(MainActivity.this, "Probando Menuitem",
//                        Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                throw new IllegalArgumentException("menu option not implemented!!");
//        }
//
//
//        return true;
//    }
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }

}
