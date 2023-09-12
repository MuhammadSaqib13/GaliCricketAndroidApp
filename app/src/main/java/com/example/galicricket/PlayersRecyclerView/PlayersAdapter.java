package com.example.galicricket.PlayersRecyclerView;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galicricket.R;
import com.example.galicricket.TeamDetails.ReadWriteTeamDetails;
import com.example.galicricket.TeamsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>{

    private final Context context;
    private List<ReadWriteTeamDetails> playerList;

    private EditText playerTxt, batTxt, bowlTxt;
    TeamsActivity teams;
    
    int count;
    SharedPreferences preferences;

    private RecyclerView playerRecView;

    String teamName, playerKey;

    FirebaseAuth auth;
    FirebaseUser user;


    public PlayersAdapter(Context context, List<ReadWriteTeamDetails> playerList, TeamsActivity teams, String teamName, SharedPreferences preferences) {
        this.context = context;
        this.playerList = playerList;
        this.teams = teams;
        this.teamName = teamName;
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(R.layout.single_player,null);
        PlayerViewHolder holder =new PlayerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        ReadWriteTeamDetails model = playerList.get(position);
        holder.player_name.setText(model.getPlayerName());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showBottomDialog(teams);
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();

                count = preferences.getInt("playerCount", 1);

                DatabaseReference playerReference = FirebaseDatabase.getInstance().getReference("Teams").child(user.getUid()).child(teamName);

                playerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String playerBatStyle = childSnapshot.child("battingStyle").getValue(String.class);
                            String playerBowlStyle = childSnapshot.child("bowlingStyle").getValue(String.class);

                            String playerName = childSnapshot.child("playerName").getValue(String.class);


                            if (playerBatStyle != null && playerBowlStyle != null) {
                                if (holder.player_name.getText().toString().equals(playerName)) {
                                    playerKey = childSnapshot.getKey();
                                    Log.i("key", playerKey);
                                    holder.deletePlayer(playerKey);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt("playerCount", count);
                                    editor.apply();
                                    break;
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }


    class PlayerViewHolder extends RecyclerView.ViewHolder{
        CardView player_Card;
        TextView player_name;
        ImageView edit, delete;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            player_Card = itemView.findViewById(R.id.player_Card);
            player_name = itemView.findViewById(R.id.PlayerName);
            edit = itemView.findViewById(R.id.edit_details);
            delete = itemView.findViewById(R.id.delete_details);
        }

        void showBottomDialog(TeamsActivity teams){
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(teams,R.style.BottomSheetDialogTheme);
            View bottomSheetView = null;

            bottomSheetView = LayoutInflater.from(context)
                    .inflate(
                            R.layout.layout_add_team,
                            teams.findViewById(R.id.bottomSheetContainer)
                    );



            playerTxt = bottomSheetView.findViewById(R.id.txt_playerName);
            playerTxt.setText(player_name.getText().toString());

            batTxt = bottomSheetView.findViewById(R.id.txt_batStyle);
            bowlTxt = bottomSheetView.findViewById(R.id.txt_BalStyle);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teams").child(user.getUid()).child(teamName);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String playerBatStyle = childSnapshot.child("battingStyle").getValue(String.class);
                        String playerBowlStyle = childSnapshot.child("bowlingStyle").getValue(String.class);

                        String playerName = childSnapshot.child("playerName").getValue(String.class);


                        if (playerBatStyle != null && playerBowlStyle != null) {
                            if (player_name.getText().toString().equals(playerName)) {
                                playerKey = childSnapshot.getKey();
                                Log.i("key", playerKey);
                                batTxt.setText(playerBatStyle);
                                bowlTxt.setText(playerBowlStyle);
                                break;
                            }
                        }
                        else if (playerBowlStyle != null){
                            if (player_name.getText().toString().equals(playerName)) {
                                bowlTxt.setText(playerBowlStyle);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });

            bottomSheetView.findViewById(R.id.fb_check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePlayerDetails(teamName,playerTxt.getText().toString(),batTxt.getText().toString(),bowlTxt.getText().toString(), playerKey);
                    bottomSheetDialog.dismiss();
                }
            });


            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();


        }
        void updatePlayerDetails(String teamName,String pName, String batStyle, String ballStyle, String key){
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            ReadWriteTeamDetails writeTeamDetails = new ReadWriteTeamDetails(user.getUid(), pName, batStyle, ballStyle);


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teams");

            reference.child(user.getUid()).child(teamName).child(key).setValue(writeTeamDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        playerTxt.setText("");
                        batTxt.setText("");
                        bowlTxt.setText("");
                        Toast.makeText(context,"Player updated Succesfully",Toast.LENGTH_LONG).show();
                        notifyDataSetChanged();
                    }
                }
            });
        }
        void deletePlayer(String key) {
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            DatabaseReference playerReference = FirebaseDatabase.getInstance().getReference("Teams").child(user.getUid()).child(teamName).child(key);

            playerReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Player deleted Successfully", Toast.LENGTH_LONG).show();
                        count--;

                    } else {
                        Toast.makeText(context, "Error deleting player", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
}
