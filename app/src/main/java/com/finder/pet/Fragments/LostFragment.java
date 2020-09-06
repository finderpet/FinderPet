package com.finder.pet.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finder.pet.Adapters.LostAdapter;
import com.finder.pet.Entities.Lost_Vo;
import com.finder.pet.R;
import com.finder.pet.Utilities.PreferencesApp;
import com.finder.pet.Utilities.commonMethods;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.navigation.Navigation.findNavController;

/**
 * A simple {@link Fragment} subclass.
 */
public class LostFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private FusedLocationProviderClient fusedLocationClient; // Get the last know location
    protected Location lastLocation;
    private static final int REQUEST_PERMISSIONS_LOCATION = 105;

    RecyclerView recyclerListLost;
    ArrayList<Lost_Vo> ListLost;

    private ProgressBar progressBar;
    private TextView txtLoad;

    Button btn;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef = ref.child("pet_lost");

    public LostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lost, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferenceManager.setDefaultValues(getContext(),R.xml.preferences,false);
        // Update user preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        PreferencesApp.getPreferences(preferences, getContext());

        setupViews(view);

        // Validate location permissions
        if(validatePermissions()){
            recyclerListLost.setVisibility(View.VISIBLE);
            consultListLost();
        }else{
            recyclerListLost.setVisibility(View.GONE);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_lostFragment_to_formLostFragment);
            }
        });
    }

    /**
     * Method to initialize the views
     * @param view View fragment
     */
    private void setupViews(View view) {
        recyclerListLost=view.findViewById(R.id.recycler_lost);
        recyclerListLost.setLayoutManager(new LinearLayoutManager(getContext()));
        ListLost=new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        progressBar =  view.findViewById(R.id.progressBarLost);
        txtLoad = view.findViewById(R.id.textLoad);
        btn = view.findViewById(R.id.btn_new_lost);
    }

    /**
     * Method to validate location permissions
     * @return Boolean with true if granted permissions or false if not
     */
    private boolean validatePermissions() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((getContext().checkSelfPermission(ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                && (getContext().checkSelfPermission(ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
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
                            // Update coordinates in preferences
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            PreferencesApp.setPreferenceLocation(preferences, getContext(),lastLocation.getLatitude(), lastLocation.getLongitude());

                        }
                    });
            return true;
        }

        if((shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION))
                || (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION))){
            dialogRecommendationToGrantPermissionLocation();
        }else{
            requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_LOCATION);
        }
        return false;
    }

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
                //getCurrentLocation();
                // Validate camera permissions and external write
                if(validatePermissions()){
                    recyclerListLost.setVisibility(View.VISIBLE);
                    consultListLost();
                }else{
                    recyclerListLost.setVisibility(View.GONE);
                }
            }else{
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
    private void consultListLost() {

        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Lost_Vo lostVo;
                //clearing the previous Founds list
                ListLost.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    lostVo=new Lost_Vo();

                    if (postSnapshot.child("date").exists()){
                        try {
                            String timeDays = commonMethods.getDaysDate(postSnapshot.child("date").getValue().toString(), getContext());
                            lostVo.setDate(timeDays);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else { lostVo.setDate(getString(R.string.time_date)); }

                    lostVo.setKeyPost(postSnapshot.getKey());
                    lostVo.setIdUser(postSnapshot.child("idUser").getValue().toString());
                    lostVo.setName(postSnapshot.child("name").getValue().toString());
                    lostVo.setMicrochip(postSnapshot.child("microchip").getValue().toString());
                    lostVo.setEmail(postSnapshot.child("email").getValue().toString());
                    lostVo.setImage1(postSnapshot.child("image1").getValue().toString()); //Acá traigo la dirección de la imagen, debo crear las otras en firebase
                    lostVo.setImage2(postSnapshot.child("image2").getValue().toString());
                    lostVo.setImage3(postSnapshot.child("image3").getValue().toString());
                    lostVo.setLocation(postSnapshot.child("location").getValue().toString());
                    lostVo.setObservations(postSnapshot.child("observations").getValue().toString());
                    lostVo.setPhone(postSnapshot.child("phone").getValue().toString());
                    lostVo.setType(postSnapshot.child("type").getValue().toString());
                    lostVo.setLatitude(Double.parseDouble((postSnapshot.child("latitude").getValue().toString())));
                    lostVo.setLongitude(Double.parseDouble((postSnapshot.child("longitude").getValue().toString())));

                    Location myLocation = new Location("mylocation");
                    myLocation.setLatitude(PreferencesApp.latDefault);
                    myLocation.setLongitude(PreferencesApp.lngDefault);
                    Location petLocation = new Location("petlocation");
                    petLocation.setLatitude(lostVo.getLatitude());
                    petLocation.setLongitude(lostVo.getLongitude());
                    float distance = myLocation.distanceTo(petLocation)/1000;
                    //Log.i("Distance", String.valueOf(distance));

                    if (distance<= PreferencesApp.search_radius){
                        //We are filling the list of lost
                        ListLost.add(lostVo);
                    }
                }

                Collections.reverse(ListLost);// reverse the order of the list
                LostAdapter adapter =  new LostAdapter(ListLost);
                recyclerListLost.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Event to call the DetailAdoptedFragment fragment passing it a bundle type object
                        Bundle bundle = new Bundle(); // Create the bundle to transport the object
                        bundle.putSerializable("objeto", ListLost.get(recyclerListLost.getChildAdapterPosition(view)));
                        findNavController(view).navigate(R.id.action_lostFragment_to_detailLostFragment, bundle);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("List Lost", getString(R.string.could_not_get_information));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
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
