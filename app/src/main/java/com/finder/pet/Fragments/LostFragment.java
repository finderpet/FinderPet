package com.finder.pet.Fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.finder.pet.Adapters.LostAdapter;
import com.finder.pet.Entities.Lost_Vo;
import com.finder.pet.R;
import com.finder.pet.Utilities.Utilities;
import com.finder.pet.Utilities.commonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class LostFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerListLost;
    ArrayList<Lost_Vo> ListLost;

    private ProgressBar progressBar;
    private TextView txtLoad;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef = ref.child("pet_lost");

    public LostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lost, container, false);
        recyclerListLost=view.findViewById(R.id.recycler_lost);
        recyclerListLost.setLayoutManager(new LinearLayoutManager(getContext()));
        ListLost=new ArrayList<>();

        progressBar =  view.findViewById(R.id.progressBarLost);
        txtLoad = view.findViewById(R.id.textLoad);

        consultListLost();

        Button btn = view.findViewById(R.id.btn_new_lost);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findNavController(view).navigate(R.id.action_lostFragment_to_formLostFragment);

            }
        });
        return view;
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
                        String timeDays = commonMethods.getDaysDate(postSnapshot.child("date").getValue().toString());
                        lostVo.setDate(timeDays);
                    }else { lostVo.setDate("Sin fecha"); }

                    lostVo.setName(postSnapshot.child("name").getValue().toString());
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

                    //We are filling the list of found
                    ListLost.add(lostVo);
                }

                LostAdapter adapter =  new LostAdapter(ListLost);
                recyclerListLost.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                txtLoad.setVisibility(View.GONE);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Evento para llamar al fragment DetailFoundFragment pasandole un objeto tipo bundle
                        Bundle bundle = new Bundle(); //Creamos el bundle para transportar al objeto
                        bundle.putSerializable("objeto", ListLost.get(recyclerListLost.getChildAdapterPosition(view))); //Pasamos al bundle el objeto especifico
                        findNavController(view).navigate(R.id.action_lostFragment_to_detailLostFragment, bundle); //Ejecutamos el action junto con el bundle
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
