package com.finder.pet.ui.maps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.finder.pet.Entities.Adopted_Vo;
import com.finder.pet.Entities.Found_Vo;
import com.finder.pet.Entities.Lost_Vo;
import com.finder.pet.Entities.PetMarker;
import com.finder.pet.R;
import com.finder.pet.Utilities.PreferencesApp;
import com.finder.pet.Utilities.commonMethods;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.navigation.Navigation.findNavController;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private MapsViewModel mapsViewModel;

    private ProgressBar progressBar;
    private TextView txtLoad;
    private FloatingActionButton btnAll, btnFound, btnLost, btnAdopted;

    ArrayList<PetMarker> ListAdopted;
    ArrayList<PetMarker> ListFound;
    ArrayList<PetMarker> ListLost;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient; // Get the last know location
    protected Location lastLocation;
    private static final int REQUEST_PERMISSIONS_LOCATION = 105;
    //onSaveInstanceState
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private SupportMapFragment mapFragment;
    private double latitude, longitude, latDefault, lngDefault;
    private LatLng latLngCity;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapsViewModel =
                ViewModelProviders.of(this).get(MapsViewModel.class);
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get state map
//        if (savedInstanceState != null) {
//            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
//            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
//        }

        progressBar =  view.findViewById(R.id.progressBarMaps);
        txtLoad = view.findViewById(R.id.textLoadMaps);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        PreferencesApp.getPreferences(preferences, getContext());

        final FloatingActionMenu menuButtons = view.findViewById(R.id.groupFab);
        btnAll = view.findViewById(R.id.btnFabAll);
        btnFound = view.findViewById(R.id.btnFabFound);
        btnLost = view.findViewById(R.id.btnFabLost);
        btnAdopted = view.findViewById(R.id.btnFabAdopted);

        ListAdopted=new ArrayList<>();
        ListFound=new ArrayList<>();
        ListLost=new ArrayList<>();
        latitude = 6.2443382;
        longitude = -75.573553;
        latDefault = 6.2443382;
        lngDefault = -75.573553;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //getLocation();

        latLngCity = new LatLng(latitude, longitude);
        mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.mapFrag);

        mapFragment.getMapAsync(this);

        getCurrentLocation();

        addMarkersAdopted();
        addMarkersFound();
        addMarkersLost();

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                addMarkersAdopted();
                addMarkersFound();
                addMarkersLost();
                menuButtons.close(true);
            }
        });
        btnAdopted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                addMarkersAdopted();
                menuButtons.close(true);
            }
        });
        btnFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                addMarkersFound();
                menuButtons.close(true);
            }
        });
        btnLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                addMarkersLost();
                menuButtons.close(true);
            }
        });

        mapFragment.getMapAsync(this); // sincronizamos el mapa con los nuevos parametros
    }

    /**
     * Method to get user current location
     */
    private void getCurrentLocation() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if((getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
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
                                latDefault = lastLocation.getLatitude();
                                lngDefault = lastLocation.getLongitude();
                                mapFragment.getMapAsync(MapsFragment.this); // sincronizamos el mapa con los nuevos parametros

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
                            latDefault = lastLocation.getLatitude();
                            lngDefault = lastLocation.getLongitude();
                            mapFragment.getMapAsync(MapsFragment.this); // sincronizamos el mapa con los nuevos parametros

                        }
                    });

        }

    }// [End getCurrentLocation]

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
     * Method for querying database items in Firebase
     */
    private void addMarkersAdopted() {

        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef = ref.child("pet_adopted");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    try {
                        String id = postSnapshot.getKey();
                        String image = postSnapshot.child("image1").getValue().toString();
                        String name = postSnapshot.child("name").getValue().toString();
                        String type = postSnapshot.child("type").getValue().toString();
                        if (type.equals("cat")){type=getString(R.string.cat);}
                        else if(type.equals("dog")){type=getString(R.string.dog);}
                        else {type=getString(R.string.other);};
                        String state = getString(R.string.adoption_singular);
                        String phone = postSnapshot.child("phone").getValue().toString();
                        String email = postSnapshot.child("email").getValue().toString();
                        String info = name+"&"+type+"&"+state+"&"+phone+"&"+email+"&"+id;
                        latitude = Double.parseDouble((postSnapshot.child("latitude").getValue().toString()));
                        longitude = Double.parseDouble((postSnapshot.child("longitude").getValue().toString()));
                        LatLng latLng = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(latLng).title(info).snippet(image)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).showInfoWindow();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method for querying database items in Firebase
     */
    private void addMarkersFound() {

        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef = ref.child("pet_found");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    try {
                        String id = postSnapshot.getKey();
                        String image = postSnapshot.child("image1").getValue().toString();
                        String name = getString(R.string.field_without_info);
                        String type = postSnapshot.child("type").getValue().toString();
                        if (type.equals("cat")){type=getString(R.string.cat);}
                        else if(type.equals("dog")){type=getString(R.string.dog);}
                        else {type=getString(R.string.other);};
                        String state = getString(R.string.found_singular);
                        String phone = postSnapshot.child("phone").getValue().toString();
                        String email = postSnapshot.child("email").getValue().toString();
                        String info = name+"&"+type+"&"+state+"&"+phone+"&"+email+"&"+id;
                        latitude = Double.parseDouble((postSnapshot.child("latitude").getValue().toString()));
                        longitude = Double.parseDouble((postSnapshot.child("longitude").getValue().toString()));
                        LatLng latLng = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(latLng).title(info).snippet(image)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method for querying database items in Firebase
     */
    private void addMarkersLost() {

        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef = ref.child("pet_lost");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    try {
                        String id = postSnapshot.getKey();
                        String image = postSnapshot.child("image1").getValue().toString();
                        String name = postSnapshot.child("name").getValue().toString();
                        String type = postSnapshot.child("type").getValue().toString();
                        if (type.equals("cat")){type=getString(R.string.cat);}
                        else if(type.equals("dog")){type=getString(R.string.dog);}
                        else {type=getString(R.string.other);};
                        String state = getString(R.string.lost_singular);
                        String phone = postSnapshot.child("phone").getValue().toString();
                        String email = postSnapshot.child("email").getValue().toString();
                        String info = name+"&"+type+"&"+state+"&"+phone+"&"+email+"&"+id;
                        latitude = Double.parseDouble((postSnapshot.child("latitude").getValue().toString()));
                        longitude = Double.parseDouble((postSnapshot.child("longitude").getValue().toString()));
                        LatLng latLng = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(latLng).title(info).snippet(image)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))).showInfoWindow();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
            }
        });
    }// [End addMarkerLost]

//    // save map state
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        if (map != null) {
//            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
//            outState.putParcelable(KEY_LOCATION, lastLocation);
//            super.onSaveInstanceState(outState);
//        }
//    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        final DatabaseReference databaseRefAdoption = ref.child("pet_adopted");
        final DatabaseReference databaseRefFound = ref.child("pet_found");
        final DatabaseReference databaseRefLost = ref.child("pet_lost");
        map = googleMap;
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getActivity())));
        double lat, lng;
//        lat = PreferencesApp.lanDefault;
//        lng = PreferencesApp.lngDefault;
        lat = latDefault;
        lng = lngDefault;
        final LatLng medellin = new LatLng(lat, lng);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(medellin, 12));
        //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);// Tipo de mapa


//        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                marker.showInfoWindow();
//                return false;
//            }
//        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String[] info = marker.getTitle().split("&");
                final String iId = info[5];

                databaseRefAdoption.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (postSnapshot.getKey().equals(iId)){
                                Adopted_Vo adoptedVo=new Adopted_Vo();
                                if (postSnapshot.child("date").exists()){
                                    String timeDays = commonMethods.getDaysDate(postSnapshot.child("date").getValue().toString(), getContext());
                                    adoptedVo.setDate(timeDays);
                                }else { adoptedVo.setDate(getString(R.string.time_date)); }
                                if (postSnapshot.child("name").exists()){
                                    adoptedVo.setName(postSnapshot.child("name").getValue().toString());
                                }else { adoptedVo.setName(getString(R.string.field_without_info)); }
                                if (postSnapshot.child("name").exists()){
                                    adoptedVo.setEmail(postSnapshot.child("email").getValue().toString());
                                }else { adoptedVo.setName(getString(R.string.field_without_info)); }
                                if (postSnapshot.child("age").exists()){
                                    adoptedVo.setAge(postSnapshot.child("age").getValue().toString());
                                }else { adoptedVo.setAge(getString(R.string.field_without_info)); }
                                if (postSnapshot.child("breed").exists()){
                                    adoptedVo.setBreed(postSnapshot.child("breed").getValue().toString());
                                }else { adoptedVo.setBreed(getString(R.string.field_without_info)); }
                                if (postSnapshot.child("sterilized").exists()){
                                    adoptedVo.setSterilized(postSnapshot.child("sterilized").getValue().toString());
                                }else { adoptedVo.setSterilized(getString(R.string.field_without_info)); }
                                if (postSnapshot.child("vaccines").exists()){
                                    adoptedVo.setVaccines(postSnapshot.child("vaccines").getValue().toString());
                                }else { adoptedVo.setVaccines(getString(R.string.field_without_info)); }
                                if (postSnapshot.child("location").exists()){
                                    adoptedVo.setLocation(postSnapshot.child("location").getValue().toString());
                                }else { adoptedVo.setVaccines(getString(R.string.field_without_info)); }
                                if (postSnapshot.child("observations").exists()){
                                    adoptedVo.setObservations(postSnapshot.child("observations").getValue().toString());
                                }else { adoptedVo.setVaccines(getString(R.string.field_without_info)); }
                                if (postSnapshot.child("phone").exists()){
                                    adoptedVo.setPhone(postSnapshot.child("phone").getValue().toString());
                                }else { adoptedVo.setVaccines(getString(R.string.field_without_info)); }
                                if (postSnapshot.child("type").exists()){
                                    adoptedVo.setType(postSnapshot.child("type").getValue().toString());
                                }else { adoptedVo.setVaccines(getString(R.string.field_without_info)); }
                                adoptedVo.setLatitude(Double.parseDouble((postSnapshot.child("latitude").getValue().toString())));
                                adoptedVo.setLongitude(Double.parseDouble((postSnapshot.child("longitude").getValue().toString())));
                                adoptedVo.setImage1(postSnapshot.child("image1").getValue().toString());
                                adoptedVo.setImage2(postSnapshot.child("image2").getValue().toString());
                                adoptedVo.setImage3(postSnapshot.child("image3").getValue().toString());

                                Bundle bundle = new Bundle(); // Create the bundle to transport the object
                                bundle.putSerializable("objeto", adoptedVo);
                                if (getView()!=null){
                                    findNavController(getView()).navigate(R.id.action_nav_map_to_detailAdoptedFragment, bundle);
                                }else {
                                    Toast.makeText(getContext(),R.string.could_not_get_information,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
                    }
                });
                databaseRefFound.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (postSnapshot.getKey().equals(iId)){
                                Found_Vo foundVo=new Found_Vo();
                                String timeDays = commonMethods.getDaysDate(postSnapshot.child("date").getValue().toString(), getContext());
                                foundVo.setDate(timeDays);
                                foundVo.setEmail(postSnapshot.child("email").getValue().toString());
                                foundVo.setImage1(postSnapshot.child("image1").getValue().toString());
                                foundVo.setImage2(postSnapshot.child("image2").getValue().toString());
                                foundVo.setImage3(postSnapshot.child("image3").getValue().toString());
                                foundVo.setLocation(postSnapshot.child("location").getValue().toString());
                                foundVo.setObservations(postSnapshot.child("observations").getValue().toString());
                                foundVo.setPhone(postSnapshot.child("phone").getValue().toString());
                                foundVo.setType(postSnapshot.child("type").getValue().toString());
                                foundVo.setLatitude(Double.parseDouble((postSnapshot.child("latitude").getValue().toString())));
                                foundVo.setLongitude(Double.parseDouble((postSnapshot.child("longitude").getValue().toString())));
                                Bundle bundle = new Bundle(); // Create the bundle to transport the object
                                bundle.putSerializable("objeto", foundVo);
                                if (getView()!=null){
                                    findNavController(getView()).navigate(R.id.action_nav_map_to_detailFoundFragment, bundle);
                                }else {
                                    Toast.makeText(getContext(),R.string.could_not_get_information,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
                    }
                });
                databaseRefLost.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (postSnapshot.getKey().equals(iId)){
                                Lost_Vo lostVo=new Lost_Vo();
                                String timeDays = commonMethods.getDaysDate(postSnapshot.child("date").getValue().toString(), getContext());
                                lostVo.setDate(timeDays);
                                lostVo.setName(postSnapshot.child("name").getValue().toString());
                                lostVo.setEmail(postSnapshot.child("email").getValue().toString());
                                lostVo.setImage1(postSnapshot.child("image1").getValue().toString());
                                lostVo.setImage2(postSnapshot.child("image2").getValue().toString());
                                lostVo.setImage3(postSnapshot.child("image3").getValue().toString());
                                lostVo.setLocation(postSnapshot.child("location").getValue().toString());
                                lostVo.setObservations(postSnapshot.child("observations").getValue().toString());
                                lostVo.setPhone(postSnapshot.child("phone").getValue().toString());
                                lostVo.setType(postSnapshot.child("type").getValue().toString());
                                lostVo.setLatitude(Double.parseDouble((postSnapshot.child("latitude").getValue().toString())));
                                lostVo.setLongitude(Double.parseDouble((postSnapshot.child("longitude").getValue().toString())));
                                Bundle bundle = new Bundle(); // Create the bundle to transport the object
                                bundle.putSerializable("objeto", lostVo);
                                if (getView()!=null){
                                    findNavController(getView()).navigate(R.id.action_nav_map_to_detailLostFragment, bundle);
                                }else {
                                    Toast.makeText(getContext(),R.string.could_not_get_information,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public static class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private static final String TAG = "CustomInfoWindowAdapter";
        private LayoutInflater inflater;
        ImageView imgView;

        public CustomInfoWindowAdapter(LayoutInflater inflater){
            this.inflater = inflater;
        }

        @Override
        public View getInfoContents(final Marker m) {
            //Carga layout personalizado.
            final View v = inflater.inflate(R.layout.infowindow_layout, null);
            String[] info = m.getTitle().split("&");
            String iName = info[0];
            String iType = info[1];
            String iState = info[2];
            String iPhone = info[3];
            String iEmail = info[4];
            String iId = info[5];
            String urlImage = m.getSnippet();
            ((TextView)v.findViewById(R.id.info_window_name)).setText(iName);
            ((TextView)v.findViewById(R.id.info_window_type)).setText(iType);
            ((TextView)v.findViewById(R.id.info_window_state)).setText(iState);
            ((TextView)v.findViewById(R.id.info_window_phone)).setText(iPhone);
            ((TextView)v.findViewById(R.id.info_window_email)).setText(iEmail);
            imgView=v.findViewById(R.id.info_window_image);
            Glide.with(v.getContext())
                    .load(urlImage)
                    .placeholder(R.drawable.sin_imagen)
                    .centerCrop()// center the image and take up the entire imageView space
                    .into(imgView);
            return v;

        }// [End getInfoContents]

        @Override
        public View getInfoWindow(Marker m) {
            return null;
        }

    }


}