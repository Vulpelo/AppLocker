package com.example.cwiczenie1;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

        AppElement appElement = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_element_row, parent, false);
        }

        TextView appName = (TextView) convertView.findViewById(R.id.app_name);
        appName.setText(appElement.appName);


        return convertView;
    }
}
