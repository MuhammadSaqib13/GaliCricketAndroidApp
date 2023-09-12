package com.example.galicricket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MatchActivity extends AppCompatActivity {

    ImageView back_btn;

    String t_Name, Name, Email, phone;
    SpinKitView spinKit;

    FloatingActionButton fbPlus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        back_btn = findViewById(R.id.img_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchActivity.this, MainActivity.class);
                intent.putExtra("UserEmail", Email);
                intent.putExtra("UserName",Name);
                intent.putExtra("teamName",t_Name);
                intent.putExtra("Phone",phone);
                startActivity(intent);
                finish();
            }
        });

        spinKit = findViewById(R.id.spin_kit);
//        spinKit.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        //teamName.setText(intent.getStringExtra("teamName"));
        Name = intent.getStringExtra("Name");
        Email = intent.getStringExtra("Email");
        phone = intent.getStringExtra("Phone");
        t_Name = intent.getStringExtra("teamName");


        fbPlus = findViewById(R.id.fb_plus);
        fbPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchActivity.this,ScheduleActivity.class);
                intent.putExtra("Email", Email);
                intent.putExtra("Name",Name);
                intent.putExtra("teamName",t_Name);
                intent.putExtra("Phone",phone);
                startActivity(intent);
            }
        });

    }
}