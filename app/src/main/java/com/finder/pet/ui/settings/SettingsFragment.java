package com.finder.pet.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.finder.pet.Fragments.preferencesInitialFragment;
import com.finder.pet.R;
import com.finder.pet.ui.account.AccountFragment;

import static androidx.navigation.Navigation.findNavController;

public class SettingsFragment extends PreferenceFragmentCompat {

    Fragment frag;

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Referenciamos este fragment al xml donde configuramos la preferencias
        setPreferencesFromResource(R.xml.preferences, rootKey);



        Preference prefAccount = (Preference) findPreference("account");
        prefAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                frag = new AccountFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, frag);
                transaction.addToBackStack(null).commit();
                return false;
            }
        });
//        Preference prefAbout = (Preference) findPreference("about");
//        prefAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                frag = new About();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.nav_host_fragment, frag);
//                transaction.addToBackStack(null).commit();
//                return false;
//            }
//        });

    }


}