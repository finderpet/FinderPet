package com.finder.pet.ui.maps;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private SupportMapFragment mapFragment;
    private double latitude, longitude;
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
        latLngCity = new LatLng(latitude, longitude);
        mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.mapFrag);

        mapFragment.getMapAsync(this);

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

                }
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "No se pudo obtener información ", Toast.LENGTH_SHORT).show();
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

                }
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "No se pudo obtener información ", Toast.LENGTH_SHORT).show();
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

                }
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "No se pudo obtener información ", Toast.LENGTH_SHORT).show();
            }
        });
    }// [End addMarkerLost]

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final DatabaseReference databaseRefAdoption = ref.child("pet_adopted");
        final DatabaseReference databaseRefFound = ref.child("pet_found");
        final DatabaseReference databaseRefLost = ref.child("pet_lost");
        map = googleMap;
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getActivity())));
        double lat, lng;
        lat = PreferencesApp.lanDefault;
        lng = PreferencesApp.lngDefault;
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