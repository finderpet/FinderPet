package com.finder.pet.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finder.pet.Adapters.FoundAdapter;
import com.finder.pet.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoundFragment extends Fragment {

    ArrayList<String> listFound;
    RecyclerView recyclerView;

    public FoundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_found, container, false);
        recyclerView=view.findViewById(R.id.id_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listFound=new ArrayList<String>();

        for (int i=0;i<=50;i++){
            listFound.add("Dato # "+i+" ");
        }

        FoundAdapter adapter=new FoundAdapter(listFound);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
