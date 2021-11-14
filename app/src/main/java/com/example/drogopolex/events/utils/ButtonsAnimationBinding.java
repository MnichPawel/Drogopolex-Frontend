package com.example.drogopolex.events.utils;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.drogopolex.R;

import androidx.databinding.BindingAdapter;

public class ButtonsAnimationBinding {

    private ButtonsAnimationBinding() {
        throw new IllegalStateException("Class with only static methods.");
    }

    @BindingAdapter("showEventButton")
    public static void setShowEventButton(View view, boolean show) {
        Log.d("SHOW_EVENT_BUTTON", "show: " + show);
        if (show) {
            Animation rightwardsOpenAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.rightwards_open_anim);
            view.startAnimation(rightwardsOpenAnimation);
        } else {
            Animation rightwardsCloseAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.rightwards_close_anim);
            view.startAnimation(rightwardsCloseAnimation);
        }
    }

    @BindingAdapter("jiggleAddEventButton")
    public static void setJiggleAddEventButton(View view, boolean jiggle) {
        Log.d("JIGGLE_EVENT_BUTTON", "jiggle: " + jiggle);
        if (jiggle) {
            Animation buttonJiggleStart = AnimationUtils.loadAnimation(view.getContext(), R.anim.button_jiggle_start);
            view.startAnimation(buttonJiggleStart);
        } else {
            Animation buttonJiggleEnd = AnimationUtils.loadAnimation(view.getContext(), R.anim.button_jiggle_end);
            view.startAnimation(buttonJiggleEnd);
        }
    }

    @BindingAdapter("customVisibility")
    public static void setVisibilityMenu(View view, boolean visibility) {
        if(visibility) {
            Animation rightwardsOpenAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.downwards_open_anim);
            view.startAnimation(rightwardsOpenAnimation);
            view.setVisibility(View.VISIBLE);
        }
        else {
            Animation rightwardsOpenAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.downwards_close_anim);
            view.startAnimation(rightwardsOpenAnimation);
            view.setVisibility(View.INVISIBLE);
        }
    }
}
