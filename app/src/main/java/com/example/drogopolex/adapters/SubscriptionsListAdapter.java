package com.example.drogopolex.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.drogopolex.R;
import com.example.drogopolex.RequestSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsListAdapter extends RecyclerView.Adapter<SubscriptionsListAdapter.ViewHolder> {
    private static ArrayList<String> localDataSet;
    private static ArrayList<String> localDataSetIds;
    private final Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView subscriptionText;


        public ViewHolder(View view) {
            super(view);

            subscriptionText = (TextView) view.findViewById(R.id.subscription_row_text);
            Button unsubscribe = (Button) view.findViewById(R.id.unsubBtn);
            unsubscribe.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    unsubscribeRequest(localDataSetIds.get(getAdapterPosition()));
                }
            });
        }

        public TextView getSubscriptionTextView() {
            return subscriptionText;
        }
    }

    public SubscriptionsListAdapter(ArrayList<String> dataSet, ArrayList<String> dataId, Context context) {
        localDataSet = dataSet;
        localDataSetIds = dataId;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_subscriptions_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getSubscriptionTextView().setText(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void unsubscribeRequest(String subId){
        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", "");
        String token = sp.getString("token", "");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id_to_del",subId);
            jsonObject.put("user_id",userId);
            jsonObject.put("token", token);


            String url = "http://10.0.2.2:5000/unsubscribe";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    boolean isSuccess = false;
                    String stringError = "";

                    try {
                        isSuccess = response.getBoolean("success");
                        if(!isSuccess) {
                            stringError = response.getString("errorString");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(isSuccess){
                        Toast.makeText(context,"Subskrypcja usunieta",Toast.LENGTH_LONG).show();
                        resetSubscriptions();


                    }else{
                        Toast.makeText(context, stringError, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,"Error",Toast.LENGTH_LONG).show();
                }
            });

            RequestSingleton.getInstance(context).addToRequestQueue(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void resetSubscriptions() {
        JSONObject jsonObject = new JSONObject();
        String url = "http://10.0.2.2:5000/subscriptions";

        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        try {
            jsonObject.put("token", token);
            jsonObject.put("user_id", userId);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray resp = response.getJSONArray("subscriptions");
                        localDataSet.clear();
                        for (int i = 0; i < resp.length(); i++) {
                            JSONObject item = resp.getJSONObject(i);
                            String localization_str = item.getString("localization");
                            localDataSet.add(localization_str);
                            String sub_id_str = item.getString("id_sub");
                            localDataSetIds.add(sub_id_str);
                        }
                        notifyDataSetChanged();
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}