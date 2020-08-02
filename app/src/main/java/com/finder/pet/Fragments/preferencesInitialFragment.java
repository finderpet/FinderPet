package com.finder.pet.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finder.pet.R;
import com.google.android.material.button.MaterialButton;

public class preferencesInitialFragment extends PreferenceFragmentCompat {

    public preferencesInitialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_first, rootKey);
    }

}