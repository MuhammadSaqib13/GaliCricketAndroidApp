package com.example.galicricket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView reg;

    CardView log_btn;
    TextInputEditText email,pass;

    FirebaseAuth auth;

    String username, Email, phone, teamName;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.txt_user);
        pass = findViewById(R.id.txt_password);
        progressBar = findViewById(R.id.loader);

        //To pass focus to password field
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

        //keyboard will dismiss
        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pass.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });





        //txt for register activity
        reg = findViewById(R.id.txt_register);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });


        log_btn = findViewById(R.id.btn_login);
        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email, Pass;

                Email = email.getText().toString();
                Pass = pass.getText().toString();


                Email = Email.replace(" ",""); //  replace spaces

                if (TextUtils.isEmpty(Email)){
                    Toast.makeText(LoginActivity.this, "Email cannot be empty",Toast.LENGTH_LONG).show();
                    email.setError("this field is required");
                    email.requestFocus();

                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    Toast.makeText(LoginActivity.this, "Please Enter Correct Email",Toast.LENGTH_LONG).show();
                    email.setError("Enter your Valid email address");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(Pass)){
                    Toast.makeText(LoginActivity.this, "Password cannot be empty",Toast.LENGTH_LONG).show();
                    pass.setError("this field is required");
                    pass.requestFocus();
                }
                else{
                    if (isInternetAvailable()) {
                        progressBar.setVisibility(View.VISIBLE);
                        loginUser(Email,Pass);
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Internet connection not available", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

    }

    public void loginUser(String Email, String pass) {
        auth = FirebaseAuth.getInstance();

        if(Email.equals("admin@galicricket.com") && pass.equals("admin@123")){
            //Admin Intent
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }
        else{
            auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser user = auth.getCurrentUser();
                        if (user.isEmailVerified()) {
                            progressBar.setVisibility(View.GONE);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users").child(user.getUid());


                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {

                                        username = snapshot.child("name").getValue(String.class);
                                        String email = snapshot.child("email").getValue(String.class);
                                        teamName = snapshot.child("teamName").getValue(String.class);
                                        phone = snapshot.child("mobileNumber").getValue(String.class);


                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_NEW_TASK );



                                        intent.putExtra("UserEmail",email);
                                        intent.putExtra("UserName",username);
                                        intent.putExtra("teamName",teamName);
                                        intent.putExtra("Phone",phone);

                                        intent.putExtra("UserId",user.getUid());
                                        startActivity(intent);
                                        finish();

//                                    Log.i("userName",snapshot.child("name").getValue(String.class));

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            user.sendEmailVerification();
                            auth.signOut();
                            showAlertDialog();
                        }

                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch(FirebaseAuthInvalidUserException e){
                            progressBar.setVisibility(View.GONE);
                            email.setError("User does not exists or is no longer valid. Please try again!");
                            email.requestFocus();

                        }
                        catch(FirebaseAuthInvalidCredentialsException e){
                            progressBar.setVisibility(View.GONE);
                            email.setError("Invalid Email or Password. Kindly check and re-enter.");
                            email.requestFocus();
                        }
                        catch(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });

        }

    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Verification Failed. Kindly confirm your email and sign in again. Thank you.")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        // Launch the Login Activity
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent);
                    }
                });

        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        if (isInternetAvailable()) {
            if (auth.getCurrentUser() != null) {

                FirebaseUser user = auth.getCurrentUser();
                if (!user.isEmailVerified()) {
                    Toast.makeText(LoginActivity.this,"Please Sign In Here",Toast.LENGTH_LONG).show();
                }
                else{

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users").child(user.getUid());

//            String username, email;

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                username = snapshot.child("name").getValue(String.class);
                                Email = snapshot.child("email").getValue(String.class);
                                teamName = snapshot.child("teamName").getValue(String.class);
                                phone = snapshot.child("mobileNumber").getValue(String.class);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK );

                                intent.putExtra("UserEmail",Email);
                                intent.putExtra("UserName",username);
                                intent.putExtra("teamName",teamName);
                                intent.putExtra("Phone",phone);

                                Log.i("userName",username);
                                Log.i("userEmail",Email);
                                intent.putExtra("UserId",user.getUid());
                                startActivity(intent);

                                // Finish the current activity to prevent the user from coming back to it by pressing the back button
                                finish();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
            else{
                Toast.makeText(LoginActivity.this,"Please Sign In Here",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "Internet connection not available", Toast.LENGTH_SHORT).show();

        }
    }
}