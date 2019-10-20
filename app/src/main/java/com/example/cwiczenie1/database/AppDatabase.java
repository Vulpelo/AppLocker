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
            "PROTECTED",
            "RESET_WHEN",
            "ENTERED_PASS"
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
            appElement.resetWhen = ResetWhen.values()[
                    cursor.getInt(cursor.getColumnIndexOrThrow("RESET_WHEN"))
                    ];
            appElement.enteredPass = cursor.getInt(cursor.getColumnIndexOrThrow("ENTERED_PASS")) > 0;
        }
        return appElement;
    }

    public long updateElement(AppElement appElement) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("PROTECTED", appElement.isProtected ? 1:0);
        values.put("ENTERED_PASS", appElement.enteredPass ? 1:0);
        values.put("RESET_WHEN", appElement.resetWhen.ordinal());

        String[] selectionArgs = { String.valueOf(appElement.id) };
        return db.update(
                "APPS",
                values,
                "ID_APP LIKE ?",
                selectionArgs);
    }

    public long insertElement(AppElement appElement) {
        // no data was gotten then insert appElement to db
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("APP_NAME", appElement.name);
        values.put("PROTECTED", 0);
        values.put("ENTERED_PASS", appElement.enteredPass ? 1:0);
        values.put("RESET_WHEN", appElement.resetWhen.ordinal());
        return db.insert("APPS", null, values);
    }

    // Resets if nessessary
    public void resetEnteredPasswordWhen(ResetWhen resetWhen) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ENTERED_PASS", 0);
        String[] selectionArgs = { String.valueOf( resetWhen.ordinal() ) };

        db.update(
                "APPS",
                values,
                " RESET_WHEN = ?",
                selectionArgs);
    }

    public void resetEnteredPassword() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ENTERED_PASS", 0);
        db.update(
                "APPS",
                values,
                " RESET_WHEN = ?",
                null);
    }
}
