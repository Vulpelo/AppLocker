package com.example.cwiczenie1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Dialog dialog;

    Button buttonAppList;
    Button buttonSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSettings = (Button)findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            Intent activityChangeIntent = new Intent(view.getContext(), SettingsActivity.class);
            startActivity(activityChangeIntent);
        }
        });

        buttonAppList = (Button)findViewById(R.id.buttonLockApps);
        buttonAppList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent activityChangeIntent = new Intent(view.getContext(), AppsList.class);
                startActivity(activityChangeIntent);
            }
        });

        Log.w("Cwiczenie1", "Hello");

        // Starting lock service
        Intent service = new Intent(this, AppLockService.class);
        startService(service);

        dialog = new Dialog(this);
    }

    public void setNewPasswordButton(View view) {
        dialog.setContentView(R.layout.activity_password_change);
        dialog.show();
    }

    public void confirmButton(View view) {

        String oldPass = ((EditText)dialog.findViewById(R.id.oldPasswordEditText)).getText().toString();
        String newPass = ((EditText)dialog.findViewById(R.id.newPasswordEditText)).getText().toString();
        String reNewPass = ((EditText)dialog.findViewById(R.id.reNewPasswordEditText)).getText().toString();


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String realOldVal = settings.getString("PASSWORD", "");

        if (! oldPass.contentEquals(realOldVal)) {
            quickMessage("Old password is wrong!");
            return;
        }

        if (! newPass.contentEquals(reNewPass)) {
            quickMessage("Entered passwords do not match!");
            return;
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("PASSWORD", newPass);
        editor.apply();

        quickMessage("Password changed");

        dialog.dismiss();
    }

    private void quickMessage(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
