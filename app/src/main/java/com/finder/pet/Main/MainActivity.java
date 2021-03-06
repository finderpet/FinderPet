package com.finder.pet.Main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.pet.R;
import com.finder.pet.Utilities.PreferencesApp;
import com.finder.pet.Utilities.commonMethods;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.navigation.Navigation.findNavController;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private CircleImageView circleImageView;
    private TextView txtName, txtEmail;
    private ProgressDialog progressDialogQuestions;

    //Consent Europe
//    private ConsentInformation consentInformation;
//    private ConsentForm consentForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Store preference parameters
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferencesApp.getPreferences(preferences, this);

//        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();

        //Solicitar consentimiento para Unión Europea
//        consentInformation = UserMessagingPlatform.getConsentInformation(this);
//        consentInformation.requestConsentInfoUpdate(
//                this,
//                params,
//                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
//                    @Override
//                    public void onConsentInfoUpdateSuccess() {
//                        // The consent information state was updated.
//                        // You are now ready to check if a form is available.
//                        if (consentInformation.isConsentFormAvailable()) {
//                            loadForm();
//                        }
//                    }
//                },
//                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
//                    @Override
//                    public void onConsentInfoUpdateFailure(FormError formError) {
//                        // Handle the error.
//                    }
//                });

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });

        createNotificationChannel();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        //navigationView.setBackgroundColor(getResources().getColor(R.color.colorAccent)); //Cambia color de fondo del navigation view

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_map, R.id.nav_advert, R.id.nav_donate, R.id.nav_info, R.id.nav_help,
                R.id.nav_account, R.id.nav_setting, R.id.nav_pqr)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        progressDialogQuestions = new ProgressDialog(this);

        // Setup objects of HeaderView
        setupHeaderView();

    }// [End onCreate]

//    public void loadForm(){
//        UserMessagingPlatform.loadConsentForm(
//                this,
//                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
//                    @Override
//                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
//                        MainActivity.this.consentForm = consentForm;
//                        if(consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
//                            consentForm.show(
//                                    MainActivity.this,
//                                    new ConsentForm.OnConsentFormDismissedListener() {
//                                        @Override
//                                        public void onConsentFormDismissed(@Nullable FormError formError) {
//                                            // Handle dismissal by reloading form.
//                                            loadForm();
//                                        }
//                                    });
//
//                        }
//
//                    }
//                },
//                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
//                    @Override
//                    public void onConsentFormLoadFailure(FormError formError) {
//                        /// Handle Error.
//                    }
//                }
//        );
//    }

    /**
     * Method to update user information at the nav view
     */
    public void setupHeaderView() {
        // Creamos la instancia del nav_header para manejar sus objetos
        View mView = navigationView.getHeaderView(0);

        circleImageView = mView.findViewById(R.id.profileImage);
        txtName = mView.findViewById(R.id.profileName);
        txtEmail = mView.findViewById(R.id.profileEmail);
        //get firebase user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user!=null){
            txtName.setText(user.getDisplayName());
            txtEmail.setText(user.getEmail());
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.img_profile)
                    .into(circleImageView);
        }
    }// [End setupHeaderView]

    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_notifications);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Default", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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

    /**
     * Method to capture the event of an item in the options menu
     * @param item Menu item
     */
    public void onclickItem(MenuItem item) {
        //transaction = getSupportFragmentManager().beginTransaction();
        int id = item.getItemId();
        if (id == R.id.closeApp) {
            closeApp();
        }
        if (id == R.id.shareApp) {
            shareApp();
        }
        if (id == R.id.nav_problem) {
            //El item debe tener el mismo id que tiene el fragment en el mobile_navigation
            NavigationUI.onNavDestinationSelected(item,findNavController(this, R.id.nav_host_fragment));
        }
        if (id == R.id.action_pqr) {
            dialogFrequentQuestions();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

//        if (NavigationUI.onNavDestinationSelected(item, Navigation.findNavController(this, R.id.nav_host_fragment))) {
//            return true;
//        }else {
//            return super.onOptionsItemSelected(item);
//        }
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
            aux = aux + "https://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName();
            //aux = aux + "https://play.google.com/store/apps/details?id=com.wakypetapp.cliente&hl=es_CO";
            intent.putExtra(Intent.EXTRA_TEXT, aux);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method to display frequent questions dialog
     */
    public void dialogFrequentQuestions() {
        final CharSequence[] opciones=new CharSequence[3];
        final String Q1,Q2,Q3;
        Q1=getResources().getString(R.string.question_1);
        Q2=getResources().getString(R.string.question_2);
        Q3=getResources().getString(R.string.question_3);
        opciones[0] = Q1;
        opciones[1] = Q2;
        opciones[2] = Q3;
        AlertDialog.Builder alertOpciones=new AlertDialog.Builder(this);
        alertOpciones.setTitle(R.string.select_a_question)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
