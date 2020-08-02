package com.finder.pet.ui.donate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.finder.pet.R;
import com.google.android.material.button.MaterialButton;

import static androidx.navigation.Navigation.findNavController;

public class DonateFragment extends Fragment {

    MaterialButton btnDonate;

    private DonateViewModel donateViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donate, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_donate_to_detailsProductsFragment);
            }
        });

    }

    private void setupViews(View view) {
        btnDonate = view.findViewById(R.id.btnDonate);
    }
}