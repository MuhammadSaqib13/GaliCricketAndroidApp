package com.example.galicricket.TeamDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galicricket.R;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    private List<TeamInfo> teamList;
    private Context context;

    public TeamAdapter(Context context, List<TeamInfo> teamList) {
        this.context = context;
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_item_layout, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        TeamInfo team = teamList.get(position);
        holder.teamNameTextView.setText(team.getTeamName());
        holder.playersCountTextView.setText(String.valueOf(team.getPlayersCount())+" Players");
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamNameTextView, playersCountTextView;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamNameTextView = itemView.findViewById(R.id.teamNameTextView);
            playersCountTextView = itemView.findViewById(R.id.playersCountTextView);
        }
    }
}
