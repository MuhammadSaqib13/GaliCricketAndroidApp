package com.example.galicricket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.galicricket.GroundDetails.ReadWriteGroundDetails;
import com.example.galicricket.TeamDetails.SelectTeamAdapter;
import com.example.galicricket.TeamDetails.TeamAdapter;
import com.example.galicricket.TeamDetails.TeamInfo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {

    CardView datePicker, timePicker;
    TextView showDate, showTime, teamName, selectTeam;

    ProgressBar loader;
    SelectTeamAdapter adapter;
    RecyclerView teamRecView;
    List<TeamInfo> teams;

    FloatingActionButton fb_check;
    FirebaseAuth auth;
    FirebaseUser user;

    EditText oversTxt;
    DatabaseReference reference;

    AutoCompleteTextView txt_ground;
    private static final ArrayList<String> grnd_autocomplete = new ArrayList<String>();
    private static final Map<String,String> GroundList = new HashMap<String,String>();
    SimpleDateFormat dateFormat, timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        txt_ground = findViewById(R.id.gorundlistautocomplete);
        getGrounds();

        selectTeam = findViewById(R.id.teamB);
        selectTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeamDialog();
            }
        });

        datePicker = findViewById(R.id.datePickerCardView);
        timePicker = findViewById(R.id.timePickerCardView);

        showDate = findViewById(R.id.selectedDateTextView);
        showTime = findViewById(R.id.selectedTimeTextView);
        teamName = findViewById(R.id.teamA);
        loader = findViewById(R.id.loader);

        Intent intent = getIntent();
        teamName.setText(intent.getStringExtra("teamName"));

        dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);


        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });


        oversTxt = findViewById(R.id.selectovers);

        fb_check = findViewById(R.id.fb_check);
        fb_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedTeam = selectTeam.getText().toString();
                String date = showDate.getText().toString();
                String time = showTime.getText().toString();
                String ground = txt_ground.getText().toString();
                String overs = oversTxt.getText().toString();

                Log.i("ground", ground);
                if (TextUtils.isEmpty(date)) {
                    Toast.makeText(ScheduleActivity.this,"Please select date",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(time)){
                    Toast.makeText(ScheduleActivity.this,"Please select time",Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(ground)) {
                    Toast.makeText(ScheduleActivity.this,"Please select ground",Toast.LENGTH_LONG).show();
                    txt_ground.setError("Select ground from the list");
                    txt_ground.requestFocus();
                }
                else if(selectedTeam.equals("Select Team")){
                    Toast.makeText(ScheduleActivity.this,"Please select a Team",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(overs)){
                    Toast.makeText(ScheduleActivity.this,"Please enter overs",Toast.LENGTH_LONG).show();
                    oversTxt.setError("Enter Overs");
                    oversTxt.requestFocus();
                }
                else{
                    loader.setVisibility(View.VISIBLE);
//                    schdeuleMatch(selectedTeam, date, time, ground,overs);

                }


            }
        });


    }

    private void showTeamDialog(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ScheduleActivity.this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = null;

        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.layout_teams,
                        findViewById(R.id.bottomSheetContainer)
                );

        teamRecView = bottomSheetView.findViewById(R.id.teamsRecView);
        teams = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Teams");
        String c_uid = user.getUid();


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
                            teams.add(new TeamInfo(teamName));

                            Log.i("teamList", teams.get(0).getTeamName());

                            adapter = new SelectTeamAdapter(teams);
                            teamRecView.setLayoutManager(new LinearLayoutManager(ScheduleActivity.this,LinearLayoutManager.VERTICAL,false));
                            teamRecView.setAdapter(adapter);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScheduleActivity.this,"No teams Found",Toast.LENGTH_LONG).show();

            }
        });


        bottomSheetView.findViewById(R.id.fb_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a team is selected
                for (TeamInfo team : teams) {
                    if (team.isSelected()) {
                        // Display the selected team's name in your TextView
                        selectTeam.setText(team.getTeamName());
                        selectTeam.setTextColor(Color.parseColor("#000000"));
                        // Dismiss the BottomSheetDialog
                        bottomSheetDialog.dismiss();
                        break; // No need to continue checking
                    }
                }
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();



    }

    private void getGrounds(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, grnd_autocomplete);
        txt_ground = findViewById(R.id.gorundlistautocomplete);
        txt_ground.setAdapter(adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grounds");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String groundName = childSnapshot.child("name").getValue(String.class); //  "groundName" to get particular ground
                    String address = childSnapshot.child("address").getValue(String.class);
                    String key = childSnapshot.getKey();
                    if (groundName != null) {
                        grnd_autocomplete.add(groundName);// Add ground to the list
                        GroundList.put(key,groundName);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void showTimePicker() {
        // Get the current time as the initial time for the TimePicker
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Handle the selected time
                        Calendar selectedCalendar = getTime(hourOfDay, minute);
                        String formattedTime = timeFormat.format(selectedCalendar.getTime());
                        showTime.setText(formattedTime);
                        showTime.setVisibility(View.VISIBLE);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // Set to true for 24-hour format
        );

        timePickerDialog.show();
    }

    private Calendar getTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }

    private void showDatePicker(){

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ScheduleActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Handle the selected date
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, monthOfYear, dayOfMonth);
                        String formattedDate = dateFormat.format(selectedCalendar.getTime());
                        showDate.setText(formattedDate);
                        showDate.setVisibility(View.VISIBLE);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

}