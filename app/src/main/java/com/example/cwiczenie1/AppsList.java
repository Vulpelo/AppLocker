package com.example.cwiczenie1;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppsList extends FragmentActivity implements BlankFragment.OnFragmentInteractionListener {

    FragmentManager fm;

    PackageManager pm;
    List<ApplicationInfo> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

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

        ArrayList<AppElement> appElementArrayList = new ArrayList<>();
        AppElementAdapter adapter = new AppElementAdapter(this, appElementArrayList);

        for (ApplicationInfo appInfo: installedApps) {
            AppElement appElement = new AppElement(appInfo.processName);

            adapter.add(appElement);
            //AppPosition appPos = new AppPosition();

//            getSupportFragmentManager().beginTransaction().add(appsLayout.getId(),
//                    AppPosition.newInstance("Hello", "Fragment"), appInfo.processName).commit();


//            Button but = new Button(appsLayout.getContext());
//            but.setText(appInfo.loadLabel(pm).toString());
//
//            but.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    Button b = (Button) view;
//
//                    View appFragView = findViewById(R.id.appFragment);
//                    TextView appFragmentName = appFragView.findViewById(R.id.appFragmentName);
//                    appFragmentName.setText(b.getText().toString());
//                }
//            });

            //appsLayout.addView(but);
        }

        ListView listView = (ListView) findViewById(R.id.listViewApps);
        listView.setAdapter(adapter);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
