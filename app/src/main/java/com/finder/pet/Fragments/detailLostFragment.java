package com.finder.pet.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.pet.Entities.Lost_Vo;
import com.finder.pet.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link detailLostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detailLostFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView txtName, txtType, txtEmail, txtLocation, txtPhone, txtObservations;
    private ImageView imgPet1, imgPet2, imgPet3;
    private String imgUrl_1, imgUrl_2, imgUrl_3;
    private String namePet;
    private double lat, lng;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    public detailLostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment detailLostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static detailLostFragment newInstance(String param1, String param2) {
        detailLostFragment fragment = new detailLostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_detail_lost, container, false);

        txtName = view.findViewById(R.id.detailLostName);
        txtType = view.findViewById(R.id.detailLostTypePet);
        txtLocation = view.findViewById(R.id.detailLostLocation);
        txtEmail = view.findViewById(R.id.detailLostEmailContact);
        txtPhone = view.findViewById(R.id.detailLostPhoneContact);
        txtObservations = view.findViewById(R.id.detailLostObservations);
        imgPet1 = view.findViewById(R.id.imgDetailLost1);
        imgPet2 = view.findViewById(R.id.imgDetailLost2);
        imgPet3 = view.findViewById(R.id.imgDetailLost3);

        // Asociamos el fragment que contendra el mapa en el detalle
        mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        Bundle objectLost=getArguments();
        Lost_Vo lost_vo;
        if (objectLost != null){
            lost_vo = (Lost_Vo) objectLost.getSerializable("objeto");

            //Llenamos los campos del detalle con la información del objeto traido desde la lista de mascotas perdidas
            txtName.setText(lost_vo.getName());
            txtType.setText(lost_vo.getType());
            txtLocation.setText(lost_vo.getLocation());
            txtEmail.setText(lost_vo.getEmail());
            txtPhone.setText(lost_vo.getPhone());
            txtObservations.setText(lost_vo.getObservations());
            imgUrl_1=lost_vo.getImage1();
            imgUrl_2=lost_vo.getImage2();
            imgUrl_3=lost_vo.getImage3();
            Glide.with(getContext())
                    .load(imgUrl_1)
                    .placeholder(R.drawable.sin_imagen)
                    .into(imgPet1);
            Glide.with(getContext())
                    .load(imgUrl_2)
                    .placeholder(R.drawable.sin_imagen)
                    .into(imgPet2);
            Glide.with(getContext())
                    .load(imgUrl_3)
                    .placeholder(R.drawable.sin_imagen)
                    .into(imgPet3);
            lat = lost_vo.getLatitude();
            lng = lost_vo.getLongitude();
            //Toast.makeText(getContext(), String.valueOf(lng), Toast.LENGTH_LONG).show();
            namePet = lost_vo.getName();
        }

        imgPet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Evento para llamar al fragment DetailFoundFragment pasandole un objeto tipo bundle
                Bundle bundle = new Bundle(); //Creamos el bundle para transportar el string de la url de la imagen
                bundle.putString("objeto", imgUrl_1); //Pasamos al bundle la url de la imagen seleccionada
                findNavController(view).navigate(R.id.action_detailLostFragment_to_viewImageFragment, bundle); //Ejecutamos el action junto con el bundle
            }
        });
        imgPet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Evento para llamar al fragment DetailFoundFragment pasandole un objeto tipo bundle
                Bundle bundle = new Bundle(); //Creamos el bundle para transportar el string de la url de la imagen
                bundle.putString("objeto", imgUrl_2); //Pasamos al bundle la url de la imagen seleccionada
                findNavController(view).navigate(R.id.action_detailLostFragment_to_viewImageFragment, bundle); //Ejecutamos el action junto con el bundle
            }
        });
        imgPet3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Evento para llamar al fragment DetailFoundFragment pasandole un objeto tipo bundle
                Bundle bundle = new Bundle(); //Creamos el bundle para transportar el string de la url de la imagen
                bundle.putString("objeto", imgUrl_3); //Pasamos al bundle la url de la imagen seleccionada
                findNavController(view).navigate(R.id.action_detailLostFragment_to_viewImageFragment, bundle); //Ejecutamos el action junto con el bundle
            }
        });

        return view;
    }

    // Create map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mMap = googleMap;
        createMarker(googleMap);
    }

    // Create map marker
    public void createMarker(GoogleMap googleMap){
        mMap = googleMap;
        final LatLng latLng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(latLng).title(namePet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }
}
