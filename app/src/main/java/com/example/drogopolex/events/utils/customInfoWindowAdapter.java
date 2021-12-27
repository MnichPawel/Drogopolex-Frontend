package com.example.drogopolex.events.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.drogopolex.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class customInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    Context context;
    public customInfoWindowAdapter(Context context){
        this.context=context;
    }
    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        if(!marker.getSnippet().equals("notEvent")){
            View customMarkerView = LayoutInflater.from(context).inflate(R.layout.custom_marker_popup, null);
            TextView tW0 = customMarkerView.findViewById(R.id.popupName);
            TextView tW1 = customMarkerView.findViewById(R.id.popupDownvoteNumber);
            //TextView tW2 = customMarkerView.findViewById(R.id.popupDownvoteNumber);
            tW0.setText(marker.getTitle());
            tW1.setText(marker.getSnippet());
            //tW2.setText("t2");
            return customMarkerView;
        }else{
            return null;
        }
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }
}
