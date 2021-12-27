package com.example.drogopolex.events.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drogopolex.R;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.data.repositories.VotesRepository;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.DrogopolexRoute;
import com.example.drogopolex.model.VoteType;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    Context context;
    VotesRepository vr;
    SharedPreferencesHolder sh =null;
    ArrayList<DrogopolexEvent> events = new ArrayList<>();
    public CustomInfoWindowAdapter(Context context, SharedPreferencesHolder sh){
        vr = new VotesRepository();
        this.sh = sh;
        this.context=context;
    }
    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        if(!marker.getSnippet().equals("notEvent")){
            View customMarkerView = LayoutInflater.from(context).inflate(R.layout.empty_marker_popup_cheat, null);
            /*Log.d("MARK", marker.getSnippet());
            //snippetMsg format: "[eventId]^[voteType]^[numVotes]"
            String[] snippetMessage = marker.getSnippet().split(",");
            String eventId = snippetMessage[0];
            String voteType = snippetMessage[1];
            String numVotes = snippetMessage[2];
            View customMarkerView = LayoutInflater.from(context).inflate(R.layout.custom_marker_popup, null);
            TextView tW0 = customMarkerView.findViewById(R.id.popupName);
            TextView tW1 = customMarkerView.findViewById(R.id.popupDownvoteNumber);
            SharedPreferences sp = sh.getSharedPreferences();

            ImageView downVote = customMarkerView.findViewById(R.id.popupDownvote);
            downVote.setOnClickListener(v -> {
                if(voteType.equals(VoteType.UPVOTED.getValue())){ //event is upvoted by this user
                    vr.votesChangeVote(sp.getString("user_id",""),sp.getString("token",""),VoteType.DOWNVOTED,eventId);
                }
                if(voteType.equals(VoteType.DOWNVOTED.getValue())){ //event is downvoted by this user
                    vr.votesRemoveVote(sp.getString("user_id",""),sp.getString("token",""),eventId);
                }
                if(voteType.equals(VoteType.NO_VOTE.getValue())){ //event has not been voted yet by this user
                    vr.votesAddVote(sp.getString("user_id",""),sp.getString("token",""),VoteType.DOWNVOTED,eventId);
                }
            });

            ImageView upVote = customMarkerView.findViewById(R.id.popupUpvote);
            upVote.setOnClickListener(v -> {
                if(voteType.equals(VoteType.UPVOTED.getValue())){ //event is upvoted by this user
                    vr.votesRemoveVote(sp.getString("user_id",""),sp.getString("token",""),eventId);
                }
                if(voteType.equals(VoteType.DOWNVOTED.getValue())){ //event is downvoted by this user
                    vr.votesChangeVote(sp.getString("user_id",""),sp.getString("token",""),VoteType.UPVOTED,eventId);
                }
                if(voteType.equals(VoteType.NO_VOTE.getValue())){ //event has not been voted yet by this user
                    vr.votesAddVote(sp.getString("user_id",""),sp.getString("token",""),VoteType.UPVOTED,eventId);
                }
            });

            tW0.setText(marker.getTitle());
            tW1.setText(numVotes);

            customMarkerView.setClickable(false);*/
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
