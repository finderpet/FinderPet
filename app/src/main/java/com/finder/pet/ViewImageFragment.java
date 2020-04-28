package com.finder.pet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewImageFragment extends Fragment {

    public ViewImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_image, container, false);
        androidx.appcompat.widget.AppCompatImageView imgBig = view.findViewById(R.id.imageBig);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Your Title");

        assert getArguments() != null;
        String url = getArguments().getString("objeto"); // Recibimos el string conla url de la imagen

        // Cargamos la imagen en el imageView
        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .into(imgBig);
        return view;
    }
}
