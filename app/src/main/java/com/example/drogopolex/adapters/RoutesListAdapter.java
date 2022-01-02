package com.example.drogopolex.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drogopolex.R;
import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.activities.MapActivity;
import com.example.drogopolex.events.listeners.RoutesListListener;
import com.example.drogopolex.model.DrogopolexRoute;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

public class RoutesListAdapter extends RecyclerView.Adapter<RoutesListAdapter.ViewHolder> {
    private static List<DrogopolexRoute> localDataRoutes;
    private final Context context;
    private final EventsRepository eventsRepository;
    public RoutesListListener routesListListener;

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

        String sourceString = "Trasa z " + drogopolexRoute.getSourceName();
        String destinationString = "Trasa do " + drogopolexRoute.getDestinationName();
        viewHolder.getSourceTextView().setText(sourceString);
        viewHolder.getDestinationTextView().setText(destinationString);

        String routeLengthString = drogopolexRoute.getDistance() + "m, "
                + convertSecondsToHoursMinutesSecondsFormat(drogopolexRoute.getTime());
        viewHolder.getRouteLengthTextView().setText(routeLengthString);
    }

    @Override
    public int getItemCount() {
        return localDataRoutes.size();
    }

    public void deleteRouteRequest(String routeId, int routeIndex) {
        SharedPreferences sp = context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String userId = sp.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sp.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

        LiveData<BasicResponse> responseLiveData = eventsRepository.removeRoute(userId, token, routeId);
        routesListListener.onRouteDeleteSuccess(responseLiveData, routeIndex);
    }

    private String convertSecondsToHoursMinutesSecondsFormat(double timeInSeconds) {
        int hours = (int) (timeInSeconds / 3600);
        int minutes = (int) ((timeInSeconds % 3600) / 60);
        int seconds = (int) (timeInSeconds % 60);

        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView routeNameTextView;
        private final TextView sourceTextView;
        private final TextView destinationTextView;
        private final TextView routeLengthTextView;

        public ViewHolder(View view) {
            super(view);

            routeNameTextView = view.findViewById(R.id.routeNameText);
            sourceTextView = view.findViewById(R.id.sourceText);
            destinationTextView = view.findViewById(R.id.destinationText);
            routeLengthTextView = view.findViewById(R.id.routeLengthText);

            ImageView deleteRouteButton = view.findViewById(R.id.removeRouteButton);
            deleteRouteButton.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                DrogopolexRoute drogopolexRoute = localDataRoutes.get(adapterPosition);
                deleteRouteRequest(drogopolexRoute.getRouteId(), adapterPosition);
            });

            ImageView showRouteButton = view.findViewById(R.id.showRouteButton);
            showRouteButton.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                DrogopolexRoute drogopolexRoute = localDataRoutes.get(adapterPosition);
                Intent showRouteOnMapIntent = new Intent(view.getContext(), MapActivity.class);
                showRouteOnMapIntent.putExtra("routeId", drogopolexRoute.getRouteId());
                view.getContext().startActivity(showRouteOnMapIntent);
            });
        }

        public TextView getRouteTextView() {
            return routeNameTextView;
        }

        public TextView getSourceTextView() {
            return sourceTextView;
        }

        public TextView getDestinationTextView() {
            return destinationTextView;
        }

        public TextView getRouteLengthTextView() {
            return routeLengthTextView;
        }
    }
}
