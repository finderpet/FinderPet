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

import com.finder.pet.Entities.Adopted_Vo;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
public class formAdoptedFragment extends Fragment implements OnMapReadyCallback {

    // The fragment initialization parameters
    private RadioButton rbDog, rbCat, rbOther;
    private TextInputLayout textInputLocation, textInputName, textInputEmail, textInputPhone, textInputAge, textInputBreed, textInputSterilized, textInputVaccines, textInputDescription;
    private TextInputEditText fieldLocation, fieldNamePet, fieldEmail, fieldPhone, fieldAge, fieldBreed, fieldVaccines, fieldDescription;
    private AutoCompleteTextView fieldSterilized;
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


    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseRef = ref.child("pet_adopted");
    private StorageReference storageRef;

    //Request codes
    private static final int REQUEST_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAMERA = 103;
    private static final int REQUEST_PERMISSIONS = 104;
    private static final int REQUEST_PERMISSIONS_LOCATION = 105;

    // Constants
    private final String ROOT_FOLDER = "FinderPet/";
    private final String ROUTE_IMAGE = ROOT_FOLDER + "myPhotos";


    public formAdoptedFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form_adopted, container, false);

    }// [End onCreateView]

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);


        // Capturamos el evento de la busqueda de dirección o zona
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = search_view.getQuery().toString();
                searchLoc = location;
                //List<Address> addressList = null;
                if (location != null) {
                    List<Address> addressList = null;
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
                        searchLoc = ""; // Limpiamos la variable con la dirección o zona buscada
                        Toast.makeText(getContext(), R.string.location_not_found, Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this); // sincronizamos el mapa con los nuevos parametros

        // Reference FirebaseStorage instance
        progressDialog = new ProgressDialog(getContext());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Reference FirebaseStorage instance
        storageRef = FirebaseStorage.getInstance().getReference();

        // Validate camera permissions and external write
        if (validatePermissions()) {
            img1.setEnabled(true);
            img2.setEnabled(true);
            img3.setEnabled(true);
        } else {
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
                saveNewAdopted();
            }
        });

        // Con este evento traemos la dirección actual o manual  del usuario
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
                                //dialogInterface.dismiss();
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    alertOpciones.show();
                }else {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_LOCATION);
                }
            }
        });

        // Con este evento confirmamos la dirección que buscamos manualmente
        btnConfirmLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchLocation();
            }
        });
    }// [End onViewCreated]

    private void setupViews(View view) {

        // Initialize variables
        rbDog = view.findViewById(R.id.rbDogAdopted);
        rbCat = view.findViewById(R.id.rbCatAdopted);
        rbOther = view.findViewById(R.id.rbOtherAdopted);
        textInputLocation = view.findViewById(R.id.textInputAddAdoptedLocation);
        textInputName = view.findViewById(R.id.textInputAddAdoptedName);
        textInputEmail = view.findViewById(R.id.textInputAddAdoptedEmail);
        textInputPhone = view.findViewById(R.id.textInputAddAdoptedPhone);
        textInputAge = view.findViewById(R.id.textInputAddAdoptedAge);
        textInputBreed = view.findViewById(R.id.textInputAddAdoptedBreed);
        textInputSterilized = view.findViewById(R.id.textInputAddAdoptedSterilized);
        textInputVaccines = view.findViewById(R.id.textInputAddAdoptedVaccines);
        textInputDescription = view.findViewById(R.id.textInputAddAdoptedDescription);
        fieldLocation = view.findViewById(R.id.fieldAddAdoptedLocation);
        fieldNamePet = view.findViewById(R.id.fieldAddAdoptedName);
        fieldEmail = view.findViewById(R.id.fieldAddAdoptedEmail);
        fieldPhone = view.findViewById(R.id.fieldAddAdoptedPhone);
        fieldAge = view.findViewById(R.id.fieldAddAdoptedAge);
        fieldBreed = view.findViewById(R.id.fieldAddAdoptedBreed);
        fieldSterilized = view.findViewById(R.id.fieldAddAdoptedSterilized);
        fieldVaccines = view.findViewById(R.id.fieldAddAdoptedVaccines);
        fieldDescription = view.findViewById(R.id.fieldAddAdoptedDescription);
        img1 = view.findViewById(R.id.imageAdopted_1);
        img2 = view.findViewById(R.id.imageAdopted_2);
        img3 = view.findViewById(R.id.imageAdopted_3);
        btnSave = view.findViewById(R.id.btnAddNewAdopted);
        pathLocal1 = null;
        pathLocal2 = null;
        pathLocal3 = null;
        path_uri_1 = "null";
        path_uri_2 = "null";
        path_uri_3 = "null";
        latitude = 6.2443382;
        longitude = -75.573553;

        // Creamos el adapter con los items para el textfield de Esterilizado
        String[] Types = new String[]{"Sí", "No"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_type_item, Types);
        fieldSterilized.setAdapter(adapter);
        btnConfirmLoc = view.findViewById(R.id.btnConfirmLocAdopted);
        linearLayout = view.findViewById(R.id.layoutSearchAdopted);
        search_view = view.findViewById(R.id.svMapsAdopted);
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapSearchAdopted);
    }


    private void getSearchLocation() {
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
                                //Toast.makeText(getContext(), "onSuccess: "+location.getLatitude(), Toast.LENGTH_LONG).show();

                                // In some rare cases the location returned can be null
                                if (lastLocation == null) {
                                    Toast.makeText(getContext(), R.string.location_not_found, Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (!Geocoder.isPresent()) {
                                    Toast.makeText(getContext(),R.string.no_geocoder_available,Toast.LENGTH_LONG).show();
                                    return;
                                }

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
                    dialogRecommendationToGrantPermission();
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
                            //Toast.makeText(getContext(), "onSuccess: "+location.getLatitude(), Toast.LENGTH_LONG).show();

                            // In some rare cases the location returned can be null
                            if (lastLocation == null) {
                                Toast.makeText(getContext(), R.string.location_not_found, Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (!Geocoder.isPresent()) {
                                Toast.makeText(getContext(),R.string.no_geocoder_available,Toast.LENGTH_LONG).show();
                                return;
                            }

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


    private void showProgressDialog(){
        progressDialog.setCancelable(false);
        //progressDialog.setTitle("Guardando nuevo registro..."); // usamos esta linea cuando no utilizamos el setContenView
        progressDialog.show();
        progressDialog.setContentView(R.layout.layout_pdialog); // No usamos esta linea cuando usamos setTitle
    };

    private void saveNewAdopted() {

        // Validates that the form has all the required fields
        if (!validateForm()) {
            progressDialog.dismiss();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //getting the values to save
                String rbPet="null";
                if (rbDog.isChecked()) rbPet = "dog";
                if (rbCat.isChecked()) rbPet = "cat";
                if (rbOther.isChecked()) rbPet = "other";
                String date = commonMethods.getDateTime();
                String name = fieldNamePet.getText().toString().trim();
                String email = fieldEmail.getText().toString().trim();
                String type = rbPet;
                String age = fieldAge.getText().toString().trim();
                String breed = fieldBreed.getText().toString().trim();
                String sterilized = fieldSterilized.getText().toString().trim();
                String vaccines = fieldVaccines.getText().toString().trim();
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
                Adopted_Vo adopted_vo = new Adopted_Vo(date, name, email, type, age, breed, sterilized,
                        vaccines, description, phone, img_1, img_2, img_3, location, latitude, longitude);

                //Saving the lost pet
                databaseRef.child(id).setValue(adopted_vo);

                //setting editText to blank again
                clearFields();

                progressDialog.dismiss();
                //displaying a success toast
                Toast.makeText(getContext(), R.string.post_successful, Toast.LENGTH_LONG).show();
            }
        },15000);

    }

    private void clearFields() {
        fieldNamePet.setText("");
        fieldLocation.setText("");
        fieldPhone.setText("");
        fieldEmail.setText("");
        fieldAge.setText("");
        fieldBreed.setText("");
        fieldSterilized.setText("");
        fieldVaccines.setText("");
        fieldDescription.setText("");
        img1.setImageResource(R.drawable.img_upload_1);
        img2.setImageResource(R.drawable.img_upload_2);
        img3.setImageResource(R.drawable.img_upload_3);
        latitude = 6.2443382;
        longitude = -75.573553;
    }

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
        String email = fieldEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            textInputEmail.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputEmail.setError(null);
        }
        String phone = fieldPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            textInputPhone.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputPhone.setError(null);
        }
        String description = fieldDescription.getText().toString();
        if (TextUtils.isEmpty(description)) {
            textInputDescription.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputDescription.setError(null);
        }
        String age = fieldAge.getText().toString();
        if (TextUtils.isEmpty(age)) {
            textInputAge.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputAge.setError(null);
        }
        String breed = fieldBreed.getText().toString();
        if (TextUtils.isEmpty(breed)) {
            textInputBreed.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputBreed.setError(null);
        }
        String sterilized = fieldSterilized.getText().toString();
        if (TextUtils.isEmpty(sterilized)) {
            textInputSterilized.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputSterilized.setError(null);
        }
        String vaccines = fieldVaccines.getText().toString();
        if (TextUtils.isEmpty(vaccines)) {
            textInputVaccines.setError(getString(R.string.required_field));
            valid = false;
        } else {
            textInputVaccines.setError(null);
        }
        return valid;
    }

    /**
     * Method to validate camera and external write permissions
     * @return boolean
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
            dialogRecommendationToGrantPermission();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},104);
        }
        return false;
    }

    /**
     * Method to validate camera and external write permissions
     * @return boolean
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
            dialogRecommendationToGrantPermission();
        }else{
            requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},105);
        }
        return false;
    }

    /**
     * Method to display permission recommendation dialog
     */
    private void dialogRecommendationToGrantPermission() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getActivity());
        dialogo.setTitle(R.string.dialog_recommendation_permission_call_title);
        dialogo.setMessage(R.string.accept_permissions_functioning_app);

        dialogo.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},104);
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
                manualPermitRequestCall().show();
            }
        }
        if(requestCode== REQUEST_PERMISSIONS_LOCATION){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(), "Ya puede traer su ubicaión actual automaticamente",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Permission was not granted",Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Method to display permission authorization dialog manually
     */
    private AlertDialog manualPermitRequestCall() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_permission_call_manually_title)
                .setMessage(R.string.dialog_permission_call_manually_message)
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
     * @param imgNew
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
                                        Log.i("Ruta de almacenamiento","Path: "+path);
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
                                    //Toast.makeText(getContext(),"Load Img1", Toast.LENGTH_SHORT).show();
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
                                    //Toast.makeText(getContext(),"Load Img2", Toast.LENGTH_SHORT).show();
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
                                    //Toast.makeText(getContext(),"Load Img3", Toast.LENGTH_SHORT).show();
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
