package com.finder.pet.Fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finder.pet.Main.MainActivity;
import com.finder.pet.R;

import static android.text.Html.FROM_HTML_MODE_LEGACY;


/**
 * A simple {@link Fragment} subclass.
 */
public class informationShowFragment extends Fragment {

    public informationShowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_information_show, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvShow = view.findViewById(R.id.textShow);
        String textToShow = getArguments().getString("Object");

        if (textToShow.equals("Policy")){
            // Damos formato HTML al texto de políticas de datos, solo funciona de api 24 hacia adelante
            String text = getString(R.string.data_policy);
            Spanned styledText = Html.fromHtml(text, FROM_HTML_MODE_LEGACY);
            tvShow.setText(styledText);
            getActionBar().setTitle(R.string.label_data_policy);
        }
        if (textToShow.equals("Terms")){
            // Damos formato HTML al texto de políticas de datos, solo funciona de api 24 hacia adelante
            String text = getString(R.string.data_policy);
            Spanned styledText = Html.fromHtml(text, FROM_HTML_MODE_LEGACY);
            tvShow.setText(styledText);
            getActionBar().setTitle(R.string.terms_use);
        }
        if (textToShow.equals("About")){
            // Damos formato HTML al texto de políticas de datos, solo funciona de api 24 hacia adelante
            String text = getString(R.string.about_finder_pet);
            Spanned styledText = Html.fromHtml(text, FROM_HTML_MODE_LEGACY);
            tvShow.setText(styledText);
            getActionBar().setTitle(R.string.about_app);
        }
        if (textToShow.equals("Developers")){
            // Damos formato HTML al texto de políticas de datos, solo funciona de api 24 hacia adelante
            String text = getString(R.string.about_developers);
            Spanned styledText = Html.fromHtml(text, FROM_HTML_MODE_LEGACY);
            tvShow.setText(styledText);
            getActionBar().setTitle(R.string.developers);
        }


    }

    /**
     * Method to rename the appbar
     * @return Returns the activity contained in the appbar
     */
    private ActionBar getActionBar() {
        return ((MainActivity) getActivity()).getSupportActionBar(); }
}
