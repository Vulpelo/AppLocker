package com.example.cwiczenie1.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cwiczenie1.AppElement;

public class AppDatabase {
    private static AppLockerDbHelper dbHelper = null;

    private static final String[] projection = {
            "ID_APP",
            "APP_NAME",
            "PROTECTED"
    };

    public AppDatabase(Context context) {
        if (dbHelper == null) {
            dbHelper = new AppLockerDbHelper(context);
        }
    }

    /* Get data from database */
    public AppElement getByName(String name) {
        SQLiteDatabase dbR = dbHelper.getReadableDatabase();
        AppElement appElement = null;

        String[] parameters = {name};
        Cursor cursor = dbR.query("APPS", projection, "APP_NAME = ?", parameters, null, null, null, "1");
        // getting first matching row
        if (cursor.moveToNext()) {
            appElement = new AppElement(name);
            appElement.id = cursor.getLong(cursor.getColumnIndexOrThrow("ID_APP"));
            appElement.isProtected = cursor.getInt(cursor.getColumnIndexOrThrow("PROTECTED")) > 0;
        }
        return appElement;
    }
}
