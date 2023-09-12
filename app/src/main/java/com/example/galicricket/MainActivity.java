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
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    ImageView imgMenu, imgProf;
    String name, email,id, phone, teamName;
    TextView userName, userEmail, userId;

    FloatingActionButton fb_add;
    SpinKitView spinKit;

    RecyclerView teamRecView;
    TeamAdapter adapter;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;


    List<TeamInfo> teamList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgMenu = findViewById(R.id.img_dialog);
        fb_add = findViewById(R.id.fb_plus);
        imgProf = findViewById(R.id.img_prof);
        spinKit = findViewById(R.id.spin_kit);

        spinKit.setVisibility(View.VISIBLE);

        teamRecView = findViewById(R.id.TeamsList);


        userName = findViewById(R.id.userName);
        Intent intent = getIntent();
        userName.setText("Hi "+intent.getStringExtra("UserName"));

        name = intent.getStringExtra("UserName");
        phone= intent.getStringExtra("Phone");
        teamName = intent.getStringExtra("teamName");
        email = intent.getStringExtra("UserEmail");

        teamList = getTeams();



        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();

            }
        });

        imgProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                intent.putExtra("Email", email);
                intent.putExtra("Name",name);
                intent.putExtra("teamName",teamName);
                intent.putExtra("Phone",phone);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private List<TeamInfo> getTeams()
    {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Teams");
        //current user id
        String c_uid = user.getUid();

        teamList = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Log.i("userKey", userSnapshot.getKey());
                    if (!userSnapshot.getKey().equals(c_uid)) { // Exclude current user's teams
                        for (DataSnapshot teamSnapshot : userSnapshot.getChildren()) {
                            String teamName = teamSnapshot.getKey();
                            long playersCount = teamSnapshot.getChildrenCount();
                            Log.i("teamName", teamName);
                            Log.i("players", String.valueOf(playersCount));
                            teamList.add(new TeamInfo(teamName, playersCount));

                            Log.i("teamList", teamList.get(0).getTeamName());

                            adapter = new TeamAdapter(MainActivity.this, teamList);
                            teamRecView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
                            teamRecView.setAdapter(adapter);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"No teams Found",Toast.LENGTH_LONG).show();

            }
        });
        spinKit.setVisibility(View.GONE);

        return teamList;

    }

    private void showBottomDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = null;


        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.layout_bottom_sheet,
                        findViewById(R.id.bottomSheetContainer)
                );
        final View view = bottomSheetView;
        Intent intent = getIntent();
        userEmail = bottomSheetView.findViewById(R.id.txt_userEmail);
        userEmail.setText(intent.getStringExtra("UserEmail"));

        email = intent.getStringExtra("UserEmail");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = bottomSheetView.findViewById(R.id.userID);
        userId.setText("User Id: "+user.getUid());

        bottomSheetView.findViewById(R.id.prof_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                intent.putExtra("Email", email);
                intent.putExtra("Name",name);
                intent.putExtra("teamName",teamName);
                intent.putExtra("Phone",phone);
                startActivity(intent);
                bottomSheetDialog.dismiss();

            }
        });

        bottomSheetView.findViewById(R.id.txt_Teams).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Clicked Teams", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,TeamsActivity.class);
                intent.putExtra("Email", email);
                intent.putExtra("Name",name);
                intent.putExtra("teamName",teamName);
                intent.putExtra("Phone",phone);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.txt_Match).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MatchActivity.class);
                intent.putExtra("Email", email);
                intent.putExtra("Name",name);
                intent.putExtra("teamName",teamName);
                intent.putExtra("Phone",phone);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.btn_Logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }
}