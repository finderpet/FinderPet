package com.finder.pet.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.finder.pet.R;

import static androidx.navigation.Navigation.findNavController;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button btn;
    private CardView cardViewFound, cardViewLost, cardViewAdopted;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //Acá podemos colocar la lógica que queremos cuando cambie a este fragment
            }
        });

        btn = root.findViewById(R.id.btn_pager);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_tabsActivity);
            }
        });

        cardViewFound = root.findViewById(R.id.id_cardViewFound);
        cardViewFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_foundFragment);
            }
        });

        cardViewLost = root.findViewById(R.id.id_cardViewLost);
        cardViewLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_lostFragment);
            }
        });

        cardViewAdopted = root.findViewById(R.id.id_cardViewAdopted);
        cardViewAdopted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_adoptedFragment);
            }
        });

        return root;
    }
}