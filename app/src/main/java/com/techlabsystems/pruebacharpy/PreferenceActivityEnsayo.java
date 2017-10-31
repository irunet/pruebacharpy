package com.techlabsystems.pruebacharpy;

import java.util.List;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;


/**
 * Created by juanjo on 19/09/2017.
 */




public class PreferenceActivityEnsayo extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, new PreferenceFragmentEnsayo());
        transaction.commit();
    }

    public static class PreferenceFragmentEnsayo extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceManager manager = getPreferenceManager();
            //manager.setSharedPreferencesName("user_prefs");
            addPreferencesFromResource(R.xml.fragment_preference_ensayo);
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            String st;
            Preference pref = findPreference(key);


            if (pref instanceof ListPreference) {
                // Update display title
                // Write the description for the newly selected preference
                // in the summary field.
                ListPreference listPref = (ListPreference) pref;
                CharSequence listDesc = listPref.getEntry();
                if (!TextUtils.isEmpty(listDesc)) {
                    pref.setSummary(listDesc);
                }
            }else if (pref instanceof EditTextPreference) {
                pref.setSummary(sharedPreferences.getString(key, ""));
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            //Actualizo el sumario para que muestre los valores que están programados
            SharedPreferences shareprefs;
            shareprefs = getPreferenceManager().getSharedPreferences();
            onSharedPreferenceChanged(shareprefs, "EMAIL");
            onSharedPreferenceChanged(shareprefs, "UNIDADES");

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }




    }
}