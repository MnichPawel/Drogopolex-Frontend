package com.example.drogopolex.events.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.adapters.RoutesListAdapter;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.RoutesResponse;
import com.example.drogopolex.databinding.ActivityRoutesListBinding;
import com.example.drogopolex.events.listeners.RoutesListListener;
import com.example.drogopolex.events.utils.RoutesListAction;
import com.example.drogopolex.events.viewModel.RoutesListViewModel;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexRoute;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RoutesListActivity extends AppCompatActivity implements SharedPreferencesHolder, RoutesListListener {
    ActivityRoutesListBinding activityRoutesListBinding;
    RecyclerView routesRecyclerView;
    RoutesListAdapter listAdapter;
    List<DrogopolexRoute> routes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRoutesListBinding = DataBindingUtil.setContentView(this, R.layout.activity_routes_list);
        activityRoutesListBinding.setViewModel(new RoutesListViewModel());
        activityRoutesListBinding.executePendingBindings();
        activityRoutesListBinding.getViewModel().sharedPreferencesHolder = this;
        activityRoutesListBinding.getViewModel().routesListListener = this;

        activityRoutesListBinding.getViewModel().getAction().observe(this, routesListAction -> {
            if (routesListAction != null) {
                handleAction(routesListAction);
            }
        });

        routesRecyclerView = findViewById(R.id.routesView);

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if (!sp.getBoolean("loggedIn", false)) {
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }

        activityRoutesListBinding.getViewModel().fetchRoutes();
    }

    private void handleAction(RoutesListAction routesListAction) {
        switch (routesListAction.getValue()) {
            case RoutesListAction.SHOW_MAP:
                Intent goToMapIntent = new Intent(this, MapActivity.class);
                startActivity(goToMapIntent);
                break;
            case RoutesListAction.SHOW_ADD_ROUTE:
                Intent goToAddRouteIntent = new Intent(this, AddRouteActivity.class);
                startActivity(goToAddRouteIntent);
                break;
        }
    }

    @Override
    public void onRouteDeleteSuccess(LiveData<BasicResponse> responseLiveData, int routeIndex) {
        responseLiveData.observe(this, basicResponse -> {
            if (basicResponse != null) {
                if(basicResponse.getError() == null) {
                    routes.remove(routeIndex);
                    listAdapter.notifyItemRemoved(routeIndex);
                    listAdapter.notifyItemRangeChanged(routeIndex, routes.size());
                } else {
                    Toast.makeText(RoutesListActivity.this, basicResponse.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RoutesListActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGetRoutesSuccess(LiveData<RoutesResponse> responseLiveData) {
        responseLiveData.observe(this, routesResponse -> {
            if (routesResponse != null) {
                routes.clear();
                routesResponse.getRoutes()
                        .forEach(route -> routes.add(new DrogopolexRoute(
                                String.valueOf(route.getId()),
                                route.getNazwa(),
                                route.getFrom().getName(),
                                route.getTo().getName(),
                                route.getCzas(),
                                route.getDystans()
                        )));
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                } else {
                    listAdapter = new RoutesListAdapter(routes, RoutesListActivity.this);
                    listAdapter.routesListListener = RoutesListActivity.this;
                    routesRecyclerView.setLayoutManager(new LinearLayoutManager(RoutesListActivity.this));
                    routesRecyclerView.setAdapter(listAdapter);
                }
            } else {
                Toast.makeText(RoutesListActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }
}
