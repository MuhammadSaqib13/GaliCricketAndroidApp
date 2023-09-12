package com.example.galicricket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.galicricket.GroundDetails.GroundAdapter;
import com.example.galicricket.GroundDetails.ReadWriteGroundDetails;
import com.example.galicricket.TeamDetails.TeamAdapter;
import com.example.galicricket.TeamDetails.TeamInfo;
import com.github.ybq.android.spinkit.SpinKitView;
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

public class AdminActivity extends AppCompatActivity {

    ImageView imgMenu;
    SpinKitView spinKit, grndSpinKit;

    RecyclerView teamRecView, grndRecView;
    TeamAdapter adapter;

    GroundAdapter groundAdapter;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    List<ReadWriteGroundDetails> groundList;

    List<TeamInfo> teamList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        imgMenu = findViewById(R.id.img_dialog);
        spinKit = findViewById(R.id.spin_kit);
        grndSpinKit = findViewById(R.id.spin_kit_ground);

        spinKit.setVisibility(View.VISIBLE);
        grndSpinKit.setVisibility(View.VISIBLE);

        teamRecView = findViewById(R.id.TeamsList);
        teamList = getTeams();

        grndRecView = findViewById(R.id.GroundList);
        getGrounds();



        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();

            }
        });

    }
    private void getGrounds(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        groundList = null;
        groundList = new ArrayList<ReadWriteGroundDetails>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grounds");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String groundName = childSnapshot.child("name").getValue(String.class); // Change "playerName" to the actual key
                    String address = childSnapshot.child("address").getValue(String.class);
                    if (groundName != null) {
                        groundList.add(new ReadWriteGroundDetails(groundName,address)); // Add player name to the list
                    }

                }
                groundAdapter = new GroundAdapter(AdminActivity.this,groundList);
                grndRecView.setLayoutManager(new LinearLayoutManager(AdminActivity.this,LinearLayoutManager.VERTICAL,false));
                grndRecView.setAdapter(groundAdapter);

                grndSpinKit.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(AdminActivity.this,"No Ground Found", Toast.LENGTH_LONG).show();
                grndSpinKit.setVisibility(View.GONE);
            }
        });

    }

    private List<TeamInfo> getTeams()
    {

        reference = FirebaseDatabase.getInstance().getReference().child("Teams");

        teamList = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Log.i("userKey", userSnapshot.getKey());
                    for (DataSnapshot teamSnapshot : userSnapshot.getChildren()) {
                        String teamName = teamSnapshot.getKey();
                        long playersCount = teamSnapshot.getChildrenCount();
                        Log.i("teamName", teamName);
                        Log.i("players", String.valueOf(playersCount));
                        teamList.add(new TeamInfo(teamName, playersCount));

                        Log.i("teamList", teamList.get(0).getTeamName());

                    }
                }
                adapter = new TeamAdapter(AdminActivity.this, teamList);
                teamRecView.setLayoutManager(new LinearLayoutManager(AdminActivity.this,LinearLayoutManager.VERTICAL,false));
                teamRecView.setAdapter(adapter);


                spinKit.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this,"No teams Found",Toast.LENGTH_LONG).show();

            }
        });

        return teamList;

    }

    private void showBottomDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AdminActivity.this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = null;


        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.layout_bottom_admin,
                        findViewById(R.id.bottomSheetContainer)
                );
        final View view = bottomSheetView;


        bottomSheetView.findViewById(R.id.txt_Ground).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminActivity.this, "Clicked Ground", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminActivity.this,GroundActivity.class);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.btn_Logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }


}