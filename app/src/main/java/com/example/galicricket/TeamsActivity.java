package com.example.galicricket;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galicricket.PlayersRecyclerView.PlayersAdapter;
import com.example.galicricket.RegisterUserDetails.ReadWriteUserDetails;
import com.example.galicricket.TeamDetails.ReadWriteTeamDetails;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeamsActivity extends AppCompatActivity {


    FloatingActionButton fbPlus;


    RecyclerView players;
    SpinKitView spinKit;
    ArrayList<ReadWriteTeamDetails> playerList = null;
    PlayersAdapter adapter;

    ImageView back_btn;



    TextView teamName;
    String t_Name, Name, Email, phone;

    EditText p_name, bat_style, ball_style;

    SharedPreferences preferences;

    FirebaseAuth auth;
    FirebaseUser user;

    SwipeRefreshLayout swipePlayers;

    int count;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        preferences = getSharedPreferences("PlayerPrefs", MODE_PRIVATE);
        count = preferences.getInt("playerCount", 1);

        teamName = findViewById(R.id.teamName);
        players = findViewById(R.id.playerList);

        back_btn = findViewById(R.id.img_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamsActivity.this, MainActivity.class);
                intent.putExtra("UserEmail", Email);
                intent.putExtra("UserName",Name);
                intent.putExtra("teamName",t_Name);
                intent.putExtra("Phone",phone);
                startActivity(intent);
                finish();
            }
        });

        spinKit = findViewById(R.id.spin_kit);
        spinKit.setVisibility(View.VISIBLE);
        playerList = new ArrayList<ReadWriteTeamDetails>();


        Intent intent = getIntent();
        teamName.setText(intent.getStringExtra("teamName"));
        Name = intent.getStringExtra("Name");
        Email = intent.getStringExtra("Email");
        phone = intent.getStringExtra("Phone");
        t_Name = teamName.getText().toString();

        swipePlayers = findViewById(R.id.playerSwipe);
        swipePlayers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPlayers();
            }
        });

        getPlayers();


        fbPlus = findViewById(R.id.fb_plus);
        fbPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeamDialog();
            }
        });

    }
    private void getPlayers(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        playerList = null;
        playerList = new ArrayList<ReadWriteTeamDetails>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teams").child(user.getUid()).child(t_Name);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                count = (int) dataSnapshot.getChildrenCount();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("playerCount", count);
                editor.apply();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String playerName = childSnapshot.child("playerName").getValue(String.class); // Change "playerName" to the actual key
                    if (playerName != null) {
                        playerList.add(new ReadWriteTeamDetails(playerName)); // Add player name to the list
                    }
                    adapter = new PlayersAdapter(TeamsActivity.this,playerList, TeamsActivity.this, t_Name, preferences);
                    //setting recycler view
                    players.setLayoutManager(new LinearLayoutManager(TeamsActivity.this,LinearLayoutManager.VERTICAL,false));
                    players.setAdapter(adapter);

                    spinKit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(TeamsActivity.this,"No Players Found", Toast.LENGTH_LONG).show();
                spinKit.setVisibility(View.GONE);
            }
        });

        swipePlayers.setRefreshing(false);

    }
    private void showTeamDialog(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(TeamsActivity.this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = null;

        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.layout_add_team,
                        findViewById(R.id.bottomSheetContainer)
                );


        p_name = bottomSheetView.findViewById(R.id.txt_playerName);
        bat_style = bottomSheetView.findViewById(R.id.txt_batStyle);
        ball_style = bottomSheetView.findViewById(R.id.txt_BalStyle);


        //for adding player details
        bottomSheetView.findViewById(R.id.fb_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(p_name.getText().toString())) {
                    Toast.makeText(TeamsActivity.this, "Player Name cannot be empty", Toast.LENGTH_LONG).show();
                    p_name.setError("this field is required");
                    p_name.requestFocus();
                }
                else if(count < 11){
                    addTeamDetails(p_name.getText().toString(), bat_style.getText().toString(), ball_style.getText().toString());
                    preferences = getSharedPreferences("PlayerPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("playerCount", count);
                    editor.apply();
                    bottomSheetDialog.dismiss();
                }
                else{
                    Toast.makeText(TeamsActivity.this, "11 Players are already in the team", Toast.LENGTH_LONG).show();
                }
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }
    public void addTeamDetails(String pName, String batStyle, String ballStyle){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ReadWriteTeamDetails writeTeamDetails = new ReadWriteTeamDetails(user.getUid(), pName, batStyle, ballStyle);

        //Extracting User reference from Database for "Registered Users"
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teams");
        String playerKey = reference.child(user.getUid()).child(t_Name).push().getKey();


        reference.child(user.getUid()).child(t_Name).child(playerKey).setValue(writeTeamDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    p_name.setText("");
                    bat_style.setText("");
                    ball_style.setText("");
                    Toast.makeText(TeamsActivity.this,"Player added Succesfully",Toast.LENGTH_LONG).show();
                    getPlayers();
                }
            }
        });
        count++;
    }

}