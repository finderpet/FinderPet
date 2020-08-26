package com.finder.pet.Fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.finder.pet.Main.MainActivity;
import com.finder.pet.R;


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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvShow = view.findViewById(R.id.textShow);
        String textToShow = getArguments().getString("Object");

        if (textToShow.equals("Policy")){
            tvShow.setText(Html.fromHtml(getResources().getString(R.string.data_policy)));
            getActionBar().setTitle(R.string.label_data_policy);
        }
        if (textToShow.equals("Terms")){
            tvShow.setText(Html.fromHtml(getResources().getString(R.string.terms_of_use)));
            getActionBar().setTitle(R.string.label_terms_use);
        }
        if (textToShow.equals("About")){
            tvShow.setText(Html.fromHtml(getResources().getString(R.string.about_finder_pet)));
            getActionBar().setTitle(R.string.about_app);
        }
        if (textToShow.equals("Developers")){
            tvShow.setText(Html.fromHtml(getResources().getString(R.string.about_developers)));
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
