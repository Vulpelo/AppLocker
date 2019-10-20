package com.example.cwiczenie1;

import androidx.fragment.app.FragmentActivity;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwiczenie1.database.AppDatabase;
import com.example.cwiczenie1.database.AppLockerDbHelper;
import com.example.cwiczenie1.database.ResetWhen;

import java.util.ArrayList;
import java.util.List;

public class AppsList extends FragmentActivity implements BlankFragment.OnFragmentInteractionListener {

    AppLockerDbHelper dbHelper;

    FragmentManager fm;
    PackageManager pm;

    List<ApplicationInfo> apps;

    ArrayList<AppElement> appElementArrayList;
    AppElementAdapter adapter;

    int appElementSelected = 0;

    // param1 - Context
    // param2 - Adapter
    private class LoadAppsTask extends AsyncTask<Object, Integer, String> {
        Context context;
        AppElementAdapter adapter;

        @Override
        protected String doInBackground(Object... params) {
            context = (Context) params[0];
            adapter = (AppElementAdapter) params[1];

            List<ApplicationInfo> installedApps = getAppsOnPhone();
            updateAppElementsAdapterWithDb(context, adapter, installedApps);

            AppDatabase appDatabase = new AppDatabase(context);

            for (ApplicationInfo appInfo: installedApps) {
                AppElement appElement = appDatabase.getByName(appInfo.processName);

                if (appElement == null) {
                    appElement = new AppElement(appInfo.processName);
                    appElement.appName = appInfo.processName;
                    appElement.id = appDatabase.insertElement(appElement);
                }
                appElement.appName = appInfo.loadLabel(pm).toString();
                try {
                    Drawable icon = pm.getApplicationIcon(appElement.name);
                    appElement.appImage = icon;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                adapter.add(appElement);
            }
            return "ok";
        }

        private void updateAppElementsAdapterWithDb(Context context, AppElementAdapter adapter, List<ApplicationInfo> installedApps) {
            AppDatabase appDatabase = new AppDatabase(context);

            for (ApplicationInfo appInfo: installedApps) {
                AppElement appElement = appDatabase.getByName(appInfo.processName);

                if (appElement == null) {
                    appElement = new AppElement(appInfo.processName);
                    appElement.appName = appInfo.processName;
                    appElement.id = appDatabase.insertElement(appElement);
                }
                appElement.appName = appInfo.loadLabel(pm).toString();
                try {
                    Drawable icon = pm.getApplicationIcon(appElement.name);
                    appElement.appImage = icon;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                adapter.add(appElement);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ListView listView = (ListView) findViewById(R.id.listViewApps);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    appElementSelected = position;
                    AppElement appElement = adapter.getItem(position);

                    getSupportFragmentManager().beginTransaction().replace(R.id.appFragment, BlankFragment.newInstance(appElement));

                    View fragment = findViewById(R.id.appFragment);

                    TextView appFragmentName = fragment.findViewById(R.id.appFragmentName);
                    Switch protectedSwitch = fragment.findViewById(R.id.protectedSwitch);
                    CheckBox passAfterClose = (CheckBox) fragment.findViewById(R.id.requireAfterCloseCheckBox);

                    if (appElement.appImage != null) {
                        ImageView imageView = (ImageView) fragment.findViewById(R.id.appImageView);
                        imageView.setImageDrawable(appElement.appImage);
                    }

                    protectedSwitch.setChecked(appElement.isProtected);
                    appFragmentName.setText(appElement.name);
                    passAfterClose.setChecked( appElement.resetWhen.compareTo(ResetWhen.ON_CLOSE) == 0);
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        dbHelper = new AppLockerDbHelper(getBaseContext());
        pm = getPackageManager();
        apps = pm.getInstalledApplications(0);
        fm = getFragmentManager();
        appElementArrayList = new ArrayList<>();
        adapter = new AppElementAdapter(this, appElementArrayList);

        Object[] params = { this,  adapter };
        new LoadAppsTask().execute(params);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    public void onProtectedSwitchChange(View view) {
        Switch aSwitch = (Switch)view;

        AppElement mAppElement = adapter.getItem(appElementSelected);
        mAppElement.isProtected = aSwitch.isChecked();

        AppDatabase appDatabase = new AppDatabase(this);
        appDatabase.updateElement(mAppElement);
    }

    public void onLockCheckBoxChange(View view) {
        CheckBox aCheckBox = (CheckBox)view;

        AppElement mAppElement = adapter.getItem(appElementSelected);
        mAppElement.resetWhen = aCheckBox.isChecked() ? ResetWhen.ON_CLOSE : ResetWhen.SCREEN_OFF;

        AppDatabase appDatabase = new AppDatabase(this);
        appDatabase.updateElement(mAppElement);
    }



    private List<ApplicationInfo> getAppsOnPhone() {
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
        return installedApps;
    }
}
