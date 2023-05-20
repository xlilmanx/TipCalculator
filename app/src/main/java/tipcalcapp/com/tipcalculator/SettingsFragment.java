package tipcalcapp.com.tipcalculator;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {

        setPreferencesFromResource(R.xml.preferences, rootKey);

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

        final Preference default_tip = getPreferenceManager().findPreference(
                "default_tip");
        assert sp != null;
        assert default_tip != null;
        default_tip.setSummary(sp.getString("default_tip", "15"));
        default_tip.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                default_tip.setSummary(newValue.toString());
                return true;
            }
        });

        final Preference default_tax = getPreferenceManager().findPreference(
                "default_tax_percent");
        assert default_tax != null;
        default_tax.setSummary(sp.getString("default_tax_percent", "8.5"));
        default_tax.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                default_tax.setSummary(newValue.toString());
                return true;
            }
        });

    }

}
