package com.finder.pet.ui.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.finder.pet.R;
import com.finder.pet.ui.account.AccountFragment;

import static androidx.navigation.Navigation.findNavController;

public class SettingsFragment extends PreferenceFragmentCompat {

    Fragment frag;
    String country;

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Referenciamos este fragment al xml donde configuramos la preferencias
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference prefAccount = findPreference("account");
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

        Preference prefNotifications = findPreference("notifications");
        prefNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
                startActivity(intent);
                return false;
            }
        });

//        final Preference prefCity = findPreference("city_state");
//        prefCity.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//                String mCountry = preferences.getString("country", "Colombia");
//                String mCity = preferences.getString("city_state", "Medellín");
//                Toast.makeText(getContext(),mCity,Toast.LENGTH_SHORT).show();
//                ListPreference listPreference = findPreference("city_state");
//                assert listPreference != null;
//                if (mCountry.equals("México")){
//                    listPreference.setEntries(R.array.Mexico);
//                    listPreference.setEntryValues(R.array.Mexico);
//                }else if (mCountry.equals("Colombia")){
//                    listPreference.setEntries(R.array.Colombia);
//                    listPreference.setEntryValues(R.array.Colombia);
//                }
//
//                //getPreferenceScreen().addPreference(multiSelectListPreference);
//
////                SharedPreferences.Editor editor = preferences.edit();
////                editor.putString("flag", "1");
////                editor.apply();
////                editor.commit();
//                return false;
//            }
//        });

    }


}