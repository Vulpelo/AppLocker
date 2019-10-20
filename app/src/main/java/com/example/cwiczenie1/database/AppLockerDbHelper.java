package com.example.cwiczenie1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockerDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE = "CREATE TABLE APPS ( " +
            "ID_APP INTEGER PRIMARY KEY," +
            "APP_NAME TEXT," +
            "PROTECTED  INTEGER DEFAULT 0," +
            "RESET_WHEN INTEGER DEFAULT 0," +
            "ENTERED_PASS INTEGER DEFAULT 0)";

    private static final String SQL_DROP = "DROP TABLE IF EXISTS APPS;";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AppLocker.db";


    public AppLockerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
