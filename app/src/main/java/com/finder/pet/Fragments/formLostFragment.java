package com.finder.pet.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.finder.pet.Entities.Lost_Vo;
import com.finder.pet.R;
import com.finder.pet.Utilities.commonMethods;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class formLostFragment extends Fragment implements OnMapReadyCallback {

    // The fragment initialization parameters
    private RadioButton rbDog, rbCat, rbOther;
    private TextInputLayout textInputLocation, textInputName, textInputChip, textInputEmail, textInputPhone, textInputDescription;
    private TextInputEditText fieldLocation, fieldNamePet, fieldEmail, fieldPhone, fieldDescription;
    private AutoCompleteTextView fieldChip;
    private ImageView img1, img2, img3, imgUpdate;
    private Uri pathLocal1, pathLocal2, pathLocal3; // Local path of the images to upload
    private String path_uri_1, path_uri_2, path_uri_3; // Path of images saved in storage firebase
    private String pathPhoto; // Photo path taken with the camera
    private double latitude, longitude;
    private int imgNumber;
    private Button btnSave;
    private Button btnConfirmLoc;
    private String searchLoc;
    private ProgressDialog progressDialog;
    private FusedLocationProviderClient fusedLocationClient; // Get the last know location
    private LinearLayout linearLayout; // LinearLayout para mostrar buscador de dirección y mapa

    //Location
    protected Location lastLocation;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private androidx.appcompat.widget.SearchView search_view;

    // Initialization Firebase
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseRef = ref.child("pet_lost");
    private StorageReference storageRef;

    //Request codes
    private static final int REQUEST_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAMERA = 103;
    private static final int REQUEST_PERMISSIONS = 104;
    private static final int REQUEST_PERMISSIONS_LOCATION = 105;

    // Constants
    private final String ROOT_FOLDER="FinderPet/";
    private final String ROUTE_IMAGE=ROOT_FOLDER+"myPhotos";

    public formLostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form_lost, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize variables
        setupViews(view);

        // Capturamos el evento de la busqueda de dirección o zona
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                manualSearchLocation();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this); // sincronizamos el mapa con los nuevos parametros

        progressDialog = new ProgressDialog(getContext());

        // Reference FirebaseStorage instance
        storageRef = FirebaseStorage.getInstance().getReference();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Validate camera permissions and external write
        if(validatePermissions()){
            img1.setEnabled(true);
            img2.setEnabled(true);
            img3.setEnabled(true);
        }else{
            img1.setEnabled(false);
            img2.setEnabled(false);
            img3.setEnabled(false);
        }
        // Upload images
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage(img1, 1);
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage(img2, 2);
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage(img3, 3);
            }
        });
        // Save new record
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                saveNewLost();
            }
        });

        fieldLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePermissionsLocation()){
                    final CharSequence[] opciones = {getString(R.string.current_location), getString(R.string.manual_location)};
                    final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getContext());
                    alertOpciones.setTitle(R.string.enter_pet_location);
                    alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (opciones[i].equals(getString(R.string.current_location))) {
                                getCurrentLocation();
                            } else {
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    alertOpciones.show();
                }
            }
        });

        // button to get user current address
        textInputLocation.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatePermissionsLocation()){
                    final CharSequence[] opciones = {getString(R.string.current_location), getString(R.string.manual_location)};
                    final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getContext());
                    alertOpciones.setTitle(R.string.enter_pet_location);
                    alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (opciones[i].equals(getString(R.string.current_location))) {
                                getCurrentLocation();
                            } else {
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    alertOpciones.show();
                }
            }
        });

        // Button to confirm manual address
        btnConfirmLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchLocation();
            }
        });
    }

    /**
     * Method to find the location manually
     */
    private void manualSearchLocation() {
        String location = search_view.getQuery().toString();
        searchLoc = location;
        List<Address> addressList = null;
        if (location != null) {
            Geocoder geocoder = new Geocoder(getContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                map.addMarker(new MarkerOptions().position(latLng).title(location)).showInfoWindow();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            } else {
                searchLoc = ""; // Clear the variable with the address or area searched
                Toast.makeText(getContext(), R.string.location_not_found, Toast.LENGTH_LONG).show();
            }
        }
    }// [End manualSearchLocation]

    /**
     * Method to initialize the views
     * @param view View fragment
     */
    private void setupViews(View view) {

        btnConfirmLoc = view.findViewById(R.id.btnConfirmLocLost);
        linearLayout = view.findViewById(R.id.layoutSearchLost);
        search_view = view.findViewById(R.id.svMapsLost);
        mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.mapSearchLost);

        rbDog = view.findViewById(R.id.rbDogFound);
        rbCat = view.findViewById(R.id.rbCatFound);
        rbOther = view.findViewById(R.id.rbOtherFound);
        textInputLocation = view.findViewById(R.id.textInputAddLostLocation);
        textInputName = view.findViewById(R.id.textInputAddLostName);
        textInputChip = view.findViewById(R.id.textInputAddLostChip);
        textInputEmail = view.findViewById(R.id.textInputAddLostEmail);
        textInputPhone = view.findViewById(R.id.textInputAddLostPhone);
        textInputDescription = view.findViewById(R.id.textInputAddLostDescription);
        fieldLocation = view.findViewById(R.id.fieldAddLostLocation);
        fieldNamePet = view.findViewById(R.id.fieldAddLostName);
        fieldChip = view.findViewById(R.id.fieldAddLostChip);
        fieldEmail = view.findViewById(R.id.fieldAddLostEmail);
        fieldPhone = view.findViewById(R.id.fieldAddLostPhone);
        fieldDescription = view.findViewById(R.id.fieldAddLostDescription);
        img1 = view.findViewById(R.id.imageLost_1);
        img2 = view.findViewById(R.id.imageLost_2);
        img3 = view.findViewById(R.id.imageLost_3);
        btnSave = view.findViewById(R.id.btnAddNewLost);
        pathLocal1=null;
        pathLocal2=null;
        pathLocal3=null;
        path_uri_1="null";
        path_uri_2="null";
        path_uri_3="null";
        latitude = 6.2443382;
        longitude = -75.573553;


        String[] Types = new String[]{getString(R.string.btn_yes), getString(R.string.btn_no)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_type_item, Types);
        fieldChip.setAdapter(adapter);
    }

    /**
     * Method to open manual address search
     */
    private void getSearchLocation(){
        fieldLocation.setText(searchLoc);
        linearLayout.setVisibility(View.GONE);
    }

    /**
     * Method to get user current location
     */
    private void getCurrentLocation() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if((getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    && (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
                //Get last location
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                lastLocation = location;

                                // In some rare cases the location returned can be null
                                if (lastLocation == null) {
                                    Toast.makeText(getContext(), R.string.location_not_found, Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (!Geocoder.isPresent()) {
                                    Toast.makeText(getContext(),R.string.no_geocoder_available,Toast.LENGTH_LONG).show();
                                    return;
                                }

                                // coordinates for the location on the map
                                latitude = lastLocation.getLatitude();
                                longitude = lastLocation.getLongitude();

                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                String errorMessage = "";

                                List<Address> addresses = null;

                                try {
                                    // Get address of our location
                                    addresses = geocoder.getFromLocation(
                                            lastLocation.getLatitude(),
                                            lastLocation.getLongitude(),
                                            // In this sample, get just a single address.
                                            1);
                                } catch (IOException ioException) {
                                    // Catch network or other I/O problems.
                                    errorMessage = getString(R.string.service_not_available);
                                    Log.e("LastLocationAPI22", errorMessage, ioException);
                                } catch (IllegalArgumentException illegalArgumentException) {
                                    // Catch invalid latitude or longitude values.
                                    errorMessage = getString(R.string.invalid_lat_long_used);
                                    Log.e("LastLocationAPI22", errorMessage + ". " +
                                            "Latitude = " + lastLocation.getLatitude() +
                                            ", Longitude = " +
                                            lastLocation.getLongitude(), illegalArgumentException);
                                }

                                // Handle case where no address was found.
                                if (addresses == null || addresses.size()  == 0) {
                                    if (errorMessage.isEmpty()) {
                                        errorMessage = getString(R.string.location_not_found);
                                        Log.e("LastLocationAPI22", errorMessage);
                                        Toast.makeText(getContext(), R.string.location_not_found, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Address address = addresses.get(0);
                                    String myAddress = address.getAddressLine(0);
                                    //Toast.makeText(getContext(), myAddress, Toast.LENGTH_LONG).show();
                                    fieldLocation.setText(myAddress);
                                    Log.i("LastLocationAPI22", getString(R.string.location_found));
                                }

                            }
                        });
            }else {
                if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) || shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
                    dialogRecommendationToGrantPermissionLocation();
                }
                requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_LOCATION);
            }
        }else {
            //Get last location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            lastLocation = location;

                            // In some rare cases the location returned can be null
                            if (lastLocation == null) {
                                Toast.makeText(getContext(), R.string.location_not_found, Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (!Geocoder.isPresent()) {
                                Toast.makeText(getContext(),R.string.no_geocoder_available,Toast.LENGTH_LONG).show();
                                return;
                            }

                            // coordinates for the location on the map
                            latitude = lastLocation.getLatitude();
                            longitude = lastLocation.getLongitude();

                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            String errorMessage = "";

                            List<Address> addresses = null;

                            try {
                                // Get address of our location
                                addresses = geocoder.getFromLocation(
                                        lastLocation.getLatitude(),
                                        lastLocation.getLongitude(),
                                        // In this sample, get just a single address.
                                        1);
                            } catch (IOException ioException) {
                                // Catch network or other I/O problems.
                                errorMessage = getString(R.string.service_not_available);
                                Log.e("LastLocationAPI22", errorMessage, ioException);
                            } catch (IllegalArgumentException illegalArgumentException) {
                                // Catch invalid latitude or longitude values.
                                errorMessage = getString(R.string.invalid_lat_long_used);
                                Log.e("LastLocationAPI22", errorMessage + ". " +
                                        "Latitude = " + lastLocation.getLatitude() +
                                        ", Longitude = " +
                                        lastLocation.getLongitude(), illegalArgumentException);
                            }

                            // Handle case where no address was found.
                            if (addresses == null || addresses.size()  == 0) {
                                if (errorMessage.isEmpty()) {
                                    errorMessage = getString(R.string.location_not_found);
                                    Log.e("LastLocationAPI22", errorMessage);
                                    Toast.makeText(getContext(), R.string.location_not_found, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Address address = addresses.get(0);
                                String myAddress = address.getAddressLine(0);
                                //Toast.makeText(getContext(), myAddress, Toast.LENGTH_LONG).show();
                                fieldLocation.setText(myAddress);
                                Log.i("LastLocationAPI22", getString(R.string.location_found));
                            }

                        }
                    });

        }

    }// [End getCurrentLocation]

    /**
     * Method to display the new record progress dialog
     */
    private void showProgressDialog(){
        progressDialog.setCancelable(false);
        //progressDialog.setTitle("Guardando nuevo registro..."); // usamos esta linea cuando no utilizamos el setContenView
        progressDialog.show();
        progressDialog.setContentView(R.layout.layout_pdialog); // No usamos esta linea cuando usamos setTitle
    };

    /**
     * Method to save the new record in the database
     */
    private void saveNewLost() {

        // Validates that the form has all the required fields
        if (!validateForm()) {
            progressDialog.dismiss();
            return;
        }
        fillEmptyFields();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //getting the values to save
                String rbPet="null";
                if (rbDog.isChecked()) rbPet = "dog";
                if (rbCat.isChecked()) rbPet = "cat";
                if (rbOther.isChecked()) rbPet = "other";
                // Check user not null
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String idUser;
                if (user != null){
                    idUser = user.getUid();
                    Log.i("Id current user", idUser);
                }else {
                    Toast.makeText(getContext(), getString(R.string.you_must_login_again), Toast.LENGTH_LONG).show();
                    return;
                }
                String date = commonMethods.getDateTime();
                String name = fieldNamePet.getText().toString().trim();
                String microchip = fieldChip.getText().toString().trim();
                String email = fieldEmail.getText().toString().trim();
                String type = rbPet;
                String description = fieldDescription.getText().toString().trim();
                String phone = fieldPhone.getText().toString().trim();
                String img_1 = path_uri_1;
                String img_2 = path_uri_2;
                String img_3 = path_uri_3;
                String location = fieldLocation.getText().toString().trim();

                //getting a unique id using push().getKey() method
                //it will create a unique id and we will use it as the Primary Key for our user
                String id = databaseRef.push().getKey();

                //creating an lost pet Object
                Lost_Vo lost_vo = new Lost_Vo(idUser, date, name, microchip, email, type, description, phone, img_1, img_2, img_3, location, latitude, longitude);

                //Saving the lost pet
                databaseRef.child(id).setValue(lost_vo);

                //setting edittext to blank again
                clearFields();

                progressDialog.dismiss();
                //displaying a success toast
                Toast.makeText(getContext(), R.string.post_successful, Toast.LENGTH_LONG).show();

            }
        },12000);

    }

    private void fillEmptyFields() {
        String email = fieldEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            //email = getResources().getString(R.string)
            fieldEmail.setText("No info");
        } else {
            textInputEmail.setError(null);
        }
        String description = fieldDescription.getText().toString();
        if (TextUtils.isEmpty(description)) {
            fieldDescription.setText("No info");
        } else {
            textInputDescription.setError(null);
        }
    }

    /**
     * Method to clear form fields
     */
    private void clearFields() {
        fieldNamePet.setText("");
        fieldLocation.setText("");
        fieldPhone.setText("");
        fieldEmail.setText("");
        fieldDescription.setText("");
        img1.setImageResource(R.drawable.img_upload_1);
        img2.setImageResource(R.drawable.img_upload_2);
        img3.setImageResource(R.drawable.img_upload_3);
        latitude = 6.2443382;
        longitude = -75.573553;
    }

    /**
     * Method to validate the information in the form
     * @return Boolean with true if correct or false if there are errors
     */
    private boolean validateForm() {
        boolean valid = true;

        if (!rbDog.isChecked() && !rbCat.isChecked() && !rbOther.isChecked()){
            Toast.makeText(getContext(), R.string.select_pet_type, Toast.LENGTH_LONG).show();
            valid = false;
        }
        if (pathLocal1 == null){
            Toast.makeText(getContext(), R.string.add_least_image1, Toast.LENGTH_LONG).show();
            valid = false;
        }
        String location = fieldLocation.getText().toString();
        if (TextUtils.isEmpty(location)) {
            textInputLocation.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputLocation.setError(null);
        }
        String name = fieldNamePet.getText().toString();
        if (TextUtils.isEmpty(name)) {
            textInputName.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputName.setError(null);
        }
        String phone = fieldPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            textInputPhone.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputPhone.setError(null);
        }
//        String description = fieldDescription.getText().toString();
//        if (TextUtils.isEmpty(description)) {
//            textInputDescription.setError("La descripción es Obligatoria");
//            valid = false;
//        } else {
//            textInputDescription.setError(null);
//        }
//        String email = fieldEmail.getText().toString();
//        if (TextUtils.isEmpty(email)) {
//            textInputEmail.setError("Correo Obligatorio");
//            valid = false;
//        } else {
//            textInputEmail.setError(null);
//        }
        return valid;
    }

    /**
     * Method to validate camera and external write permissions
     * @return Boolean with true if granted permissions or false if not
     */
    private boolean validatePermissions() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((getContext().checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)
                && (getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA))
                || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            dialogRecommendationToGrantPermissionCameraStorage();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},104);
        }
        return false;
    }

    /**
     * Method to validate camera and external write permissions
     * @return Boolean with true if granted permissions or false if not
     */
    private boolean validatePermissionsLocation() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((getContext().checkSelfPermission(ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                && (getContext().checkSelfPermission(ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION))
                || (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION))){
            dialogRecommendationToGrantPermissionLocation();
        }else{
            requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},105);
        }
        return false;
    }

    /**
     * Method to display camera and storage permission recommendation dialog
     */
    private void dialogRecommendationToGrantPermissionCameraStorage() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getActivity());
        dialogo.setTitle(R.string.camera_video_permissions_not_granted);
        dialogo.setMessage(R.string.accept_permissions_functioning_app);

        dialogo.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},104);
            }
        });
        dialogo.show();
    }

    /**
     * Method to display location permission recommendation dialog
     */
    private void dialogRecommendationToGrantPermissionLocation() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getActivity());
        dialogo.setTitle(R.string.location_permissions_not_granted);
        dialogo.setMessage(R.string.accept_permissions_functioning_app);

        dialogo.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},105);
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_PERMISSIONS){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                img1.setEnabled(true);
                img2.setEnabled(true);
                img3.setEnabled(true);
            }else{
                manualPermitRequest().show();
            }
        }
        if(requestCode== REQUEST_PERMISSIONS_LOCATION){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(), R.string.get_current_location_automatically,Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }else{
                //Toast.makeText(getContext(), "Permission was not granted",Toast.LENGTH_SHORT).show();
                manualPermitRequest().show();
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Method to display permission authorization dialog manually
     */
    private AlertDialog manualPermitRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_permissions_manually)
                .setMessage(R.string.accept_permissions_functioning_app)
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri=Uri.fromParts("package",getActivity().getPackageName(),null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),R.string.permission_not_accepted,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * Method to upload image or take photo of the pet
     * @param imgNew ImageView to display the picture
     * @param imgNum Image path identifier
     */
    private void loadImage(ImageView imgNew, int imgNum) {
        imgNumber = imgNum; // Asigno el número que identifica la ruta de la imagen que estamos subiendo
        imgUpdate = imgNew; // Asigno a imgUpdate el ImageView que solicitó cargar la imagen
        final CharSequence[] options = {getString(R.string.upload_from_gallery),
                getString(R.string.take_photo), getString(R.string.cancel)};
        final AlertDialog.Builder alertOptions = new AlertDialog.Builder(getActivity());
        alertOptions.setTitle(R.string.select_an_option);
        alertOptions.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(options[i].equals(getString(R.string.take_photo))){
                    makePhoto();
                }else {
                    if(options[i].equals(getString(R.string.upload_from_gallery))){
                        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");//si hay problemas para cargar algunas imagenes colocar "image/*"
                        startActivityForResult(intent.createChooser(intent, getString(R.string.select_an_application)), 100);
                    }else {
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOptions.show();
    }

    /**
     * Method to take the photo with the camera
     */
    private void makePhoto() {
        File fileImage=new File(Environment.getExternalStorageDirectory(),ROUTE_IMAGE);
        boolean isMake=fileImage.exists();
        String nameImage="";
        if(!isMake){
            isMake=fileImage.mkdirs();
        }
        if(isMake){
            nameImage=(System.currentTimeMillis()/1000)+".jpg";
        }

        pathPhoto=Environment.getExternalStorageDirectory()+
                File.separator+ROUTE_IMAGE+File.separator+nameImage;

        File newImage=new File(pathPhoto);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            String authorities=getContext().getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(getContext(),authorities,newImage);
            if (imgNumber==1) pathLocal1=imageUri;
            if (imgNumber==2) pathLocal2=imageUri;
            if (imgNumber==3) pathLocal3=imageUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else{
            if (imgNumber==1) pathLocal1=Uri.fromFile(newImage);
            if (imgNumber==2) pathLocal2=Uri.fromFile(newImage);
            if (imgNumber==3) pathLocal3=Uri.fromFile(newImage);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newImage));
        }
        startActivityForResult(intent,103);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode== RESULT_OK){
            switch (requestCode) {
                case REQUEST_IMAGE:
                    try {
                        Uri path=data.getData();
                        imgUpdate.setImageURI(path);
                        if (imgNumber==1){
                            pathLocal1=path;
                            StorageReference filePath1 = storageRef.child("pets")
                                    .child(pathLocal1.getLastPathSegment()); //Referenciamos donde quedará guardada nuestra imagen
                            filePath1.putFile(pathLocal1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful());
                                    path_uri_1 = urlTask.getResult().toString();
                                }
                            });
                        }
                        if (imgNumber==2){
                            pathLocal2=path;
                            StorageReference filePath2 = storageRef.child("pets")
                                    .child(pathLocal2.getLastPathSegment()); //Referenciamos donde quedará guardada nuestra imagen
                            filePath2.putFile(pathLocal2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful());
                                    path_uri_2 = urlTask.getResult().toString();
                                }
                            });
                        }
                        if (imgNumber==3){
                            pathLocal3=path;
                            StorageReference filePath3 = storageRef.child("pets")
                                    .child(pathLocal3.getLastPathSegment()); //Referenciamos donde quedará guardada nuestra imagen
                            filePath3.putFile(pathLocal3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful());
                                    path_uri_3 = urlTask.getResult().toString();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_IMAGE_CAMERA:
                    try {
                        MediaScannerConnection.scanFile(getActivity(), new String[]{pathPhoto}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("Storage path","Path: "+path);
                                    }
                                });
                        Bitmap bitmap= BitmapFactory.decodeFile(pathPhoto);
                        imgUpdate.setImageBitmap(bitmap);
                        if (imgNumber==1){
                            //Toast.makeText(getContext(),"Path local 1: "+pathLocal1,Toast.LENGTH_SHORT).show();
                            StorageReference filePath1 = storageRef.child("pets")
                                    .child(pathLocal1.getLastPathSegment()); //Referenciamos donde quedará guardada nuestra imagen
                            filePath1.putFile(pathLocal1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful());
                                    path_uri_1 = urlTask.getResult().toString();
                                }
                            });
                        }
                        if (imgNumber==2){
                            StorageReference filePath2 = storageRef.child("pets")
                                    .child(pathLocal2.getLastPathSegment()); //Referenciamos donde quedará guardada nuestra imagen
                            filePath2.putFile(pathLocal2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful());
                                    path_uri_2 = urlTask.getResult().toString();
                                }
                            });
                        }
                        if (imgNumber==3){
                            StorageReference filePath3 = storageRef.child("pets")
                                    .child(pathLocal3.getLastPathSegment()); //Referenciamos donde quedará guardada nuestra imagen
                            filePath3.putFile(pathLocal3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful());
                                    path_uri_3 = urlTask.getResult().toString();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }else {
            Toast.makeText(getContext(),R.string.image_not_loaded, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        final LatLng medellin = new LatLng(6.2443382, -75.573553);
        //map.addMarker(new MarkerOptions().position(medellin).title("Medellín")).showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(medellin, 12));
//        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                Toast.makeText(getContext(),"Medellín Antioquia", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

}