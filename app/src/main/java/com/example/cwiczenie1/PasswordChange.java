package com.example.cwiczenie1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordChange extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);


    }

    public void confirmButton(View view) {
        String oldPass = ((EditText)findViewById(R.id.oldPasswordEditText)).getText().toString();
        String newPass = ((EditText)findViewById(R.id.newPasswordEditText)).getText().toString();
        String reNewPass = ((EditText)findViewById(R.id.reNewPasswordEditText)).getText().toString();


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String realOldVal = settings.getString("PASSWORD", "");

        if (! oldPass.contentEquals(realOldVal)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Old password is wrong!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (! newPass.contentEquals(reNewPass)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Entered passwords do not match!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("PASSWORD", newPass);
    }
}
