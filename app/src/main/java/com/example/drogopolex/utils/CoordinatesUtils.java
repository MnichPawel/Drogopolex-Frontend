package com.example.drogopolex.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinatesUtils {

    private CoordinatesUtils() {
        Log.e("CoordinatesUtils", "Utility class.");
    }

    public static LatLng parseCoordinatesString(String latLngString) {
        Pattern pattern = Pattern.compile("(.+),(.+)");
        Matcher matcher = pattern.matcher(latLngString.replace("(", "").replace(")", ""));
        if (matcher.find()) {
            return new LatLng(
                    Double.parseDouble(matcher.group(1)),
                    Double.parseDouble(matcher.group(2)));
        }
        return new LatLng(0.0, 0.0);
    }
}
