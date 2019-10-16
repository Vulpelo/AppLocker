package com.example.cwiczenie1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AppElementAdapter extends ArrayAdapter<AppElement> {

    public AppElementAdapter(Context context, ArrayList<AppElement> list) {
        super(context, R.layout.app_element_row);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        AppElement appElement = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_element_row, parent, false);
        }

        // Lookup view for data population
        TextView appName = (TextView) convertView.findViewById(R.id.app_name);
//        TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);

        // Populate the data into the template view using the data object
        appName.setText(appElement.name);
//        tvHome.setText(user.hometown);

        // Return the completed view to render on screen
        return convertView;
    }
}
