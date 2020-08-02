package com.finder.pet.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finder.pet.Adapters.PagerPhotoAdapter;
import com.finder.pet.R;
import com.finder.pet.Utilities.ZoomOutPageTransformer;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagerPhotoFragment extends Fragment {
    private String[] imageUrls;
    private ViewPager viewPager;
    private LinearLayout layoutPoint;
    private TextView[] pointSlide;
    private ImageButton btnClose;

    public PagerPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();//Hide the ActionBar
        return inflater.inflate(R.layout.fragment_pager_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;


        //initialize views
        setupViews(view);

        // Recibimos el string con la url de la imagen
        String url1 = getArguments().getString("objeto1");
        String url2 = getArguments().getString("objeto2");
        String url3 = getArguments().getString("objeto3");
        imageUrls = new String[]{url1,url2,url3};

        // Close view photos
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        addPointsIndicator(0);

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        PagerPhotoAdapter adapter = new PagerPhotoAdapter(getContext(), imageUrls);
        viewPager.setAdapter(adapter);
    }

    /**
     * Method to initialize views
     * @param view
     */
    private void setupViews(View view) {
        viewPager = view.findViewById(R.id.pager_photo);
        btnClose = view.findViewById(R.id.closePhoto);
        layoutPoint = view.findViewById(R.id.linearPoints);
        viewPager.addOnPageChangeListener(viewListener);
    }

    //Indicador de puntos para las imagenes
    private void addPointsIndicator(int pos) {
        pointSlide = new TextView[3];
        layoutPoint.removeAllViews();
        for (int i=0;i<pointSlide.length;i++){
            pointSlide[i] = new TextView(getContext());
            pointSlide[i].setText(Html.fromHtml("&#8226;"));
            pointSlide[i].setTextSize(45);
            pointSlide[i].setTextColor(getResources().getColor(R.color.colorAccent));
            layoutPoint.addView(pointSlide[i]);
        }
        if (pointSlide.length>0){
            pointSlide[pos].setTextColor(getResources().getColor(R.color.colorWhite));
            pointSlide[pos].setTextSize(50);
        }
    }

   ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
       @Override
       public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

       }

       @Override
       public void onPageSelected(int position) {
           addPointsIndicator(position);
       }

       @Override
       public void onPageScrollStateChanged(int state) {

       }
   };

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }


}
