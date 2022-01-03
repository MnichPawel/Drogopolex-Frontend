package com.example.drogopolex.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.drogopolex.R;
import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.repositories.VotesRepository;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.VoteType;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private List<DrogopolexEvent> localDataSet;
    private final Context context;

    private VotesRepository votesRepository;

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

            upvoteButton.setOnClickListener(v -> {
                if (localDataSet.get(getAdapterPosition()).getUserVoteType() == VoteType.NO_VOTE) {
                    sendVote(getAdapterPosition(), true);
                    upvoteButton.setImageResource(R.drawable.thumb_up_checked);
                    voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) + 1));
                    localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.UPVOTED);
                } else if (localDataSet.get(getAdapterPosition()).getUserVoteType() == VoteType.UPVOTED) {
                    deleteVote(getAdapterPosition());
                    upvoteButton.setImageResource(R.drawable.thumb_up);
                    voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) - 1));
                    localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.NO_VOTE);
                } else {
                    changeVote(getAdapterPosition(), VoteType.UPVOTED);
                    downvoteButton.setImageResource(R.drawable.thumb_down);
                    voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) + 2));
                    upvoteButton.setImageResource(R.drawable.thumb_up_checked);
                    localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.UPVOTED);
                }
            });
            downvoteButton.setOnClickListener(v -> {
                if (localDataSet.get(getAdapterPosition()).getUserVoteType() == VoteType.NO_VOTE) {
                    sendVote(getAdapterPosition(), false);
                    downvoteButton.setImageResource(R.drawable.thumb_down_checked);
                    voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) - 1));
                    localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.DOWNVOTED);
                } else if (localDataSet.get(getAdapterPosition()).getUserVoteType() == VoteType.DOWNVOTED) {
                    deleteVote(getAdapterPosition());
                    downvoteButton.setImageResource(R.drawable.thumb_down);
                    voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) + 1));
                    localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.NO_VOTE);
                } else {
                    changeVote(getAdapterPosition(), VoteType.DOWNVOTED);
                    upvoteButton.setImageResource(R.drawable.thumb_up);
                    voteCounter.setText(String.valueOf(Integer.parseInt((String) voteCounter.getText()) - 2));
                    downvoteButton.setImageResource(R.drawable.thumb_down_checked);
                    localDataSet.get(getAdapterPosition()).setUserVoteType(VoteType.DOWNVOTED);
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

        public ImageButton getUpvoteButton() {
            return upvoteButton;
        }

        public ImageButton getDownvoteButton() {
            return downvoteButton;
        }
    }

    public EventListAdapter(List<DrogopolexEvent> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
        votesRepository = new VotesRepository();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_events_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        DrogopolexEvent drogopolexEvent = localDataSet.get(position);
        viewHolder.getTypeTextView().setText(drogopolexEvent.getType());
        viewHolder.getLocationTextView().setText(String.format("%s, %s", drogopolexEvent.getStreet(), drogopolexEvent.getLocation()));
        viewHolder.getVoteCounter().setText(String.valueOf(drogopolexEvent.getValueOfVotes()));

        if (drogopolexEvent.getUserVoteType() == VoteType.UPVOTED) {
            viewHolder.getUpvoteButton().setImageResource(R.drawable.thumb_up_checked);
        } else if (drogopolexEvent.getUserVoteType() == VoteType.DOWNVOTED) {
            viewHolder.getDownvoteButton().setImageResource(R.drawable.thumb_down_checked);
        } else {
            viewHolder.getUpvoteButton().setImageResource(R.drawable.thumb_up);
            viewHolder.getDownvoteButton().setImageResource(R.drawable.thumb_down);
        }

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void sendVote(int position, boolean isUpvote) {
        String eventId = String.valueOf(localDataSet.get(position).getId());
        VoteType voteType;
        if (isUpvote) voteType = VoteType.UPVOTED;
        else voteType = VoteType.DOWNVOTED;

        SharedPreferences sp = context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String userId = sp.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sp.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

        votesRepository.votesAddVote(userId, token, voteType, eventId);
    }

    public void deleteVote(int position) {
        String eventId = String.valueOf(localDataSet.get(position).getId());

        SharedPreferences sp = context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String userId = sp.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sp.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

        votesRepository.votesRemoveVote(userId, token, eventId);
    }

    public void changeVote(int position, VoteType newVoteType) {
        String eventId = String.valueOf(localDataSet.get(position).getId());

        SharedPreferences sp = context.getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String userId = sp.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sp.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

        votesRepository.votesChangeVote(userId, token, newVoteType, eventId);
    }
}