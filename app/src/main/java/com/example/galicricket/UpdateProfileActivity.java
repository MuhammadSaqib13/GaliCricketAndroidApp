package com.example.galicricket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.galicricket.RegisterUserDetails.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    TextInputEditText nameTxt, phoneTxt, teamText;
    ProgressBar loader;

    ImageView backBtn;
    FloatingActionButton fb_update;

    FirebaseAuth auth;
    FirebaseUser user;

    String email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        loader = findViewById(R.id.loader);
//        loader.setVisibility(View.VISIBLE);

        nameTxt = findViewById(R.id.txt_fName);
        phoneTxt = findViewById(R.id.txt_phone);
        teamText = findViewById(R.id.txt_teamName);
        backBtn = findViewById(R.id.img_back);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileActivity.super.onBackPressed();
                finish();
            }
        });


        Intent intent = getIntent();
        String name = intent.getStringExtra("UserName");
        email = intent.getStringExtra("UserEmail");
        String phone = intent.getStringExtra("Phone");
        String teamName = intent.getStringExtra("teamName");

        nameTxt.setText(name);
        phoneTxt.setText(phone);
        teamText.setText(teamName);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        fb_update = findViewById(R.id.fb_update);
        fb_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.setVisibility(View.VISIBLE);
                updateProfile(user, teamName);
            }
        });




    }
    private void updateProfile(FirebaseUser user, String OldteamName){

        String Name, team, number;

        Name = String.valueOf(nameTxt.getText());
        team = String.valueOf(teamText.getText());
        number = String.valueOf(phoneTxt.getText());


        //validate mobile number
//                String regex = "^(\\+92|0)3[0-9]{2}-?[0-9]{7}$";
        String regex = "^(\\+92|0)3[0-4][0-9]-?[0-9]{7}$";
        Matcher mobileMatcher;

        Pattern mobilePattern = Pattern.compile(regex);
        mobileMatcher = mobilePattern.matcher(number);



        if (TextUtils.isEmpty(Name)){
            Toast.makeText(UpdateProfileActivity.this, "Name cannot be empty",Toast.LENGTH_LONG).show();
            nameTxt.setError("this field is required");
            nameTxt.requestFocus();

        }
        else if(TextUtils.isEmpty(team)){
            Toast.makeText(UpdateProfileActivity.this, "Team Name cannot be empty",Toast.LENGTH_LONG).show();
            teamText.setError("this field is required");
            teamText.requestFocus();
        }
        else if(TextUtils.isEmpty(number)){
            Toast.makeText(UpdateProfileActivity.this, "Number cannot be empty",Toast.LENGTH_LONG).show();
            phoneTxt.setError("this field is required");
            phoneTxt.requestFocus();
        }
        else if(!mobileMatcher.find()){
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Valid mobile number",Toast.LENGTH_LONG).show();
            phoneTxt.setError("Please enter valid mobile number e.g: 03351234567");
            phoneTxt.requestFocus();
        }
        else if(number.length() != 11){
            Toast.makeText(UpdateProfileActivity.this, "Please enter 11-digit mobile number",Toast.LENGTH_LONG).show();
            phoneTxt.setError("Mobile number should be of 11-digits");
            phoneTxt.requestFocus();
        }
        else{
            loader.setVisibility(View.VISIBLE);
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(Name,team,number,email);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
            reference.child(user.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        //setting new Display name
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(Name).build();

                        user.updateProfile(profileChangeRequest);

                        Toast.makeText(UpdateProfileActivity.this, "Changes Updated Successfully", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        intent.putExtra("Name", Name);
                        intent.putExtra("Email", email);
                        intent.putExtra("Phone", number);
                        intent.putExtra("teamName", team);
                        intent.putExtra("UserId", user.getUid());

                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    loader.setVisibility(View.GONE);
                }
            });

            DatabaseReference teamReference = FirebaseDatabase.getInstance().getReference("Teams");
            teamReference.child(user.getUid()).child(OldteamName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Save the child nodes data
                        Map<String, Object> childNodes = (Map<String, Object>) snapshot.getValue();

                        // Delete the old team node
                        teamReference.child(user.getUid()).child(OldteamName).setValue(null);

                        // Update the team name in the data
                        teamReference.child(user.getUid()).child(team).setValue(childNodes);


                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}