package com.example.galicricket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.galicricket.BookingDetails.Slot;
import com.example.galicricket.GroundDetails.ReadWriteGroundDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
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

public class AddGroundActivity extends AppCompatActivity {

    CardView datePicker, timePicker;
    TextView showDate, showTime, addSlots;

    TextInputEditText grnd_name, grnd_add;
    FirebaseAuth auth;
    FirebaseUser user;
    SimpleDateFormat dateFormat, timeFormat;

    DatabaseReference databaseReference;

    private Map<String, Map<String, List<Slot>>> slotsMap = new HashMap<>();
    private Map<String, List<Slot>> dateSlots = new HashMap<>();

    private List<Slot> timingSlots = new ArrayList<>();
    ProgressBar loader;
    FloatingActionButton fbCheck;

    String fromAdapter, groundKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ground);

        databaseReference = FirebaseDatabase.getInstance().getReference("Grounds");

        addSlots = findViewById(R.id.btnSlot);
        grnd_name = findViewById(R.id.txt_groundName);
        grnd_add = findViewById(R.id.txt_Address);

        datePicker = findViewById(R.id.datePickerCardView);
        timePicker = findViewById(R.id.timePickerCardView);

        showDate = findViewById(R.id.selectedDateTextView);
        showTime = findViewById(R.id.selectedTimeTextView);
        loader = findViewById(R.id.loader);

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


        Intent intent = getIntent();
        fromAdapter = intent.getStringExtra("adapter");
        String groundName = intent.getStringExtra("groundName");
        String location = intent.getStringExtra("location");

        grnd_name.setText(groundName);
        grnd_add.setText(location);



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grounds");


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String groundName = childSnapshot.child("name").getValue(String.class);

                    if (grnd_name.getText().toString().equals(groundName)) {
                        groundKey = childSnapshot.getKey();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

        addSlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate = showDate.getText().toString();
                String selectedTiming = showTime.getText().toString();

                if (!selectedDate.equals("Select Date") && !selectedTiming.equals("Select Time")) {
                    Slot slot = new Slot(true, selectedTiming);

                    // Check if slots for the selected date already exist in the slotsMap
                    Map<String, List<Slot>> dateSlots = slotsMap.get(selectedDate);
                    if (dateSlots == null) {
                        dateSlots = new HashMap<>();
                        slotsMap.put(selectedDate, dateSlots);
                    }

                    // Add the new slot to the dateSlots map
                    List<Slot> timingSlots = dateSlots.get(selectedTiming);
                    if (timingSlots == null) {
                        timingSlots = new ArrayList<>();
                        dateSlots.put(selectedTiming, timingSlots);
                    }

                    timingSlots.add(slot);

                    Toast.makeText(AddGroundActivity.this, "Slot Added!",Toast.LENGTH_LONG).show();

                    // Optionally, display the selected slots to the user in a TextView or RecyclerView
                }
                else{
                    Toast.makeText(AddGroundActivity.this, "Date and time cannot be empty",Toast.LENGTH_LONG).show();

                }
            }
        });


        fbCheck = findViewById(R.id.fb_check);
        fbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(fromAdapter)) {
                    loader.setVisibility(View.VISIBLE);

                    String date = showDate.getText().toString();
                    String time = showTime.getText().toString();

                    if (TextUtils.isEmpty(grnd_name.getText().toString())) {
                        Toast.makeText(AddGroundActivity.this, "Ground Name cannot be empty", Toast.LENGTH_LONG).show();
                        grnd_name.setError("this field is required");
                        grnd_name.requestFocus();
                    }
                    else if(TextUtils.isEmpty(grnd_add.getText().toString())){
                        Toast.makeText(AddGroundActivity.this, "Address cannot be empty", Toast.LENGTH_LONG).show();
                        grnd_add.setError("this field is required");
                        grnd_add.requestFocus();
                    }
                    else if (TextUtils.isEmpty(date)) {
                        Toast.makeText(AddGroundActivity.this,"Please select date",Toast.LENGTH_LONG).show();
                    }
                    else if(TextUtils.isEmpty(time)){
                        Toast.makeText(AddGroundActivity.this,"Please select time",Toast.LENGTH_LONG).show();
                    }
                    else{
                        updateGroundDetails(grnd_name.getText().toString(),grnd_add.getText().toString(),date,time, groundKey);
                    }


                }
                else{
                    String date = showDate.getText().toString();
                    String time = showTime.getText().toString();

                    if (TextUtils.isEmpty(grnd_name.getText().toString())) {
                        Toast.makeText(AddGroundActivity.this, "Ground Name cannot be empty", Toast.LENGTH_LONG).show();
                        grnd_name.setError("this field is required");
                        grnd_name.requestFocus();
                    }
                    else if(TextUtils.isEmpty(grnd_add.getText().toString())){
                        Toast.makeText(AddGroundActivity.this, "Address cannot be empty", Toast.LENGTH_LONG).show();
                        grnd_add.setError("this field is required");
                        grnd_add.requestFocus();
                    }
                    else if (TextUtils.isEmpty(date)) {
                        Toast.makeText(AddGroundActivity.this,"Please select date",Toast.LENGTH_LONG).show();
                    }
                    else if(TextUtils.isEmpty(time)){
                        Toast.makeText(AddGroundActivity.this,"Please select time",Toast.LENGTH_LONG).show();
                    }
                    else{
                        loader.setVisibility(View.VISIBLE);
                        addGround(grnd_name.getText().toString(), grnd_add.getText().toString());

                    }

                }

            }
        });

    }
    private void updateGroundDetails(String groundName,String location,String date, String time, String key){


        ReadWriteGroundDetails groundDetails = new ReadWriteGroundDetails(groundName,slotsMap, location);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grounds");

        reference.child(key).setValue(groundDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddGroundActivity.this,"Changes applied",Toast.LENGTH_LONG).show();
//                        GroundActivity groundActivity = new GroundActivity();
//                        groundActivity.getGrounds();

                }
            }
        });
    }

    private void addGround(String groundName, String groundAddress){

        ReadWriteGroundDetails groundDetails = new ReadWriteGroundDetails(groundName,slotsMap, groundAddress );
        // Push the ground to Firebase
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(groundDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Display a success message
                    Toast.makeText(AddGroundActivity.this, "Ground added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddGroundActivity.this,GroundActivity.class));
                    finish();

                }
            }
        });

        loader.setVisibility(View.GONE);

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
                this,
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