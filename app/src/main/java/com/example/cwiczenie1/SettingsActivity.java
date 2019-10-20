package com.example.cwiczenie1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.cwiczenie1.database.AppDatabase;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference myPref = (Preference) findPreference("reset");

            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getResources().getString(R.string.reset))
                            .setMessage(getResources().getString(R.string.confirmReset))
                            .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppDatabase appDatabase = new AppDatabase(getContext());
                                    appDatabase.resetToDefault();

                                    Toast.makeText(getContext(), getResources().getString(R.string.reseted), Toast.LENGTH_SHORT).show();
                                }

                            })
                            .setNegativeButton(getResources().getString(R.string.no), null)
                            .show();
                    return true;
                }
            });


        }
    }
}