package com.finder.pet.Fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.finder.pet.Entities.Found_Vo;
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
 * Activities that contain this fragment must implement the
 * {@link DetailFoundFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFoundFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView txtType, txtEmail, txtLocation, txtPhone, txtObservations;
    private ImageView imgPet1, imgPet2, imgPet3;
    private String imgUrl_1, imgUrl_2, imgUrl_3;
    private double lat, lng;
    private String namePet;
    //private ViewFlipper viewFlipper;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    public DetailFoundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFoundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFoundFragment newInstance(String param1, String param2) {
        DetailFoundFragment fragment = new DetailFoundFragment();
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
        View view = inflater.inflate(R.layout.fragment_detail_found, container, false);
        txtType = view.findViewById(R.id.detailFoundTypePet);
        txtLocation = view.findViewById(R.id.detailFoundLocation);
        txtEmail = view.findViewById(R.id.detailFoundEmailContact);
        txtPhone = view.findViewById(R.id.detailFoundPhoneContact);
        txtObservations = view.findViewById(R.id.detailFoundObservations);
        imgPet1 = view.findViewById(R.id.imgDetailFound1);
        imgPet2 = view.findViewById(R.id.imgDetailFound2);
        imgPet3 = view.findViewById(R.id.imgDetailFound3);

        // Asociamos el fragment que contendra el mapa en el detalle
        mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        Bundle objectFound=getArguments();
        Found_Vo found_vo;
        if (objectFound != null){
            found_vo = (Found_Vo) objectFound.getSerializable("objeto");

            //Llenamos los campos del detalle con la información del objeto traido desde la lista de mascotas encontradas
            txtType.setText(found_vo.getType());
            txtLocation.setText(found_vo.getLocation());
            txtEmail.setText(found_vo.getEmail());
            txtPhone.setText(found_vo.getPhone());
            txtObservations.setText(found_vo.getObservations());
            imgUrl_1=found_vo.getImage1();
            imgUrl_2=found_vo.getImage2();
            imgUrl_3=found_vo.getImage3();
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
            lat = found_vo.getLatitude();
            lng = found_vo.getLongitude();
            //Toast.makeText(getContext(), String.valueOf(lng), Toast.LENGTH_LONG).show();
            namePet = found_vo.getType();
        }
//        //Código para mostrar el flipper de imagenes
//        String imagesUrl[] = {imgUrl_1, imgUrl_2, imgUrl_3};
//        viewFlipper = view.findViewById(R.id.viewFlipperFound);
//        for (String image: imagesUrl){
//            flipperImg(image);
//        }

        imgPet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Evento para llamar al fragment DetailFoundFragment pasandole un objeto tipo bundle
                Bundle bundle = new Bundle(); //Creamos el bundle para transportar el string de la url de la imagen
                bundle.putString("objeto", imgUrl_1); //Pasamos al bundle la url de la imagen seleccionada
                findNavController(view).navigate(R.id.action_detailFoundFragment_to_viewImageFragment, bundle); //Ejecutamos el action junto con el bundle
            }
        });
        imgPet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Evento para llamar al fragment DetailFoundFragment pasandole un objeto tipo bundle
                Bundle bundle = new Bundle(); //Creamos el bundle para transportar el string de la url de la imagen
                bundle.putString("objeto", imgUrl_2); //Pasamos al bundle la url de la imagen seleccionada
                findNavController(view).navigate(R.id.action_detailFoundFragment_to_viewImageFragment, bundle); //Ejecutamos el action junto con el bundle
            }
        });
        imgPet3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Evento para llamar al fragment DetailFoundFragment pasandole un objeto tipo bundle
                Bundle bundle = new Bundle(); //Creamos el bundle para transportar el string de la url de la imagen
                bundle.putString("objeto", imgUrl_3); //Pasamos al bundle la url de la imagen seleccionada
                findNavController(view).navigate(R.id.action_detailFoundFragment_to_viewImageFragment, bundle); //Ejecutamos el action junto con el bundle
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
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

//    // Metodo para mostrar el flipper de imagenes
//    public void flipperImg(String imageUrl){
//        ImageView imageView = new ImageView(getContext());
//        imageView.setBackgroundColor(Color.parseColor("#000000"));
//        Glide.with(getContext())
//                .load(imageUrl)
//                .placeholder(R.drawable.sin_imagen)
//                .into(imageView);
//        viewFlipper.addView(imageView);
//        viewFlipper.setFlipInterval(4000);
//        viewFlipper.setAutoStart(true);
//        viewFlipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
//        viewFlipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
