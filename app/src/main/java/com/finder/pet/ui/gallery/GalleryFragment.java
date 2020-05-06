package com.finder.pet.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.finder.pet.Adapters.AdoptedAdapter;
import com.finder.pet.Entities.Adopted_Vo;
import com.finder.pet.Entities.PetMarker;
import com.finder.pet.Fragments.ContainerFragment;
import com.finder.pet.Main.MapsActivity;
import com.finder.pet.R;
import com.finder.pet.Utilities.Utilities;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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

public class GalleryFragment extends Fragment implements OnMapReadyCallback {

    private GalleryViewModel galleryViewModel;

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
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar =  view.findViewById(R.id.progressBarMaps);
        txtLoad = view.findViewById(R.id.textLoadMaps);

        final FloatingActionsMenu menuButtons = view.findViewById(R.id.groupFab);
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
                menuButtons.collapse();
            }
        });
        btnAdopted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                addMarkersAdopted();
                menuButtons.collapse();
            }
        });
        btnFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                addMarkersFound();
                menuButtons.collapse();
            }
        });
        btnLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                addMarkersLost();
                menuButtons.collapse();
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
                PetMarker petMarker;
                //clearing the previous Founds list
                ListAdopted.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    petMarker=new PetMarker();

                    petMarker.setName(postSnapshot.child("name").getValue().toString());
                    //petMarker.setState(postSnapshot.child("email").getValue().toString());
                    petMarker.setLatitude(Double.parseDouble((postSnapshot.child("latitude").getValue().toString())));
                    petMarker.setLongitude(Double.parseDouble((postSnapshot.child("longitude").getValue().toString())));

                    //We are filling the list of found
                    ListAdopted.add(petMarker);
                }
                Marker marker = null;
                for (int i=0; i < ListAdopted.size(); i++){
                    latitude = ListAdopted.get(i).getLatitude();
                    longitude = ListAdopted.get(i).getLongitude();
                    String name = ListAdopted.get(i).getName();
                    //String state = ListAdopted.get(i).getState();
                    LatLng latLng = new LatLng(latitude, longitude);
                    //map.addMarker(new MarkerOptions().position(latLng).title(name).snippet("Adopción").icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_lost))).showInfoWindow();
                    map.addMarker(new MarkerOptions().position(latLng).title(name).snippet("Adopción")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).showInfoWindow();
                    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCity, 12));
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
                PetMarker petMarker;
                //clearing the previous Founds list
                ListFound.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    petMarker=new PetMarker();

                    petMarker.setName(postSnapshot.child("type").getValue().toString());
                    //petMarker.setState(postSnapshot.child("email").getValue().toString());
                    petMarker.setLatitude(Double.parseDouble((postSnapshot.child("latitude").getValue().toString())));
                    petMarker.setLongitude(Double.parseDouble((postSnapshot.child("longitude").getValue().toString())));

                    //We are filling the list of found
                    ListFound.add(petMarker);
                }
                Marker marker = null;
                for (int i=0; i < ListFound.size(); i++){
                    latitude = ListFound.get(i).getLatitude();
                    longitude = ListFound.get(i).getLongitude();
                    String name = ListFound.get(i).getName();
                    //String state = ListFound.get(i).getState();
                    LatLng latLng = new LatLng(latitude, longitude);
                    map.addMarker(new MarkerOptions().position(latLng).title(name).snippet("Encontrado")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCity, 12));
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
                PetMarker petMarker;
                //clearing the previous Founds list
                ListLost.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    petMarker=new PetMarker();

                    petMarker.setName(postSnapshot.child("name").getValue().toString());
                    //petMarker.setState(postSnapshot.child("email").getValue().toString());
                    petMarker.setLatitude(Double.parseDouble((postSnapshot.child("latitude").getValue().toString())));
                    petMarker.setLongitude(Double.parseDouble((postSnapshot.child("longitude").getValue().toString())));

                    //We are filling the list of found
                    ListLost.add(petMarker);
                }
                Marker marker = null;
                for (int i=0; i < ListLost.size(); i++){
                    latitude = ListLost.get(i).getLatitude();
                    longitude = ListLost.get(i).getLongitude();
                    String name = ListLost.get(i).getName();
                    //String state = ListLost.get(i).getState();
                    LatLng latLng = new LatLng(latitude, longitude);
                    map.addMarker(new MarkerOptions().position(latLng).title(name).snippet("Perdido")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))).showInfoWindow();
                    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCity, 12));
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "No se pudo obtener información ", Toast.LENGTH_SHORT).show();
            }
        });
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