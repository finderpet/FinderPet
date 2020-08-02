package com.finder.pet.Main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.finder.pet.Authentication.LoginActivity;
import com.finder.pet.Fragments.frequentQuestionsFragment;
import com.finder.pet.R;
import com.finder.pet.Utilities.PreferencesApp;
import com.finder.pet.Utilities.commonMethods;
import com.finder.pet.ui.help.HelpFragment;
import com.finder.pet.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private CircleImageView circleImageView;
    private TextView txtName, txtEmail;
    private FragmentTransaction transaction;
    private Fragment fragHelp, fragQuestions, fragHome;
    private ProgressDialog progressDialogQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//        //Verificamos si el acceso de facebook está activo
//        if (AccessToken.getCurrentAccessToken() == null){
//            goLoginScreen();
//        }

        // Almacenar parametros de preferencias
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //Actualizamos las preferencias de usuario
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferencesApp.getPreferences(preferences, this);
        //Validamos si es la primer vez que se abre la aplicación para solicitar País, Ciudad e Idioma
        SharedPreferences.Editor editor = preferences.edit();
        int flag = Integer.parseInt(preferences.getString("flag", "0"));
        if (flag==0){
            editor.putString("flag", "1");
            editor.commit();
            Intent intent=new Intent(MainActivity.this, PreferencesInitialActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        //navigationView.setBackgroundColor(getResources().getColor(R.color.colorAccent)); //Cambia color de fondo del navigation view

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_map, R.id.nav_advert, R.id.nav_donate, R.id.nav_info, R.id.nav_help,
                R.id.nav_account, R.id.id_ViewPager, R.id.nav_setting, R.id.nav_pqr)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        fragHelp = new HelpFragment();
        fragQuestions = new frequentQuestionsFragment();
        fragHome = new HomeFragment();

        progressDialogQuestions = new ProgressDialog(this);

        // Setup objects of HeaderView
        setupHeaderView();

        // Setup information current user Firebase
        setupAuthFirebase();
    }// [End onCreate]

    /**
     * Metodo para ir a la pantalla del login
     */
    private void goLoginScreen() {
        Intent intent=new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setupAuthFirebase() {
        // [START declare_auth ]
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        setUserProfile(user);
    }

    private void setupHeaderView() {
        // Creamos la instancia del nav_header para manejar sus objetos
        View mView = navigationView.getHeaderView(0);
        circleImageView = mView.findViewById(R.id.profileImage);
        txtName = mView.findViewById(R.id.profileName);
        txtEmail = mView.findViewById(R.id.profileEmail);
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

    // With this method we capture the event of an item in the options menu
    public void onclickItem(MenuItem item) {
        transaction = getSupportFragmentManager().beginTransaction();
        int id = item.getItemId();
        if (id == R.id.closeApp) {
            closeApp();
        }
        if (id == R.id.shareApp) {
            shareApp();
        }
        if (id == R.id.action_help) {
            createSimpleDialog();
        }
        if (id == R.id.action_pqr) {
            createSimpleDialog();
        }
    }


    /**
     * Method to close the application
     */
    private void closeApp(){
        commonMethods.dialogCloseApp(this).show();
    }

    /**
     * Method to share the application
     */
    private void shareApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String aux = "Descarga la App\n";
            //aux = aux + "https://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName();
            aux = aux + "https://play.google.com/store/apps/details?id=com.wakypetapp.cliente&hl=es_CO";
            intent.putExtra(Intent.EXTRA_TEXT, aux);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Crea un Diálogo con una lista de selección simplead
     *
     * @return La instancia del diálogo
     */
    public void createSimpleDialog() {
        final CharSequence[] opciones=new CharSequence[3];
        final String Q1,Q2,Q3;
        Q1=getResources().getString(R.string.question_1);
        Q2=getResources().getString(R.string.question_2);
        Q3=getResources().getString(R.string.question_3);
        opciones[0] = Q1;
        opciones[1] = Q2;
        opciones[2] = Q3;
        AlertDialog.Builder alertOpciones=new AlertDialog.Builder(this);
        alertOpciones.setTitle("Seleccione una pregunta")
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
        });
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals(Q1)){
                    progressDialogQuestions.setCancelable(true);
                    progressDialogQuestions.show();
                    progressDialogQuestions.setContentView(R.layout.question1);
                }
                if (opciones[i].equals(Q2)){
                    progressDialogQuestions.setCancelable(true);
                    progressDialogQuestions.show();
                    progressDialogQuestions.setContentView(R.layout.question2);
                }
                if (opciones[i].equals(Q3)){
                    progressDialogQuestions.setCancelable(true);
                    progressDialogQuestions.show();
                    progressDialogQuestions.setContentView(R.layout.question3);
                }
            }
        });
        alertOpciones.show();
    }


}
