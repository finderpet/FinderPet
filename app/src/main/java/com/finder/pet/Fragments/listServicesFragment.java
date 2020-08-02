package com.finder.pet.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.finder.pet.Adapters.AdvertAdapter;
import com.finder.pet.Entities.Advert;
import com.finder.pet.R;
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
public class listServicesFragment extends Fragment {

    private FoundFragment.OnFragmentInteractionListener mListener;

    RecyclerView recyclerListAdverts;
    ArrayList<Advert> ListAdverts;

    private ProgressBar progressBar;
    private TextView txtLoad;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef = ref.child("veterinary");


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

        consultListAdverts();
    }

    /**
     * Method for querying database items in Firebase
     */
    private void consultListAdverts() {
        progressBar.setVisibility(View.VISIBLE);
        txtLoad.setVisibility(View.VISIBLE);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Advert advert;
                //clearing the previous Founds list
                ListAdverts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    advert=new Advert();

                    advert.setName(postSnapshot.child("name").getValue().toString());
                    advert.setDescription(postSnapshot.child("description").getValue().toString());
                    advert.setPhone(postSnapshot.child("phone").getValue().toString());
                    advert.setUrlPage(postSnapshot.child("urlPage").getValue().toString());
                    advert.setImage(postSnapshot.child("image").getValue().toString());

                    //We are filling the list of found
                    ListAdverts.add(advert);
                }

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
                Toast.makeText(getContext(), "No se pudo obtener información ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
