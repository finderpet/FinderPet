package com.finder.pet.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.finder.pet.Adapters.AdvertAdapter;
import com.finder.pet.Entities.Advert;
import com.finder.pet.R;
import com.finder.pet.Utilities.commonMethods;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static androidx.navigation.Navigation.findNavController;
import static androidx.navigation.Navigation.setViewNavController;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class HomeFragment extends Fragment{

    CardView cardViewFound, cardViewLost, cardViewAdopted, cardViewMap;
    private ViewFlipper viewFlipper;
//    // [END declare_auth]
//    private AdView adView;
//    FrameLayout adContainerView;
    private ArrayList<Advert> imagesUrl;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        // Initialize the Mobile Ads SDK.
//        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {}
//        });

        imagesUrl = new ArrayList<>();
        consultListImageEvents();

//        //Banner adaptativo
//        adContainerView = view.findViewById(R.id.ad_view_container);
//        // Step 1 - Create an AdView and set the ad unit ID on it.
//        adView = new AdView(getContext());
//        adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id));
//        adContainerView.addView(adView);
//        loadBanner();

        // This callback will only be called when MyFragment is at least Started.
        //Con este método manejamos el evento de boton atras solamente en este fragment
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                commonMethods.dialogCloseApp(getContext()).show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        cardViewFound = view.findViewById(R.id.id_cardViewFound);
        cardViewFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_foundFragment);
            }
        });

        cardViewLost = view.findViewById(R.id.id_cardViewLost);
        cardViewLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_lostFragment);
            }
        });

        cardViewAdopted = view.findViewById(R.id.id_cardViewAdopted);
        cardViewAdopted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_adoptedFragment);
            }
        });
        cardViewMap = view.findViewById(R.id.id_cardViewMap);
        cardViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_nav_map);
            }
        });

        viewFlipper = view.findViewById(R.id.flipperAdverts);
        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Object", "Events");
                findNavController(v).navigate(R.id.action_nav_home_to_listServicesFragment, bundle);
            }
        });

    }// [End onViewCreated]

    private void setViewFlipper(ArrayList<Advert> imgUrl){
        for (Advert advert: imgUrl){
            flipperImg(advert.getImage());
        }
    }

    /**
     * Method for querying database image news and events in Firebase
     */
    private void consultListImageEvents() {
        databaseRef = ref.child("events");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Advert advert;
                //clearing the previous list
                imagesUrl.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    advert=new Advert();
                    if (postSnapshot.child("image").exists()){
                        advert.setImage(postSnapshot.child("image").getValue().toString());
                    }
                    //We are filling the list
                    imagesUrl.add(advert);
                }

                Collections.shuffle(imagesUrl); // Show random listing
                setViewFlipper(imagesUrl);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("List Services", getString(R.string.could_not_get_information));
            }
        });
    }// [consultListImageEvents]

//    /**
//     * Method to load banner ads adaptative
//     */
//    private void loadBanner() {
//        // Create an ad request. Check your logcat output for the hashed device ID
//        // to get test ads on a physical device, e.g.,
//        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
//        // device."
//        AdRequest adRequest =
//                new AdRequest.Builder().build();
//
//        AdSize adSize = getAdSize();
//        // Step 4 - Set the adaptive ad size on the ad view.
//        adView.setAdSize(adSize);
//
//        // Step 5 - Start loading the ad in the background.
//        adView.loadAd(adRequest);
//    }
//
//    /**
//     * Method to calculate the screen width
//     * @return screen width
//     */
//    private AdSize getAdSize() {
//        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        display.getMetrics(outMetrics);
//
//        float widthPixels = outMetrics.widthPixels;
//        float density = outMetrics.density;
//
//        int adWidth = (int) (widthPixels / density);
//
//        // Step 3 - Get adaptive ad size and return for setting on the ad view.
//        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
//    }

    /**
     * Metodo para mostrar el flipper de imagenes de promociones
     * @param imageUrl recibe un parametro de tipo string con la url de la imagen a mostrar
     */
    public void flipperImg(String imageUrl){
        ImageView imageView = new ImageView(getContext());
        //imageView.setCropToPadding(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundColor(Color.parseColor("#ffffff"));
        Glide.with(getActivity())
                .load(imageUrl)
                .placeholder(R.drawable.sin_imagen)
                //.centerCrop()
                .into(imageView);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();
        viewFlipper.setInAnimation(getContext(), android.R.anim.fade_in);
        viewFlipper.setOutAnimation(getContext(), android.R.anim.fade_out);
    }


}