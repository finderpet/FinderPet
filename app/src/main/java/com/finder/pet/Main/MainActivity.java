package com.finder.pet.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.pet.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    // [START declare_auth ]
    private FirebaseAuth firebaseAuth;
    //private GoogleSignInClient mGoogleSignInClient;

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

        // Creamos la instancia del nav_header para manejar sus objetos
        mView = navigationView.getHeaderView(0);

        circleImageView = mView.findViewById(R.id.profileImage);
        txtName = mView.findViewById(R.id.profileName);
        txtEmail = mView.findViewById(R.id.profileEmail);

        // [START declare_auth ]
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        setUserProfile(user);
        // [END declare_auth]

//        Intent intent = new Intent(MainActivity.this, TabsActivity.class);
//        startActivity(intent);

//        foundFragment = new FoundFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_host_fragment, foundFragment).addToBackStack(null).commit();

    }

    /**
     * Method to update logged in user information
     * @param user FirebaseUser containing current user information
     */
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

    // With this method we capture the event of an item in the navigation drawer
    public void onclickItem(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.closeApp) {
            Intent intent = new Intent(MainActivity.this, CloseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                          | Intent.FLAG_ACTIVITY_SINGLE_TOP
                          | Intent.FLAG_ACTIVITY_CLEAR_TASK
                          | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

//    @Override // Este método hace que de cualquier lugar de la aplicación al pulsar boton atras pregunte al usuario si quiere salir de la aplicación
//    public void onBackPressed() {
//
//        if (Utilities.CLOSE_APP == "true"){
//            final CharSequence[] opciones={"Aceptar","Cancelar"};
//            final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(this);
//            alertOpciones.setTitle("¿Desea salir de la aplicación?");
//            alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    if (opciones[i].equals("Aceptar")){
//                        //firebaseAuth.signOut();
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                | Intent.FLAG_ACTIVITY_NEW_TASK); // Cerramos sesión de usuario de firebase, de google y eliminamos la pila de actividades y fragments
//                        startActivity(intent);
//                    }else{
//                        dialogInterface.dismiss();
//                    }
//                }
//            });
//            alertOpciones.show();
//        }else {
//            onBackPressed();
//        }
//
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if ((keyCode == KeyEvent.KEYCODE_BACK))
//        {
//            //codigo adicional
//
//            final CharSequence[] opciones={"Aceptar","Cancelar"};
//            final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(this);
//            alertOpciones.setTitle("¿Desea salir de la aplicación?");
//            alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    if (opciones[i].equals("Aceptar")){
//                        //firebaseAuth.signOut();
//                        finish();
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                | Intent.FLAG_ACTIVITY_NEW_TASK); // Cerramos sesión de usuario de firebase, de google y eliminamos la pila de actividades y fragments
//                        startActivity(intent);
//                    }else{
//                        dialogInterface.dismiss();
//                    }
//                }
//            });
//            alertOpciones.show();
//        }
//        return super.onKeyDown(keyCode, event);
//    }


}
