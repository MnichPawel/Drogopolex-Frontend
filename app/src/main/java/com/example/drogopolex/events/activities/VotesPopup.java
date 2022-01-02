package com.example.drogopolex.events.activities;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.drogopolex.R;
import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.repositories.VotesRepository;
import com.example.drogopolex.model.VoteType;
import com.google.android.gms.maps.model.Marker;

import androidx.core.content.ContextCompat;

public class VotesPopup {
    private static final String VOTES_TAG = "VOTES";

    private VotesPopup() {
        Log.e("VotesPopup", "Utility class.");
    }

    public static void showVotePopup(Context context, View view, Marker marker) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.custom_marker_popup, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.setElevation(20);


        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        String[] snippetMessage = marker.getSnippet().split(",");
        String eventId = snippetMessage[0];
        String voteType = snippetMessage[1];
        String numVotes = snippetMessage[2];
        TextView tW0 = popupView.findViewById(R.id.popupName);
        TextView tW1 = popupView.findViewById(R.id.popupDownvoteNumber);
        VotesRepository vr = new VotesRepository();
        ImageView downVote = popupView.findViewById(R.id.popupDownvote);
        ImageView upVote = popupView.findViewById(R.id.popupUpvote);
        //setting the graphics:
        setPopupGraphics(context, voteType, downVote, upVote);
        //voting
        downVote.setOnClickListener(v -> {
            Log.d(VOTES_TAG, "downvote click");
            String snippet = "";
            if (voteType.equals(VoteType.UPVOTED.getValue())) { //event is upvoted by this user
                vr.votesChangeVote(
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.USER_ID_SHARED_PREFERENCES, ""),
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.TOKEN_SHARED_PREFERENCES, ""),
                        VoteType.DOWNVOTED,
                        eventId);
                snippet = eventId + "," + VoteType.DOWNVOTED.getValue() + "," + (Integer.parseInt(numVotes) - 2);
                setPopupGraphics(context, VoteType.DOWNVOTED.getValue(), downVote, upVote);
            } else if (voteType.equals(VoteType.DOWNVOTED.getValue())) { //event is downvoted by this user
                vr.votesRemoveVote(
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.USER_ID_SHARED_PREFERENCES, ""),
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.TOKEN_SHARED_PREFERENCES, ""),
                        eventId);
                snippet = eventId + "," + VoteType.NO_VOTE.getValue() + "," + (Integer.parseInt(numVotes) + 1);
                setPopupGraphics(context, VoteType.NO_VOTE.getValue(), downVote, upVote);
            } else if (voteType.equals(VoteType.NO_VOTE.getValue())) { //event has not been voted yet by this user
                vr.votesAddVote(context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.USER_ID_SHARED_PREFERENCES, ""),
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.TOKEN_SHARED_PREFERENCES, ""),
                        VoteType.DOWNVOTED,
                        eventId);
                snippet = eventId + "," + VoteType.DOWNVOTED.getValue() + "," + (Integer.parseInt(numVotes) - 1);
                setPopupGraphics(context, VoteType.DOWNVOTED.getValue(), downVote, upVote);
            }
            marker.setSnippet(snippet);
            popupWindow.dismiss();
        });


        upVote.setOnClickListener(v -> {
            Log.d(VOTES_TAG, "UpVote click");
            String snippet = "";
            if (voteType.equals(VoteType.UPVOTED.getValue())) { //event is upvoted by this user
                vr.votesRemoveVote(
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.USER_ID_SHARED_PREFERENCES, ""),
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.TOKEN_SHARED_PREFERENCES, ""),
                        eventId);
                snippet = eventId + "," + VoteType.NO_VOTE.getValue() + "," + (Integer.parseInt(numVotes) - 1);
                setPopupGraphics(context, VoteType.NO_VOTE.getValue(), downVote, upVote);
            } else if (voteType.equals(VoteType.DOWNVOTED.getValue())) { //event is downvoted by this user
                vr.votesChangeVote(
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.USER_ID_SHARED_PREFERENCES, ""),
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.TOKEN_SHARED_PREFERENCES, ""),
                        VoteType.UPVOTED,
                        eventId);
                snippet = eventId + "," + VoteType.UPVOTED.getValue() + "," + (Integer.parseInt(numVotes) + 2);
                setPopupGraphics(context, VoteType.UPVOTED.getValue(), downVote, upVote);
            } else if (voteType.equals(VoteType.NO_VOTE.getValue())) { //event has not been voted yet by this user
                vr.votesAddVote(
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.USER_ID_SHARED_PREFERENCES, ""),
                        context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .getString(AppConstant.TOKEN_SHARED_PREFERENCES, ""),
                        VoteType.UPVOTED,
                        eventId);
                snippet = eventId + "," + VoteType.UPVOTED.getValue() + "," + (Integer.parseInt(numVotes) + 1);
                setPopupGraphics(context, VoteType.UPVOTED.getValue(), downVote, upVote);
            }
            marker.setSnippet(snippet);
            popupWindow.dismiss();
        });

        tW0.setText(marker.getTitle());
        tW1.setText(numVotes);
    }

    private static void setPopupGraphics(Context context, String voteType, ImageView downVote, ImageView upVote) {
        if (voteType.equals(VoteType.UPVOTED.getValue())) {
            Log.d(VOTES_TAG, "setPopupGraphics UpVote");
            downVote.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_thumb_down_24_gray));
            upVote.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_thumb_up_24));
        } else if (voteType.equals(VoteType.DOWNVOTED.getValue())) {
            Log.d(VOTES_TAG, "setPopupGraphics DownVote");
            downVote.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_thumb_down_24));
            upVote.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_thumb_up_24_gray));
        } else if (voteType.equals(VoteType.NO_VOTE.getValue())) {
            Log.d(VOTES_TAG, "setPopupGraphics NoVote");
            downVote.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_thumb_down_24_gray));
            upVote.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_thumb_up_24_gray));
        }
    }
}
