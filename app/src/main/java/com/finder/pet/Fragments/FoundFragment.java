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

import com.finder.pet.Adapters.FoundAdapter;
import com.finder.pet.Entities.Found_Vo;
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
public class FoundFragment extends Fragment{

    private OnFragmentInteractionListener mListener;

    private FusedLocationProviderClient fusedLocationClient; // Get the last know location
    protected Location lastLocation;
    private static final int REQUEST_PERMISSIONS_LOCATION = 105;

    RecyclerView recyclerListFound;
    ArrayList<Found_Vo> ListFound;

    private ProgressBar progressBar;
    private TextView txtLoad;

    Button btn;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef = ref.child("pet_found");

    public FoundFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_found, container, false);
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
            recyclerListFound.setVisibility(View.VISIBLE);
            consultListFounds();
        }else{
            recyclerListFound.setVisibility(View.GONE);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_foundFragment_to_formFoundFragment);
            }
        });
    }

    /**
     * Method to initialize the views
     * @param view View fragment
     */
    private void setupViews(View view) {
        recyclerListFound=view.findViewById(R.id.id_recycler_found);
        recyclerListFound.setLayoutManager(new LinearLayoutManager(getContext()));
        ListFound=new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        progressBar =  view.findViewById(R.id.progressBarFound);
        txtLoad = view.findViewById(R.id.textLoad);
        btn = view.findViewById(R.id.btnNewFound_id);
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
                    recyclerListFound.setVisibility(View.VISIBLE);
                    consultListFounds();
                }else{
                    recyclerListFound.setVisibility(View.GONE);
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
    private void consultListFounds() {

        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Found_Vo foundVo;
                //clearing the previous Founds list
                ListFound.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    foundVo=new Found_Vo();

                    if (postSnapshot.child("date").exists()){
                        try {
                            String timeDays = commonMethods.getDaysDate(postSnapshot.child("date").getValue().toString(), getContext());
                            foundVo.setDate(timeDays);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else { foundVo.setDate(getString(R.string.time_date)); }

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

                    Location myLocation = new Location("mylocation");
                    myLocation.setLatitude(PreferencesApp.latDefault);
                    myLocation.setLongitude(PreferencesApp.lngDefault);
                    Location petLocation = new Location("petlocation");
                    petLocation.setLatitude(foundVo.getLatitude());
                    petLocation.setLongitude(foundVo.getLongitude());
                    float distance = myLocation.distanceTo(petLocation)/1000;
                    Log.e("Distancia", String.valueOf(distance));

                    if (distance<= PreferencesApp.search_radius){
                        //We are filling the list of found
                        ListFound.add(foundVo);
                    }

                }

                Collections.reverse(ListFound);// reverse the order of the list
                FoundAdapter adapter =  new FoundAdapter(ListFound);
                recyclerListFound.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Event to call the DetailAdoptedFragment fragment passing it a bundle type object
                        Bundle bundle = new Bundle(); // Create the bundle to transport the object
                        bundle.putSerializable("objeto", ListFound.get(recyclerListFound.getChildAdapterPosition(view)));
                        findNavController(view).navigate(R.id.action_foundFragment_to_detailFoundFragment, bundle);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("List Found", getString(R.string.could_not_get_information));
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
