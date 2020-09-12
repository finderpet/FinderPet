package com.finder.pet.ui.information;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finder.pet.R;
import com.google.android.material.button.MaterialButton;

import static androidx.navigation.Navigation.findNavController;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformationFragment extends Fragment {

    private MaterialButton btnDataPolicy, btnUserTerms, btnaboutApp, btnInfoDev;

    public InformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);

        btnDataPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle(); //Creamos el bundle para transportar al objeto
                bundle.putString("Object", "Policy");
                findNavController(v).navigate(R.id.action_nav_info_to_dataPolicyFragment, bundle);
            }
        });
        btnUserTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle(); //Creamos el bundle para transportar al objeto
                bundle.putString("Object", "Terms");
                findNavController(v).navigate(R.id.action_nav_info_to_dataPolicyFragment, bundle);
            }
        });
        btnaboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNavController(v).navigate(R.id.action_nav_info_to_aboutAppFragment);
            }
        });
//        btnInfoDev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle(); //Creamos el bundle para transportar al objeto
//                bundle.putString("Object", "Developers");
//                findNavController(v).navigate(R.id.action_nav_info_to_dataPolicyFragment, bundle);
//            }
//        });
    }

    private void setupView(View view) {
        btnDataPolicy = view.findViewById(R.id.dataPolicies);
        btnUserTerms = view.findViewById(R.id.userTerms);
        btnaboutApp = view.findViewById(R.id.aboutApp);
        //btnInfoDev = view.findViewById(R.id.infoDevelopers);
    }
}
