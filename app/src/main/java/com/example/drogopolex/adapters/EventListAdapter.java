package com.example.drogopolex.adapters;

import android.content.Context;
import android.content.SharedPreferences;
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
        private final ImageButton upvoteButton;
        private final ImageButton downvoteButton;

        public ViewHolder(View view) {
            super(view);

            typeText = (TextView) view.findViewById(R.id.typeText);
            locationText = (TextView) view.findViewById(R.id.locationText);
            upvoteButton = (ImageButton) view.findViewById(R.id.upvoteButton);
            downvoteButton = (ImageButton) view.findViewById(R.id.downvoteButton);

            upvoteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    sendVote(getAdapterPosition(), true);
                }
            });
            downvoteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    sendVote(getAdapterPosition(), false);
                }
            });
        }

        public TextView getTypeTextView() {
            return typeText;
        }

        public TextView getLocationTextView() {
            return locationText;
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
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTypeTextView().setText(localDataSet.get(position).getType());
        viewHolder.getLocationTextView().setText(localDataSet.get(position).getLocation());
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
}