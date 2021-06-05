package com.example.drogopolex;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.drogopolex.model.Vote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServerUtils {

    public static List<Vote> getVotes(Context context) {
        List<Vote> votes = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();

        String url = "http://10.0.2.2:5000/votes";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray resp = response.getJSONArray("votes");
                    for (int i = 0; i < resp.length(); i++) {
                        JSONObject item = resp.getJSONObject(i);
                        String voteType = item.getString("vote_type");
                        int eventId = Integer.parseInt(item.getString("event_id"));
                        int userId = Integer.parseInt(item.getString("user_id"));
                        votes.add(new Vote(eventId, userId, voteType));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }
        });

        RequestSingleton.getInstance(context).addToRequestQueue(objectRequest);

        return votes;
    }
}
