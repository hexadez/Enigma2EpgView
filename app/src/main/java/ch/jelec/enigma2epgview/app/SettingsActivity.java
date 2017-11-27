package ch.jelec.enigma2epgview.app;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference ipaddressPref = findPreference(getString(R.string.preference_ipaddress_key));
        ipaddressPref.setOnPreferenceChangeListener(this);

        // onPreferenceChange sofort aufrufen mit der in SharedPreferences gespeicherten Aktienliste
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedIpAddress = sharedPrefs.getString(ipaddressPref.getKey(), "");
        onPreferenceChange(ipaddressPref, savedIpAddress);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        preference.setSummary(value.toString());
        return false;
    }

}
