package com.finder.pet.Main.ui.tutorial;

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

import com.finder.pet.Fragments.tutorialDonateFragment;
import com.finder.pet.Fragments.tutorialHelpFragment;
import com.finder.pet.Fragments.tutorialMainFragment;
import com.finder.pet.Fragments.tutorialMapsFragment;
import com.finder.pet.Fragments.tutorialNavFragment;
import com.finder.pet.Fragments.tutorialSettingsFragment;
import com.finder.pet.Fragments.tutorialWelcomeFragment;
import com.finder.pet.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static Fragment newInstance(int index) {
        Fragment fragment = null;

        switch (index){
            case 1:fragment=new tutorialWelcomeFragment(); break;
            case 2:fragment=new tutorialSettingsFragment(); break;
            case 3:fragment=new tutorialMainFragment(); break;
            case 4:fragment=new tutorialNavFragment(); break;
            case 5:fragment=new tutorialMapsFragment(); break;
            case 6:fragment=new tutorialHelpFragment(); break;
            case 7:fragment=new tutorialDonateFragment(); break;
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_container_tutorial, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}