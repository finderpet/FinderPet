package com.finder.pet.Fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finder.pet.Adapters.AdoptedAdapter;
import com.finder.pet.Entities.Adopted_Vo;
import com.finder.pet.R;
import com.finder.pet.Utilities.commonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdoptedFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerListAdopted;
    ArrayList<Adopted_Vo> ListAdopted;

    private ProgressBar progressBar;
    private TextView txtLoad;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef = ref.child("pet_adopted");

    public AdoptedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adopted, container, false);
        recyclerListAdopted=view.findViewById(R.id.id_recycler_adopted);
        recyclerListAdopted.setLayoutManager(new LinearLayoutManager(getContext()));
        ListAdopted=new ArrayList<>();

        progressBar =  view.findViewById(R.id.progressBarAdopted);
        txtLoad = view.findViewById(R.id.textLoad);

        consultListAdopted();

        Button btn = view.findViewById(R.id.btn_new_adopted);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_adoptedFragment_to_formAdoptedFragment);
            }
        });
        return view;
    }

    /**
     * Method for querying database items in Firebase
     */
    private void consultListAdopted() {

        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Adopted_Vo adoptedVo;
                //clearing the previous Founds list
                ListAdopted.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    adoptedVo=new Adopted_Vo();

                    if (postSnapshot.child("date").exists()){
                        try {
                            String timeDays = commonMethods.getDaysDate(postSnapshot.child("date").getValue().toString(), getContext());
                            adoptedVo.setDate(timeDays);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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

                    //We are filling the list of found
                    ListAdopted.add(adoptedVo);
                }

                Collections.reverse(ListAdopted);// reverse the order of the list
                AdoptedAdapter adapter =  new AdoptedAdapter(ListAdopted);
                recyclerListAdopted.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Event to call the DetailAdoptedFragment fragment passing it a bundle type object
                        Bundle bundle = new Bundle(); // Create the bundle to transport the object
                        bundle.putSerializable("objeto", ListAdopted.get(recyclerListAdopted.getChildAdapterPosition(view)));
                        findNavController(view).navigate(R.id.action_adoptedFragment_to_detailAdoptedFragment, bundle);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("List Adopted", getString(R.string.could_not_get_information));
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
