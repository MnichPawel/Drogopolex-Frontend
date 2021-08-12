package com.example.drogopolex.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.drogopolex.R;
import com.example.drogopolex.RequestSingleton;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.VoteType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private ArrayList<DrogopolexEvent> localDataSet;
    private final Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView typeText;
        private final TextView locationText;
        private final TextView voteCounter;
        private final ImageButton upvoteButton;
        private final ImageButton downvoteButton;

        public ViewHolder(View view) {
            super(view);

            typeText = (TextView) view.findViewById(R.id.typeText);
            locationText = (TextView) view.findViewById(R.id.locationText);
            voteCounter = (TextView) view.findViewById(R.id.voteCounter);
            upvoteButton = (ImageButton) view.findViewById(R.id.upvoteButton);
            downvoteButton = (ImageButton) view.findViewById(R.id.downvoteButton);

            upvoteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(localDataSet.get(getAdapterPosition()).getUserVoteType() == VoteType.NO_VOTE) {
                        sendVote(getAdapterPosition(), true);
                        upvoteButton.setImageResource(R.drawable.thumb_up_checked);
                        voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) + 1));
                        localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.UPVOTED);
                    }
                    else if(localDataSet.get(getAdapterPosition()).getUserVoteType() == VoteType.UPVOTED) {
                        deleteVote(getAdapterPosition());
                        upvoteButton.setImageResource(R.drawable.thumb_up);
                        voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) - 1));
                        localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.NO_VOTE);
                    }
                    else {
                        changeVote(getAdapterPosition(), VoteType.UPVOTED);
                        //deleteVote(getAdapterPosition());
                        downvoteButton.setImageResource(R.drawable.thumb_down);
                        voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) + 2));
                        //sendVote(getAdapterPosition(), true);
                        upvoteButton.setImageResource(R.drawable.thumb_up_checked);
                        localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.UPVOTED);
                    }
                }
            });
            downvoteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(localDataSet.get(getAdapterPosition()).getUserVoteType() == VoteType.NO_VOTE) {
                        sendVote(getAdapterPosition(), false);
                        downvoteButton.setImageResource(R.drawable.thumb_down_checked);
                        voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) - 1));
                        localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.DOWNVOTED);
                    }
                    else if(localDataSet.get(getAdapterPosition()).getUserVoteType() == VoteType.DOWNVOTED) {
                        deleteVote(getAdapterPosition());
                        downvoteButton.setImageResource(R.drawable.thumb_down);
                        voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) + 1));
                        localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.NO_VOTE);
                    }
                    else {
                        changeVote(getAdapterPosition(), VoteType.DOWNVOTED);
                        upvoteButton.setImageResource(R.drawable.thumb_up);
                        voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) - 2));
                        downvoteButton.setImageResource(R.drawable.thumb_down_checked);
                        localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.DOWNVOTED);
                    }
                }
            });
        }

        public TextView getTypeTextView() {
            return typeText;
        }

        public TextView getLocationTextView() {
            return locationText;
        }

        public TextView getVoteCounter() {
            return voteCounter;
        }

        public ImageButton getUpvoteButton(){
            return upvoteButton;
        }

        public ImageButton getDownvoteButton() {
            return downvoteButton;
        }
    }

    public EventListAdapter(ArrayList<DrogopolexEvent> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_events_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d("myTag", "This is my messageeevents");
        DrogopolexEvent drogopolexEvent = localDataSet.get(position);
        viewHolder.getTypeTextView().setText(drogopolexEvent.getType());
        viewHolder.getLocationTextView().setText(String.format("%s, %s", drogopolexEvent.getStreet(), drogopolexEvent.getLocation()));
        viewHolder.getVoteCounter().setText(String.valueOf(drogopolexEvent.getVotesCount()));

        if(drogopolexEvent.getUserVoteType() == VoteType.UPVOTED) {
            viewHolder.getUpvoteButton().setImageResource(R.drawable.thumb_up_checked);
        }
        else if(drogopolexEvent.getUserVoteType() == VoteType.DOWNVOTED) {
            viewHolder.getDownvoteButton().setImageResource(R.drawable.thumb_down_checked);
        }
        else {
            viewHolder.getUpvoteButton().setImageResource(R.drawable.thumb_up);
            viewHolder.getDownvoteButton().setImageResource(R.drawable.thumb_down);
        }

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void sendVote(int position, boolean isUpvote) {
        int eventId = localDataSet.get(position).getId();
        String voteType;
        if(isUpvote) voteType = "T";
        else voteType = "F";

        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event_id", eventId);
            jsonObject.put("type", voteType);
            jsonObject.put("user_id", user_id);
            jsonObject.put("token", token);

            String url = "http://10.0.2.2:5000/vote"; //TODO adres jako zmienna globalna w pakiecie, bo jak się zmieni to się nie doszukamy
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    boolean isSuccess = false;
                    String stringError = "";

                    try {
                        isSuccess = response.getBoolean("success");
                        if (!isSuccess) {
                            stringError = response.getString("error");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!isSuccess) {
                        Toast.makeText(context, stringError, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                }
            });

            RequestSingleton.getInstance(context).addToRequestQueue(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteVote(int position) {
        int eventId = localDataSet.get(position).getId();

        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event_id", eventId);
            jsonObject.put("user_id", user_id);
            jsonObject.put("token", token);

            String url = "http://10.0.2.2:5000/remove_vote";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    boolean isSuccess = false;
                    String stringError = "";

                    try {
                        isSuccess = response.getBoolean("success");
                        if (!isSuccess) {
                            stringError = response.getString("error");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!isSuccess) {
                        Toast.makeText(context, stringError, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                }
            });

            RequestSingleton.getInstance(context).addToRequestQueue(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeVote(int position, VoteType newVoteType) {
        int eventId = localDataSet.get(position).getId();

        String newVoteTypeString;
        if(newVoteType == VoteType.UPVOTED) newVoteTypeString = "T";
        else newVoteTypeString = "F";

        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event_id", eventId);
            jsonObject.put("new_type", newVoteTypeString);
            jsonObject.put("user_id", user_id);
            jsonObject.put("token", token);

            String url = "http://10.0.2.2:5000/change_vote";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    boolean isSuccess = false;
                    String stringError = "";

                    try {
                        isSuccess = response.getBoolean("success");
                        if (!isSuccess) {
                            stringError = response.getString("error");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!isSuccess) {
                        Toast.makeText(context, stringError, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                }
            });

            RequestSingleton.getInstance(context).addToRequestQueue(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}