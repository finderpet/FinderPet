package com.finder.pet.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.finder.pet.Entities.Adopted_Vo;
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
 * Use the {@link detailAdoptedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detailAdoptedFragment extends Fragment implements OnMapReadyCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView txtDate, txtName, txtEmail, txtType, txtAge, txtBreed, txtSterilized, txtVaccines, txtLocation, txtPhone, txtObservations;
    private ImageView imgPet1, imgPet2, imgPet3;
    private FloatingActionButton btnDeletePost;
    private String imgUrl_1, imgUrl_2, imgUrl_3;
    private String namePet, keyPost;
    private double lat, lng;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseRef;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    public detailAdoptedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment detailAdoptedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static detailAdoptedFragment newInstance(String param1, String param2) {
        detailAdoptedFragment fragment = new detailAdoptedFragment();
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
        return inflater.inflate(R.layout.fragment_detail_adopted, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize variables
        setupViews(view);

        mapFragment.getMapAsync(this);

        Bundle objectAdopted=getArguments();
        Adopted_Vo adopted_vo;
        if (objectAdopted != null){
            adopted_vo = (Adopted_Vo) objectAdopted.getSerializable("objeto");

            // Validated user to enable delete button
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null){
                String idUser = user.getUid();
                if (idUser.equals(adopted_vo.getIdUser())){
                    Log.i("Id current user", idUser);
                    btnDeletePost.setVisibility(View.VISIBLE);
                }
            }

            keyPost = adopted_vo.getKeyPost();

            //Fill the detail fields with the information of the object brought from the list of pets
            txtDate.setText(adopted_vo.getDate());
            txtName.setText(adopted_vo.getName());
            txtEmail.setText(adopted_vo.getEmail());
            if (adopted_vo.getType().equals("dog")){
                txtType.setText(R.string.dog);
            }else if (adopted_vo.getType().equals("cat")){
                txtType.setText(R.string.cat);
            }else {
                txtType.setText(R.string.other);
            }
            txtAge.setText(adopted_vo.getAge());
            txtBreed.setText(adopted_vo.getBreed());
            txtSterilized.setText(adopted_vo.getSterilized());
            txtVaccines.setText(adopted_vo.getVaccines());
            txtLocation.setText(adopted_vo.getLocation());
            txtPhone.setText(adopted_vo.getPhone());
            txtObservations.setText(adopted_vo.getObservations());
            imgUrl_1=adopted_vo.getImage1();
            imgUrl_2=adopted_vo.getImage2();
            imgUrl_3=adopted_vo.getImage3();
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
            lat = adopted_vo.getLatitude();
            lng = adopted_vo.getLongitude();
            namePet = adopted_vo.getName();
        }

        imgPet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("objeto1", imgUrl_1);
                bundle.putString("objeto2", imgUrl_2);
                bundle.putString("objeto3", imgUrl_3);
                findNavController(view).navigate(R.id.action_detailAdoptedFragment_to_pagerPhotoFragment, bundle);
            }
        });
        imgPet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("objeto1", imgUrl_2);
                bundle.putString("objeto2", imgUrl_1);
                bundle.putString("objeto3", imgUrl_3);
                findNavController(view).navigate(R.id.action_detailAdoptedFragment_to_pagerPhotoFragment, bundle);
            }
        });
        imgPet3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("objeto1", imgUrl_3);
                bundle.putString("objeto2", imgUrl_1);
                bundle.putString("objeto3", imgUrl_2);
                findNavController(view).navigate(R.id.action_detailAdoptedFragment_to_pagerPhotoFragment, bundle);
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
        btnDeletePost = view.findViewById(R.id.fabDeletePostAdopted);
        txtDate= view.findViewById(R.id.detailAdoptedDate);
        txtName = view.findViewById(R.id.detailAdoptedName);
        txtEmail = view.findViewById(R.id.detailAdoptedEmailContact);
        txtType = view.findViewById(R.id.detailAdoptedTypePet);
        txtAge = view.findViewById(R.id.detailAdoptedAgePet);
        txtBreed = view.findViewById(R.id.detailAdoptedBreed);
        txtSterilized = view.findViewById(R.id.detailAdoptedSterilized);
        txtVaccines = view.findViewById(R.id.detailAdoptedVaccines);
        txtLocation = view.findViewById(R.id.detailAdoptedLocation);
        txtPhone = view.findViewById(R.id.detailAdoptedPhoneContact);
        txtObservations = view.findViewById(R.id.detailAdoptedObservations);
        imgPet1 = view.findViewById(R.id.imgDetailAdopted1);
        imgPet2 = view.findViewById(R.id.imgDetailAdopted2);
        imgPet3 = view.findViewById(R.id.imgDetailAdopted3);

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
                        findNavController(getView()).navigate(R.id.action_detailAdoptedFragment_to_adoptedFragment);
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
        databaseRef = ref.child("pet_adopted/");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(keyPost).exists()){
                    // Get firebase user phone
                    databaseRef = ref.child("pet_adopted/"+keyPost);
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
        LatLng latLng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(latLng).title(namePet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }


}
