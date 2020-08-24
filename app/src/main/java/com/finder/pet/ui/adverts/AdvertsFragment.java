package com.finder.pet.ui.adverts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.finder.pet.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.navigation.Navigation.findNavController;

public class AdvertsFragment extends Fragment {

    CardView cardServices, cardProducts, cardDelivery, cardEvents;
    MaterialButton btnContactUs;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef = ref.child("urls_finder");

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
        btnContactUs = view.findViewById(R.id.btnContactUs);

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
        btnContactUs.setOnClickListener(new View.OnClickListener() {
            String contactUs;
            @Override
            public void onClick(View view) {

                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("contact_whatsapp").exists()){
                            // Get firebase contact information
                            contactUs = dataSnapshot.child("contact_whatsapp").getValue(String.class);
                            Uri uri = Uri.parse(contactUs);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }else {
                            // Get local contact information
                            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=+573008460707&text=Buenas%20tardes,%20quiero%20saber%20como%20mostrar%20mi%20negocio%20en%20FinderPet");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
                    }
                });
            }// [End onClick]
        });

    }// [End onViewCreated]
}