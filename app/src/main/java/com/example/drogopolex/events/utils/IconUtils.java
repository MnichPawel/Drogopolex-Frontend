package com.example.drogopolex.events.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.drogopolex.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;

public class IconUtils {

    private IconUtils() {
        Log.e("IconUtils", "Utility class.");
    }

    public static BitmapDescriptor findIconForType(String type, Context context) {
        int drawableId;

        if ("Wypadek".equals(type)) {
            drawableId = R.drawable.ic_wypadek;
        } else if ("Korek".equals(type)) {
            drawableId = R.drawable.ic_korek;
        } else if ("Patrol Policji".equals(type)) {
            drawableId = R.drawable.ic_radar;
        } else if ("Roboty Drogowe".equals(type)) { //Roboty Drogowe
            drawableId = R.drawable.ic_roboty;
        } else if ("hotel".equals(type)) {
            drawableId = R.drawable.ic_hotel;
        } else if ("gallery".equals(type)) {
            drawableId = R.drawable.ic_galeriahandlowa;
        } else if ("library".equals(type)) {
            drawableId = R.drawable.ic_biblioteka;
        } else if ("museum".equals(type)) {
            drawableId = R.drawable.ic_galeriasztuki;
        } else if ("college".equals(type)) {
            drawableId = R.drawable.ic_uczelnia;
        } else if ("kindergarten".equals(type)) {
            drawableId = R.drawable.ic_uczelnia;
        } else if ("school".equals(type)) {
            drawableId = R.drawable.ic_uczelnia;
        } else if ("university".equals(type)) {
            drawableId = R.drawable.ic_uczelnia;
        } else if ("bank".equals(type)) {
            drawableId = R.drawable.ic_bank;
        } else if ("dentist".equals(type)) {
            drawableId = R.drawable.ic_dentysta;
        } else if ("hospital".equals(type)) {
            drawableId = R.drawable.ic_szpital;
        } else if ("pharmacy".equals(type)) {
            drawableId = R.drawable.ic_szpital; //pharmacy icon same as hospital
        } else if ("fitness_centre".equals(type)) {
            drawableId = R.drawable.ic_sport;
        } else if ("swimming_pool".equals(type)) {
            drawableId = R.drawable.ic_sport;
        } else if ("stadium".equals(type)) {
            drawableId = R.drawable.ic_sport;
        } else if ("cinema".equals(type)) {
            drawableId = R.drawable.ic_kinopinezka;
        } else if ("park".equals(type)) {
            drawableId = R.drawable.ic_park;
        } else if ("zoo".equals(type)) {
            drawableId = R.drawable.ic_zoopinezka;
        } else if ("fire_station".equals(type)) {
            drawableId = R.drawable.ic_strazpinezka;
        } else if ("police".equals(type)) {
            drawableId = R.drawable.ic_policjapinezka;
        } else if ("post_office".equals(type)) {
            drawableId = R.drawable.ic_poczta;
        } else if ("townhall".equals(type)) {
            drawableId = R.drawable.ic_ratuszpinezka;
        } else if ("hairdresser".equals(type)) {
            drawableId = R.drawable.ic_fryzjerpinezka;
        } else if ("bar".equals(type)) {
            drawableId = R.drawable.ic_pub;
        } else if ("fast_food".equals(type)) {
            drawableId = R.drawable.ic_jedzenie;
        } else if ("pub".equals(type)) {
            drawableId = R.drawable.ic_pub;
        } else if ("restaurant".equals(type)) {
            drawableId = R.drawable.ic_jedzenie;
        } else if ("fuel".equals(type)) {
            drawableId = R.drawable.ic_paliwo;
        } else if ("parking".equals(type)) {
            drawableId = R.drawable.ic_parking;
        } else if ("railway_station".equals(type)) {
            drawableId = R.drawable.ic_ciapolongi;
        } else if ("public_transport_station".equals(type)) {
            drawableId = R.drawable.ic_busy;
        } else {
            drawableId = R.drawable.ic_fontanna; // fountain
        }
        return svgToBitmap(drawableId, context);
    }

    private static BitmapDescriptor svgToBitmap(@DrawableRes int id, Context context) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
