package com.finder.pet.ui.adverts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.finder.pet.R;

import static androidx.navigation.Navigation.findNavController;

public class AdvertsFragment extends Fragment {

    CardView cardServices, cardProducts, cardDelivery, cardEvents;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_adverts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardServices = view.findViewById(R.id.cardViewServices);
        cardProducts = view.findViewById(R.id.cardViewProducts);
        cardDelivery = view.findViewById(R.id.cardViewDelivery);
        cardEvents = view.findViewById(R.id.cardViewEvents);

        cardServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Object", "Services");
                findNavController(view).navigate(R.id.action_nav_advert_to_listServicesFragment, bundle);
            }
        });
        cardProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Object", "Products");
                findNavController(view).navigate(R.id.action_nav_advert_to_listServicesFragment, bundle);
            }
        });
        cardDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Object", "Delivery");
                findNavController(view).navigate(R.id.action_nav_advert_to_listServicesFragment, bundle);
            }
        });
        cardEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Object", "Events");
                findNavController(view).navigate(R.id.action_nav_advert_to_listServicesFragment, bundle);
            }
        });


    }
}