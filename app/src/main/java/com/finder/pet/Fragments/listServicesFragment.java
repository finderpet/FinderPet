package com.finder.pet.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finder.pet.Adapters.AdvertAdapter;
import com.finder.pet.Entities.Advert;
import com.finder.pet.Main.MainActivity;
import com.finder.pet.R;
import com.finder.pet.Utilities.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.Manifest.permission.CALL_PHONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class listServicesFragment extends Fragment {

    private FoundFragment.OnFragmentInteractionListener mListener;

    RecyclerView recyclerListAdverts;
    ArrayList<Advert> ListAdverts;

    private ProgressBar progressBar;
    private TextView txtLoad;

    private static final int REQUEST_PERMISSIONS = 104;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef;

    public listServicesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerListAdverts=view.findViewById(R.id.id_recycler_adverts);
        recyclerListAdverts.setLayoutManager(new LinearLayoutManager(getContext()));
        ListAdverts=new ArrayList<>();

        progressBar =  view.findViewById(R.id.progressBarAdvert);
        txtLoad = view.findViewById(R.id.textLoadAdvert);

        // Validate call permissions
        if(validatePermissionsCall()){
            Utilities.PERMISSION_CALL = true;
        }

        String typeAds = getArguments().getString("Object");
        if (typeAds.equals("Services")){
            getActionBar().setTitle(R.string.services);
            consultListVeterinaryCenters();
        }
        if (typeAds.equals("Products")){
            getActionBar().setTitle(R.string.products);
            consultListPetShops();
        }
        if (typeAds.equals("Delivery")){
            getActionBar().setTitle(R.string.delivery);
            consultListDelivery();
        }
        if (typeAds.equals("Events")){
            getActionBar().setTitle(R.string.events);
            consultListEvents();
        }

    }

    /**
     * Method for querying database veterinary items in Firebase
     */
    private void consultListVeterinaryCenters() {
        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef = ref.child("veterinary");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Advert advert;
                //clearing the previous Founds list
                ListAdverts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    advert=new Advert();

                    if (postSnapshot.child("name").exists()){
                        advert.setName(postSnapshot.child("name").getValue().toString());
                    }else { advert.setName(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("description").exists()){
                        advert.setDescription(postSnapshot.child("description").getValue().toString());
                    }else { advert.setDescription(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("phone").exists()){
                        advert.setPhone(postSnapshot.child("phone").getValue().toString());
                    }else { advert.setPhone(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("urlPage").exists()){
                        advert.setUrlPage(postSnapshot.child("urlPage").getValue().toString());
                    }else { advert.setUrlPage(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("image").exists()){
                        advert.setImage(postSnapshot.child("image").getValue().toString());
                    }

                    //We are filling the list of found
                    ListAdverts.add(advert);
                }

                Collections.shuffle(ListAdverts);
                AdvertAdapter adapter =  new AdvertAdapter(ListAdverts);
                recyclerListAdverts.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
//                adapter.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        // Evento para llamar al fragment DetailFoundFragment pasandole un objeto tipo bundle
//                        Bundle bundle = new Bundle(); //Creamos el bundle para transportar al objeto
//                        bundle.putSerializable("objeto", ListAdverts.get(recyclerListAdverts.getChildAdapterPosition(view))); //Pasamos al bundle el objeto especifico
//                        findNavController(view).navigate(R.id.action_foundFragment_to_detailFoundFragment, bundle); //Ejecutamos el action junto con el bundle
//                    }
//                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method for querying database pet_shops items in Firebase
     */
    private void consultListPetShops() {
        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef = ref.child("pet_shops");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Advert advert;
                //clearing the previous Founds list
                ListAdverts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    advert=new Advert();

                    if (postSnapshot.child("name").exists()){
                        advert.setName(postSnapshot.child("name").getValue().toString());
                    }else { advert.setName(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("description").exists()){
                        advert.setDescription(postSnapshot.child("description").getValue().toString());
                    }else { advert.setDescription(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("phone").exists()){
                        advert.setPhone(postSnapshot.child("phone").getValue().toString());
                    }else { advert.setPhone(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("urlPage").exists()){
                        advert.setUrlPage(postSnapshot.child("urlPage").getValue().toString());
                    }else { advert.setUrlPage(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("image").exists()){
                        advert.setImage(postSnapshot.child("image").getValue().toString());
                    }

                    //We are filling the list of found
                    ListAdverts.add(advert);
                }

                Collections.shuffle(ListAdverts);
                AdvertAdapter adapter =  new AdvertAdapter(ListAdverts);
                recyclerListAdverts.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
            }
        });
    }// [consultListPetShops]

    /**
     * Method for querying database delivery items in Firebase
     */
    private void consultListDelivery() {
        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef = ref.child("delivery");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Advert advert;
                //clearing the previous Founds list
                ListAdverts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    advert=new Advert();

                    if (postSnapshot.child("name").exists()){
                        advert.setName(postSnapshot.child("name").getValue().toString());
                    }else { advert.setName(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("description").exists()){
                        advert.setDescription(postSnapshot.child("description").getValue().toString());
                    }else { advert.setDescription(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("phone").exists()){
                        advert.setPhone(postSnapshot.child("phone").getValue().toString());
                    }else { advert.setPhone(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("urlPage").exists()){
                        advert.setUrlPage(postSnapshot.child("urlPage").getValue().toString());
                    }else { advert.setUrlPage(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("image").exists()){
                        advert.setImage(postSnapshot.child("image").getValue().toString());
                    }

                    //We are filling the list of found
                    ListAdverts.add(advert);
                }

                Collections.shuffle(ListAdverts);
                AdvertAdapter adapter =  new AdvertAdapter(ListAdverts);
                recyclerListAdverts.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
            }
        });
    }// [consultListDelivery]

    /**
     * Method for querying database delivery items in Firebase
     */
    private void consultListEvents() {
        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef = ref.child("events");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Advert advert;
                //clearing the previous Founds list
                ListAdverts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    advert=new Advert();

                    if (postSnapshot.child("name").exists()){
                        advert.setName(postSnapshot.child("name").getValue().toString());
                    }else { advert.setName(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("description").exists()){
                        advert.setDescription(postSnapshot.child("description").getValue().toString());
                    }else { advert.setDescription(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("phone").exists()){
                        advert.setPhone(postSnapshot.child("phone").getValue().toString());
                    }else { advert.setPhone(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("urlPage").exists()){
                        advert.setUrlPage(postSnapshot.child("urlPage").getValue().toString());
                    }else { advert.setUrlPage(getString(R.string.field_without_info)); }
                    if (postSnapshot.child("image").exists()){
                        advert.setImage(postSnapshot.child("image").getValue().toString());
                    }

                    //We are filling the list of found
                    ListAdverts.add(advert);
                }

                Collections.shuffle(ListAdverts); // Show random listing
                AdvertAdapter adapter =  new AdvertAdapter(ListAdverts);
                recyclerListAdverts.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("List Services", getString(R.string.could_not_get_information));
            }
        });
    }// [consultListEvents]

    /**
     * Method to validate camera and external write permissions
     * @return boolean
     */
    private boolean validatePermissionsCall() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((getContext().checkSelfPermission(CALL_PHONE)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CALL_PHONE))){
            dialogRecommendationToGrantPermissionCall();
        }else{
            requestPermissions(new String[]{CALL_PHONE},104);
        }
        return false;
    }

    /**
     * Method to display permission recommendation dialog
     */
    private void dialogRecommendationToGrantPermissionCall() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getActivity());
        dialogo.setTitle(R.string.dialog_recommendation_permission_call_title);
        dialogo.setMessage(R.string.dialog_permission_call_manually_message);

        dialogo.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{CALL_PHONE},104);
            }
        });
        dialogo.show();
    }

    /**
     * Method to display permission request
     * @param requestCode request code solicited
     * @param permissions number of permits requested
     * @param grantResults number of results obtained
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_PERMISSIONS){
            if(grantResults.length==1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Utilities.PERMISSION_CALL = true;
            }else{
                manualPermitRequestCall().show();
            }
        }
    }

    /**
     * Method to display permission authorization dialog manually
     */
    private  AlertDialog manualPermitRequestCall(){
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
     * Method to rename the appbar
     * @return Returns the activity contained in the appbar
     */
    private ActionBar getActionBar() {
        return ((MainActivity) getActivity()).getSupportActionBar(); }
}
