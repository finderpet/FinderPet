package com.finder.pet.Fragments;

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

import com.finder.pet.Entities.Found_Vo;
import com.finder.pet.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class formFoundFragment extends Fragment implements OnMapReadyCallback {

    // The fragment initialization parameters
    private RadioButton rbDog, rbCat, rbOther;
    private TextInputLayout textInputLocation, textInputName, textInputEmail, textInputPhone, textInputDescription;
    private TextInputEditText fieldLocation, fieldEmail, fieldPhone, fieldDescription;
    //private AutoCompleteTextView fieldType;
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

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private androidx.appcompat.widget.SearchView search_view;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseRef = ref.child("pet_found");
    private StorageReference storageRef;

    //Request codes
    private static final int REQUEST_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAMERA = 103;
    private static final int REQUEST_PERMISSIONS = 104;

    // Constants
    private final String ROOT_FOLDER="FinderPet/";
    private final String ROUTE_IMAGE=ROOT_FOLDER+"myPhotos";


    public formFoundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_found, container, false);

        // Initialize variables
        rbDog = view.findViewById(R.id.rbDogFound);
        rbCat = view.findViewById(R.id.rbCatFound);
        rbOther = view.findViewById(R.id.rbOtherFound);
        textInputLocation = view.findViewById(R.id.textInputAddFoundLocation);
        textInputEmail = view.findViewById(R.id.textInputAddFoundEmail);
        textInputPhone = view.findViewById(R.id.textInputAddFoundPhone);
        textInputDescription = view.findViewById(R.id.textInputAddFoundDescription);
        fieldLocation = view.findViewById(R.id.fieldAddFoundLocation);
        fieldEmail = view.findViewById(R.id.fieldAddFoundEmail);
        fieldPhone = view.findViewById(R.id.fieldAddFoundPhone);
        fieldDescription = view.findViewById(R.id.fieldAddFoundDescription);
        //fieldType = view.findViewById(R.id.fieldAddFoundType);
        img1 = view.findViewById(R.id.imageFound_1);
        img2 = view.findViewById(R.id.imageFound_2);
        img3 = view.findViewById(R.id.imageFound_3);
        btnSave = view.findViewById(R.id.btnAddNewFound);
        pathLocal1=null;
        pathLocal2=null;
        pathLocal3=null;
        path_uri_1="null";
        path_uri_2="null";
        path_uri_3="null";
        latitude = 6.2443382;
        longitude = -75.573553;

        btnConfirmLoc = view.findViewById(R.id.btnConfirmLocFound);

        linearLayout = view.findViewById(R.id.layoutSearchFound);
        search_view = view.findViewById(R.id.svMapsFound);
        mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.mapSearchFound);

        // Capturamos el evento de la busqueda de dirección o zona
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = search_view.getQuery().toString();
                searchLoc = location;
                List<Address> addressList = null;
                if (location != null){
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList.size() != 0){
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        map.addMarker(new MarkerOptions().position(latLng).title(location)).showInfoWindow();
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    }else {
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

//        // Creamos el adapter con los items para el textfield de tipo de mascota (AutoCompleteTextView)
//        String[] Types = new String[] {"Perro", "Gato", "Otro"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_type_item, Types);
//        fieldType.setAdapter(adapter);

        // Reference FirebaseStorage instance
        progressDialog = new ProgressDialog(getContext());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //getLocation();


        // Reference FirebaseStorage instance
        storageRef = FirebaseStorage.getInstance().getReference();

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
                saveNewFound();
            }
        });

        // Con este evento traemos la dirección actual o manual  del usuario
        textInputLocation.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] opciones={"Ubicación actual","Ubicación manual"};
                final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());
                alertOpciones.setTitle("Ingrese la ubicación de la mascota:");
                alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (opciones[i].equals("Ubicación actual")){
                            getCurrentLocation();
                        }else{
                            //dialogInterface.dismiss();
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
                alertOpciones.show();
            }
        });

        // Con este evento confirmamos la dirección que buscamos manualmente
        btnConfirmLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchLocation();
            }
        });

        return view;
    }

    private void getSearchLocation(){
        fieldLocation.setText(searchLoc);
        linearLayout.setVisibility(View.GONE);
    }

    private void getCurrentLocation() {

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.e("Location ","Lat: "+location.getLatitude()+" Lng: "+location.getLongitude());
                            List<Address> addresses = null;
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            String errorMessage = "";
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        // In this sample, get just a single address.
                                        1);
                                if (!addresses.isEmpty()) {
                                    Address streetAddress = addresses.get(0);
                                    fieldLocation.setText(streetAddress.getAddressLine(0));
                                }else{
                                    Toast.makeText(getContext(), "No se encontró la dirección actual", Toast.LENGTH_LONG).show();
                                }
                            }  catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void showProgressDialog(){
        progressDialog.setCancelable(false);
        //progressDialog.setTitle("Guardando nuevo registro..."); // usamos esta linea cuando no utilizamos el setContenView
        progressDialog.show();
        progressDialog.setContentView(R.layout.layout_pdialog); // No usamos esta linea cuando usamos setTitle
    };

    private void saveNewFound() {

        // Validates that the form has all the required fields
        if (!validateForm()) {
            progressDialog.dismiss();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create variables to the object
                String rbPet="null";
                String email = "null";
                String type = "null";
                String description = "null";
                String phone = "null";
                String img_1 = "null";
                String img_2 = "null";
                String img_3 = "null";
                String location = "null";

                //getting the values to save
                if (rbDog.isChecked()) rbPet = "Perro";
                if (rbCat.isChecked()) rbPet = "Gato";
                if (rbOther.isChecked()) rbPet = "Otro";
                email = fieldEmail.getText().toString().trim();
                type = rbPet;
                description = fieldDescription.getText().toString().trim();
                phone = fieldPhone.getText().toString().trim();
                img_1 = path_uri_1;
                img_2 = path_uri_2;
                img_3 = path_uri_3;
                location = fieldLocation.getText().toString().trim();

                //getting a unique id using push().getKey() method
                //it will create a unique id and we will use it as the Primary Key for our user
                String id = databaseRef.push().getKey();

                //creating an lost pet Object
                Found_Vo found_vo = new Found_Vo(email, type, description, phone, img_1, img_2, img_3, location, latitude, longitude);
                // Puedo crear con un solo entitie global que sea mascota (pet_vo)

                //Saving the lost pet
                databaseRef.child(id).setValue(found_vo);

                //setting edittext to blank again
                fieldLocation.setText("");
                fieldPhone.setText("");
                fieldEmail.setText("");
                fieldDescription.setText("");
                img1.setImageResource(R.mipmap.ic_photo1);
                img2.setImageResource(R.mipmap.ic_photo2);
                img3.setImageResource(R.mipmap.ic_photo3);
                latitude = 6.2443382;
                longitude = -75.573553;

                progressDialog.dismiss();
                //displaying a success toast
                Toast.makeText(getContext(), "¡Registro realizado correctamente!", Toast.LENGTH_LONG).show();
            }
        },15000);

    }

    private boolean validateForm() {
        boolean valid = true;

        if (!rbDog.isChecked() && !rbCat.isChecked() && !rbOther.isChecked()){
            Toast.makeText(getContext(), "Debes seleccionar un tipo de mascota", Toast.LENGTH_LONG).show();
            valid = false;
        }
        if (pathLocal1 == null){
            Toast.makeText(getContext(), "Debe agregar por lo menos la imagen 1", Toast.LENGTH_LONG).show();
            valid = false;
        }
        String location = fieldLocation.getText().toString();
        if (TextUtils.isEmpty(location)) {
            textInputLocation.setError("La dirección es olbigatoria");
            valid = false;
        } else {
            textInputLocation.setError(null);
        }
        String email = fieldEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            textInputEmail.setError("Correo Obligatorio");
            valid = false;
        } else {
            textInputEmail.setError(null);
        }
        String phone = fieldPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            textInputPhone.setError("El teléfono es Obligatorio");
            valid = false;
        } else {
            textInputPhone.setError(null);
        }
        String description = fieldDescription.getText().toString();
        if (TextUtils.isEmpty(description)) {
            textInputDescription.setError("La descripción es Obligatoria");
            valid = false;
        } else {
            textInputDescription.setError(null);
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
                && (getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                && (getContext().checkSelfPermission(ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA))
                || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))
                || (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION))){
            loadDialogRecomendation();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,ACCESS_FINE_LOCATION},104);
        }
        return false;
    }

    /**
     * Method to display permission recommendation dialog
     */
    private void loadDialogRecomendation() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getActivity());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,ACCESS_FINE_LOCATION},104);
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_PERMISSIONS){
            if(grantResults.length==3 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED){
                img1.setEnabled(true);
                img2.setEnabled(true);
                img3.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }
    }

    /**
     * Method to display permission authorization dialog manually
     */
    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getActivity());
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getActivity().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    /**
     * Method to upload image or take photo of the pet
     * @param imgNew
     */
    private void loadImage(ImageView imgNew, int imgNum) {
        imgNumber = imgNum; // Asigno el número que identifica la ruta de la imagen que estamos subiendo
        imgUpdate = imgNew; // Asigno a imgUpdate el ImageView que solicitó cargar la imagen
        final CharSequence[] options = {"Cargar desde Galería", "Tomar Foto", "Cancelar"};
        final AlertDialog.Builder alertOptions = new AlertDialog.Builder(getActivity());
        alertOptions.setTitle("Seleccione una opción");
        alertOptions.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(options[i].equals("Tomar Foto")){
                    makePhoto();
                }else {
                    if(options[i].equals("Cargar desde Galería")){
                        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");//si hay problemas para cargar algunas imagenes colocar "image/*"
                        startActivityForResult(intent.createChooser(intent, "Seleccione una aplicación"), 100);
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
                                    //Toast.makeText(getContext(),"Imagen Cargada", Toast.LENGTH_SHORT).show();
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
                                    //Toast.makeText(getContext(),"Imagen Cargada", Toast.LENGTH_SHORT).show();
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
                                    //Toast.makeText(getContext(),"Imagen Cargada", Toast.LENGTH_SHORT).show();
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
                            //Toast.makeText(getContext(),"Path local 1: "+pathLocal1, Toast.LENGTH_SHORT).show();
                            filePath1.putFile(pathLocal1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful());
                                    path_uri_1 = urlTask.getResult().toString();
                                    Toast.makeText(getContext(),"Load Img1", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getContext(),"Load Img2", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getContext(),"Load Img3", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }else {
            Toast.makeText(getContext(),"No se cargó la imagen", Toast.LENGTH_SHORT).show();
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
