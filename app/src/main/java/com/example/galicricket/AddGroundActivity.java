package com.example.galicricket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddGroundActivity extends AppCompatActivity {

    CardView datePicker, timePicker;
    TextView showDate, showTime;

    TextInputEditText grnd_name, grnd_add;
    FirebaseAuth auth;
    FirebaseUser user;
    SimpleDateFormat dateFormat, timeFormat;

    ProgressBar loader;
    FloatingActionButton fbCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ground);

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


        dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

        fbCheck = findViewById(R.id.fb_check);
        fbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                    addGround(grnd_name.getText().toString(), grnd_add.getText().toString(),date,time);

                }
            }
        });



    }
    private void addGround(String groundName, String groundAddress, String date, String time){

        Slot slot = new Slot(true, time);
        // Create a Map to hold the slots for the selected date
        Map<String, Map<String, Slot>> slotsMap = new HashMap<>();
        Map<String, Slot> dateSlots = new HashMap<>();

        Log.i("time", time);
        if (time.contains("am") || time.contains("AM")) {
            dateSlots.put("Morning", slot); // You can change "morning" as needed
        }
        else{
            dateSlots.put("Afternoon", slot); // You can change "morning" as needed
        }
        slotsMap.put(date, dateSlots);

        ReadWriteGroundDetails groundDetails = new ReadWriteGroundDetails(groundName,slotsMap, groundAddress );

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