package com.example.galicricket.TeamDetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galicricket.R;

import java.util.List;

public class SelectTeamAdapter extends RecyclerView.Adapter<SelectTeamAdapter.SelectTeamViewHolder> {

    private List<TeamInfo> teamList;

    public SelectTeamAdapter(List<TeamInfo> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public SelectTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new SelectTeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectTeamViewHolder holder, int position) {
        TeamInfo team = teamList.get(position);
        holder.teamName.setText(team.getTeamName());
        holder.checkBox.setChecked(team.isSelected());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Unselect all teams first
                for (TeamInfo t : teamList) {
                    t.setSelected(false);
                }
                // Select the clicked team
                team.setSelected(true);
                notifyDataSetChanged(); // Refresh the RecyclerView
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    static class SelectTeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamName;
        CheckBox checkBox;

        SelectTeamViewHolder(View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.teamName);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
