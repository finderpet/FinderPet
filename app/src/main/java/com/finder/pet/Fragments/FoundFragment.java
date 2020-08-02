package com.finder.pet.Fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.finder.pet.Adapters.FoundAdapter;
import com.finder.pet.Entities.Found_Vo;
import com.finder.pet.R;
import com.finder.pet.Utilities.Utilities;
import com.finder.pet.Utilities.commonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoundFragment extends Fragment{

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerListFound;
    ArrayList<Found_Vo> ListFound;

    private ProgressBar progressBar;
    private TextView txtLoad;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef = ref.child("pet_found");

    public FoundFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_found, container, false);
        recyclerListFound=view.findViewById(R.id.id_recycler_found);
        recyclerListFound.setLayoutManager(new LinearLayoutManager(getContext()));
        ListFound=new ArrayList<>();

        progressBar =  view.findViewById(R.id.progressBarFound);
        txtLoad = view.findViewById(R.id.textLoad);

        consultListFounds();

        Button btn = view.findViewById(R.id.btnNewFound_id);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findNavController(view).navigate(R.id.action_foundFragment_to_formFoundFragment);

            }
        });
        return view;
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
                        String timeDays = commonMethods.getDaysDate(postSnapshot.child("date").getValue().toString());
                        foundVo.setDate(timeDays);
                    }else { foundVo.setDate("Sin fecha"); }

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

                    //We are filling the list of found
                    ListFound.add(foundVo);
                }

                FoundAdapter adapter =  new FoundAdapter(ListFound);
                recyclerListFound.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Evento para llamar al fragment DetailFoundFragment pasandole un objeto tipo bundle
                        Bundle bundle = new Bundle(); //Creamos el bundle para transportar al objeto
                        bundle.putSerializable("objeto", ListFound.get(recyclerListFound.getChildAdapterPosition(view))); //Pasamos al bundle el objeto especifico
                        findNavController(view).navigate(R.id.action_foundFragment_to_detailFoundFragment, bundle); //Ejecutamos el action junto con el bundle
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "No se pudo obtener información ", Toast.LENGTH_SHORT).show();
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
