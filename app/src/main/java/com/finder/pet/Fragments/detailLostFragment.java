package com.finder.pet.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private TextView txtDate, txtName, txtType, txtMicrochip, txtEmail, txtLocation, txtPhone, txtObservations;
    private ImageView imgPet1, imgPet2, imgPet3;
    private FloatingActionButton btnDeletePost;
    private String imgUrl_1, imgUrl_2, imgUrl_3;
    private String namePet, keyPost;
    private double lat, lng;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseRef;

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
        return inflater.inflate(R.layout.fragment_detail_lost, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize variables
        setupViews(view);

        mapFragment.getMapAsync(this);

        Bundle objectLost=getArguments();
        Lost_Vo lost_vo;
        if (objectLost != null){
            lost_vo = (Lost_Vo) objectLost.getSerializable("objeto");

            // Validated user to enable delete button
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null){
                String idUser = user.getUid();
                if (idUser.equals(lost_vo.getIdUser())){
                    Log.i("Id current user", idUser);
                    btnDeletePost.setVisibility(View.VISIBLE);
                }
            }

            keyPost = lost_vo.getKeyPost();

            //Fill the detail fields with the information of the object brought from the list of pets
            txtDate.setText(lost_vo.getDate());
            txtName.setText(lost_vo.getName());
            txtMicrochip.setText(lost_vo.getMicrochip());
            if (lost_vo.getType().equals("dog")){
                txtType.setText(R.string.dog);
            }else if (lost_vo.getType().equals("cat")){
                txtType.setText(R.string.cat);
            }else {
                txtType.setText(R.string.other);
            }
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
            namePet = lost_vo.getName();
        }

        imgPet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("objeto1", imgUrl_1);
                bundle.putString("objeto2", imgUrl_2);
                bundle.putString("objeto3", imgUrl_3);
                findNavController(view).navigate(R.id.action_detailLostFragment_to_pagerPhotoFragment, bundle);
            }
        });
        imgPet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("objeto1", imgUrl_2);
                bundle.putString("objeto2", imgUrl_1);
                bundle.putString("objeto3", imgUrl_3);
                findNavController(view).navigate(R.id.action_detailLostFragment_to_pagerPhotoFragment, bundle);
            }
        });
        imgPet3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("objeto1", imgUrl_3);
                bundle.putString("objeto2", imgUrl_1);
                bundle.putString("objeto3", imgUrl_2);
                findNavController(view).navigate(R.id.action_detailLostFragment_to_pagerPhotoFragment, bundle);
            }
        });
        btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDeletePost().show();
            }
        });
    }

    /**
     * Method to initialize the views
     * @param view View fragment
     */
    private void setupViews(View view) {
        btnDeletePost = view.findViewById(R.id.fabDeletePostLost);
        txtDate= view.findViewById(R.id.detailLostDate);
        txtName = view.findViewById(R.id.detailLostName);
        txtMicrochip = view.findViewById(R.id.detailLostChipPet);
        txtType = view.findViewById(R.id.detailLostTypePet);
        txtLocation = view.findViewById(R.id.detailLostLocation);
        txtEmail = view.findViewById(R.id.detailLostEmailContact);
        txtPhone = view.findViewById(R.id.detailLostPhoneContact);
        txtObservations = view.findViewById(R.id.detailLostObservations);
        imgPet1 = view.findViewById(R.id.imgDetailLost1);
        imgPet2 = view.findViewById(R.id.imgDetailLost2);
        imgPet3 = view.findViewById(R.id.imgDetailLost3);

        // Associate the fragment that will contain the map in the detail
        mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.mapView);
    }

    /**
     * Method to display delete post dialog
     */
    private AlertDialog dialogDeletePost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.delete_post_title)
                .setMessage(R.string.delete_post_msg)
                .setPositiveButton(Html.fromHtml(getString(R.string.btn_delete)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost();
                        findNavController(getView()).navigate(R.id.action_detailLostFragment_to_lostFragment);
                        //dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    /**
     * Method to delete a post
     */
    private void deletePost(){
        databaseRef = ref.child("pet_lost/");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(keyPost).exists()){
                    // Get firebase user phone
                    databaseRef = ref.child("pet_lost/"+keyPost);
                    databaseRef.removeValue();
                    Toast.makeText(getContext(),R.string.post_was_successfully_removed,Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(),R.string.post_was_not_successfully_removed,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
            }
        });
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
