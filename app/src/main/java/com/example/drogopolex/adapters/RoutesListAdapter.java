package com.example.drogopolex.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.drogopolex.R;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.listeners.RoutesListListener;
import com.example.drogopolex.model.DrogopolexRoute;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

public class RoutesListAdapter extends RecyclerView.Adapter<RoutesListAdapter.ViewHolder> {
    private static List<DrogopolexRoute> localDataRoutes;
    private final Context context;
    public RoutesListListener routesListListener;
    private final EventsRepository eventsRepository;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView routeNameTextView;
        private final TextView routeLengthTextView;

        public ViewHolder(View view) {
            super(view);

            routeNameTextView = (TextView) view.findViewById(R.id.routeNameText);
            routeLengthTextView = (TextView) view.findViewById(R.id.routeLengthText);
            Button deleteRouteButton = (Button) view.findViewById(R.id.removeRouteButton);
            deleteRouteButton.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                DrogopolexRoute drogopolexRoute = localDataRoutes.get(adapterPosition);
                deleteRouteRequest(drogopolexRoute.getRouteId(), adapterPosition);
            });
        }

        public TextView getRouteTextView() {
            return routeNameTextView;
        }

        public TextView getRouteLengthTextView() {
            return routeLengthTextView;
        }
    }

    public RoutesListAdapter(List<DrogopolexRoute> data, Context context) {
        localDataRoutes = data;
        this.context = context;
        this.eventsRepository = new EventsRepository();
    }

    @NonNull
    @Override
    public RoutesListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_routes_list, viewGroup, false);

        return new RoutesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoutesListAdapter.ViewHolder viewHolder, final int position) {
        DrogopolexRoute drogopolexRoute = localDataRoutes.get(position);
        viewHolder.getRouteTextView().setText(drogopolexRoute.getName());
        String routeLengthString = drogopolexRoute.getDistance() + "m, "
                + convertSecondsToHoursMinutesSecondsFormat(drogopolexRoute.getTime());
        viewHolder.getRouteLengthTextView().setText(routeLengthString);
    }

    @Override
    public int getItemCount() {
        return localDataRoutes.size();
    }

    public void deleteRouteRequest(String routeId, int routeIndex) {
        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        LiveData<BasicResponse> responseLiveData = eventsRepository.removeRoute(token, userId, routeId);
        routesListListener.onRouteDeleteSuccess(responseLiveData, routeIndex);
    }

    private String convertSecondsToHoursMinutesSecondsFormat(double timeInSeconds) {
        int hours = (int) (timeInSeconds / 3600);
        int minutes = (int) ((timeInSeconds % 3600) / 60);
        int seconds = (int) (timeInSeconds % 60);

        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}
