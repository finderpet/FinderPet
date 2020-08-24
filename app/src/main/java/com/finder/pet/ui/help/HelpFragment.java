package com.finder.pet.ui.help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finder.pet.Main.ContainerTutorialActivity;
import com.finder.pet.R;
import com.google.android.material.button.MaterialButton;

import static androidx.navigation.Navigation.findNavController;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {

    MaterialButton btnUserManual, btnTutorial, btnPqr, btnVideoTutorial, btnInfoProblem;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);

        btnUserManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://firebasestorage.googleapis.com/v0/b/finderpet-2cd1d.appspot.com/o/documents%2FTutorial%20detalle%20marcador%20-%20JocarSF.pdf?alt=media&token=23158512-d334-4a97-9f9e-2a94436d72d7";
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ContainerTutorialActivity.class);
                startActivity(intent);
            }
        });
        btnPqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_help_to_nav_pqr);
            }
        });
        btnInfoProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_help_to_reportProblemFragment);
            }
        });
        btnVideoTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://youtu.be/FbuitEfS8gM");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void setupViews(View view) {
        btnUserManual = view.findViewById(R.id.btnUserManual);
        btnTutorial = view.findViewById(R.id.btnTutorial);
        btnPqr = view.findViewById(R.id.btnPqr);
        btnVideoTutorial = view.findViewById(R.id.btnSeeVideoTutorial);
        btnInfoProblem = view.findViewById(R.id.btnInfoProblem);
    }
}
