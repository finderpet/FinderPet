package com.finder.pet.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.finder.pet.Adapters.FoundAdapter;
import com.finder.pet.Entities.Found_Vo;
import com.finder.pet.R;
import com.finder.pet.Utilities.Utilities;
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
public class FoundFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerListFound;
    ArrayList<Found_Vo> ListFound;

    private ProgressBar progressBar;

    //Comunicación entre fragments
    //Activity activity;
    //IComunicaFragments interfaceComunicaFragments;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef = ref.child("foods");

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
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Found_Vo foundVo;
                //clearing the previous Founds list
                ListFound.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    foundVo=new Found_Vo();

                    foundVo.setName(postSnapshot.child("name").getValue().toString());
                    Utilities.WG_NAME=postSnapshot.child("name").getValue().toString();
                    foundVo.setDescription(postSnapshot.child("description").getValue().toString());
                    foundVo.setImage(postSnapshot.child("image").getValue().toString());
                    foundVo.setType(postSnapshot.child("type").getValue().toString());
                    Utilities.WG_TYPE=postSnapshot.child("type").getValue().toString();
                    foundVo.setTime(postSnapshot.child("time").getValue().toString());
                    foundVo.setPrice(postSnapshot.child("price").getValue().toString());
                    Utilities.WG_PRICE=postSnapshot.child("price").getValue().toString();

                    SharedPreferences preferences = getActivity().getSharedPreferences("credentials", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("wg_name", Utilities.WG_NAME);
                    editor.putString("wg_type", Utilities.WG_TYPE);
                    editor.putString("wg_price", Utilities.WG_PRICE);
                    editor.commit();

                    //We are filling the list of found
                    ListFound.add(foundVo);
                }

                FoundAdapter adapter =  new FoundAdapter(ListFound);
                recyclerListFound.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
//                adapter.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        //evento para llamar DetailplateFragment
//                        interfaceComunicaFragments.sendFood
//                                (ListFound.get(recyclerListFound.getChildAdapterPosition(view)));
//                    }
//                });
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
