package com.example.drogopolex.events.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.drogopolex.R;
import com.example.drogopolex.data.repositories.VotesRepository;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    Context context;
    VotesRepository vr;

    public CustomInfoWindowAdapter(Context context){
        vr = new VotesRepository();
        this.context=context;
    }
    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        if(!Objects.equals(marker.getSnippet(), "notEvent")){
            return LayoutInflater.from(context).inflate(R.layout.empty_marker_popup_cheat, null);
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
