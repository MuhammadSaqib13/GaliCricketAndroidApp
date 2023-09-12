package com.example.galicricket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galicricket.RegisterUserDetails.ReadWriteUserDetails;
import com.example.galicricket.TeamDetails.ReadWriteTeamDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TextView log;
    ImageView back;

    //Register Btn
    CardView reg;

    //Progress Bar
    ProgressBar progressBar;

    //
    boolean exists;

    TextInputEditText name, teamName, phone, email, pass, cnfPass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //finding all views
        name = findViewById(R.id.txt_name);
        teamName = findViewById(R.id.txt_team);
        phone = findViewById(R.id.txt_phone);
        email = findViewById(R.id.txt_user);
        pass = findViewById(R.id.txt_password);
        cnfPass = findViewById(R.id.txt_cnfpassword);
        progressBar = findViewById(R.id.loader);


        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    teamName.requestFocus();
                    return true;
                }
                return false;
            }
        });

        teamName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    phone.requestFocus();
                    return true;
                }
                return false;
            }
        });

        phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    email.requestFocus();
                    return true;
                }
                return false;
            }
        });

        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    pass.requestFocus();
                    return true;
                }
                return false;
            }
        });

        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    cnfPass.requestFocus();
                    return true;
                }
                return false;
            }
        });

        cnfPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //Close the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(cnfPass.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        reg = findViewById(R.id.btn_register);


        log = findViewById(R.id.txt_login);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

        back = findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name, team, number, Email, Pass, CnfPass;

                Name = String.valueOf(name.getText());
                team = String.valueOf(teamName.getText());
                number = String.valueOf(phone.getText());

                Email = String.valueOf(email.getText());
                Pass = String.valueOf(pass.getText());
                CnfPass = String.valueOf(cnfPass.getText());


                //validate mobile number
//                String regex = "^(\\+92|0)3[0-9]{2}-?[0-9]{7}$"; //0365, 0377, not accepted
                String regex = "^(\\+92|0)3[0-4][0-9]-?[0-9]{7}$";
                Matcher mobileMatcher;

                Pattern mobilePattern = Pattern.compile(regex);
                mobileMatcher = mobilePattern.matcher(number);



                if (TextUtils.isEmpty(Name)){
                    Toast.makeText(RegisterActivity.this, "Name cannot be empty",Toast.LENGTH_LONG).show();
                    name.setError("this field is required");
                    name.requestFocus();

                }
                else if(TextUtils.isEmpty(team)){
                    Toast.makeText(RegisterActivity.this, "Team Name cannot be empty",Toast.LENGTH_LONG).show();
                    teamName.setError("this field is required");
                    teamName.requestFocus();
                }
                else if(TextUtils.isEmpty(Email)){
                    Toast.makeText(RegisterActivity.this, "Email cannot be empty",Toast.LENGTH_LONG).show();
                    email.setError("this field is required");
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    Toast.makeText(RegisterActivity.this, "Please Enter Correct Email",Toast.LENGTH_LONG).show();
                    email.setError("Enter your Valid email address");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(number)){
                    Toast.makeText(RegisterActivity.this, "Number cannot be empty",Toast.LENGTH_LONG).show();
                    phone.setError("this field is required");
                    phone.requestFocus();
                }
                else if(!mobileMatcher.find()){
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid mobile number",Toast.LENGTH_LONG).show();
                    phone.setError("Please enter valid mobile number e.g: 03351234567");
                    phone.requestFocus();
                }
                else if(number.length() != 11){
                    Toast.makeText(RegisterActivity.this, "Please enter 11-digit mobile number",Toast.LENGTH_LONG).show();
                    phone.setError("Mobile number should be of 11-digits");
                    phone.requestFocus();
                }
                else if(TextUtils.isEmpty(Pass)){
                    Toast.makeText(RegisterActivity.this, "Password cannot be empty",Toast.LENGTH_LONG).show();
                    pass.setError("Password must be at least 8 characters long");
                    pass.requestFocus();
                }
                else if(TextUtils.isEmpty(CnfPass)){
                    Toast.makeText(RegisterActivity.this, "Please Re-enter your password",Toast.LENGTH_LONG).show();
                    cnfPass.setError("Password confirmation is required");
                    cnfPass.requestFocus();
                }
                else if(!Pass.equals(CnfPass)){
                    Toast.makeText(RegisterActivity.this, "Confirm Password Should be same",Toast.LENGTH_LONG).show();
                    cnfPass.setError("Password doesn't match");
                    cnfPass.requestFocus();

                    //clear the edit text
                    pass.clearComposingText();
                    cnfPass.clearComposingText();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(Name, team, number, Email, Pass);

                }

            }
        });


    }

    //Register the User
    private void registerUser(String name, String team, String number, String Email, String pass) {
//        exists = checkMobileNumber(number);

//        if (exists) {
//            progressBar.setVisibility(View.GONE);
//            Log.i("checkMobile","Checked Phone Number");
//
//        }
//        else {
//            exists = checkTeamName(team);
//            if (exists) {
//                progressBar.setVisibility(View.GONE);
//                Log.i("checkTeamName", "Checked Team Name");
//            }
//            else{
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(Email, pass).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            //Enter User Data into the Firebase Realtime Database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(name, team, number, Email);

                            //Enter Details in the team as well
                            ReadWriteTeamDetails writeTeamDetails = new ReadWriteTeamDetails(user.getUid(),name,"","");

                            //Extracting User reference from Database for "Registered Users"
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");

                            //ADDING 1ST PLAYER INTO TEAM AUTOMATICALLY
                            DatabaseReference teamRefernece = FirebaseDatabase.getInstance().getReference("Teams");

                            String playerKey = reference.child(user.getUid()).child(team).push().getKey();



                            teamRefernece.child(user.getUid()).child(team).child(playerKey).setValue(writeTeamDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i("MemberAdded", "Team Member Added");
                                }
                            });

                            reference.child(user.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                        user.sendEmailVerification();
                                        progressBar.setVisibility(View.GONE);
                                        showVerificationAlertDialog();

                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                progressBar.setVisibility(View.GONE);
                                email.setError("User is already exist");
                                email.requestFocus();
                            } catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
    }


    private boolean checkMobileNumber(String Phone){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.orderByChild("mobileNumber").equalTo(Phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exists = snapshot.exists();
                Log.i("phone","Mobile number exists");
                phone.setError("Phone number is already registered.");
                phone.requestFocus();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exists = false;

            }
        });
        return exists;
    }

    private boolean checkTeamName(String TeamName){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.orderByChild("teamName").equalTo(TeamName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exists = snapshot.exists();
                Log.i("teamName","Team Name exists");
                teamName.setError("Team Name is already Exist.");
                teamName.requestFocus();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exists = false;

            }
        });

        return exists;
    }


    private void showVerificationAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We have sent a verification email. Kindly confirm your email and sign in again. Thank you.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        // Launch the Login Activity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK );
                            startActivity(intent);

                        // Finish the current activity to prevent the user from coming back to it by pressing the back button
                        finish();
                    }
                });

        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}