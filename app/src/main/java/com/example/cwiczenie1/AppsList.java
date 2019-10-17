package com.example.cwiczenie1;

import androidx.fragment.app.FragmentActivity;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwiczenie1.database.AppLockerDbHelper;

import java.util.ArrayList;
import java.util.List;

public class AppsList extends FragmentActivity implements BlankFragment.OnFragmentInteractionListener {

    FragmentManager fm;

    PackageManager pm;
    List<ApplicationInfo> apps;

    ArrayList<AppElement> appElementArrayList;
    AppElementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        AppLockerDbHelper dbHelper = new AppLockerDbHelper(getBaseContext());

        pm = getPackageManager();
        apps = pm.getInstalledApplications(0);

        fm = getFragmentManager();

        List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();

        for(ApplicationInfo app : apps) {
            //checks for flags; if flagged, check if updated system app
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                installedApps.add(app);
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //Discard this one
                installedApps.add(app);
                //in this case, it should be a user-installed app
            } else {
                installedApps.add(app);
            }
        }

        /* **************** */
        /* Setting ListView */
        appElementArrayList = new ArrayList<>();
        adapter = new AppElementAdapter(this, appElementArrayList);

        SQLiteDatabase dbR = dbHelper.getReadableDatabase();
        String[] projection = {
                "ID_APP",
                "APP_NAME",
                "PROTECTED"
        };

        for (ApplicationInfo appInfo: installedApps) {
            AppElement appElement = new AppElement(appInfo.processName);


            /* Get data from database */
            String[] parameters = {appElement.name};
            Cursor cursor = dbR.query("APPS", projection, "APP_NAME = ?", parameters, null, null, null, "1");
            // getting first matching row
            if (cursor.moveToNext()) {
                appElement.id = cursor.getLong(cursor.getColumnIndexOrThrow("ID_APP"));
                appElement.isProtected = cursor.getInt(cursor.getColumnIndexOrThrow("PROTECTED")) > 0;
            } else {
                // no data was gotten then insert appElement to db
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("APP_NAME", appElement.name);
                values.put("PROTECTED", 0);
                appElement.id = db.insert("APPS", null, values);
            }

            adapter.add(appElement);
        }

        /* Reading from database */
        Cursor cursor = dbR.query("APPS", projection, null, null, null, null, null);
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow("ID_APP"));
            itemIds.add(itemId);
            Log.w("database", String.valueOf(itemId).toString());
        }
        cursor.close();

        ListView listView = (ListView) findViewById(R.id.listViewApps);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppElement appElement = adapter.getItem(position);

                Toast.makeText(getBaseContext(), appElement.name, Toast.LENGTH_SHORT).show();

                View appFragView = findViewById(R.id.appFragment);
                TextView appFragmentName = appFragView.findViewById(R.id.appFragmentName);
                Switch protectedSwitch = appFragView.findViewById(R.id.protectedSwitch);

                protectedSwitch.setChecked(appElement.isProtected);
                appFragmentName.setText(appElement.name);
            }
        });
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(getBaseContext(), "O hej00", Toast.LENGTH_SHORT).show();
    }

    public void switchProtected(View view) {
        // TODO: switch function somewhere. Update data in SQLite
        //Toast.makeText(getBaseContext(), "O hej0022", Toast.LENGTH_SHORT).show();
    }

}
