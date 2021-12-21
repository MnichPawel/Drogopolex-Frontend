package com.example.drogopolex.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.drogopolex.R;
import com.example.drogopolex.model.rules.DrogopolexRule;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RulesListAdapter extends RecyclerView.Adapter<RulesListAdapter.ViewHolder> {
    private final List<DrogopolexRule> localDataRules;

    public RulesListAdapter(List<DrogopolexRule> data) {
        localDataRules = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_rules_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        DrogopolexRule drogopolexRule = localDataRules.get(position);
        viewHolder.getRuleDescriptionText().setText(drogopolexRule.getDescription());
    }

    @Override
    public int getItemCount() {
        return localDataRules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ruleDescriptionText;


        public ViewHolder(View view) {
            super(view);

            ruleDescriptionText = view.findViewById(R.id.rule_description_row_text);
            Button deleteRuleBtn = view.findViewById(R.id.delete_rule_button);
            deleteRuleBtn.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                localDataRules.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, localDataRules.size());
            });
        }

        public TextView getRuleDescriptionText() {
            return ruleDescriptionText;
        }
    }
}
