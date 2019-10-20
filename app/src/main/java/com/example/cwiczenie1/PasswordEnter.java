package com.example.cwiczenie1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cwiczenie1.Services.AppLockService;
import com.example.cwiczenie1.database.AppDatabase;

public class PasswordEnter extends AppCompatActivity {

    AppElement appElement = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_enter);
        appElement = AppLockService.appActualToLockElement;
    }

    public void confirmButton(View view) {
        String oldPass = ((EditText) findViewById(R.id.passwordTextEdit)).getText().toString();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String realVal = settings.getString("PASSWORD", "");

        if (oldPass.contentEquals(realVal)) {
            appElement.enteredPass = true;
            AppDatabase appDatabase = new AppDatabase(null);
            appDatabase.updateElement(appElement);
            finish();
            return;
        }
        Toast.makeText(getApplicationContext(), "Password is wrong!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
