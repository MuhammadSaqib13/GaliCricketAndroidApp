package com.example.galicricket;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000; // Splash display time in milliseconds
    private FirebaseAuth firebaseAuth;

    String username, Email, phone, teamName;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserStatus();
            }
        }, SPLASH_DELAY);
    }

    private void checkUserStatus() {
        if (isInternetAvailable()) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // User is signed in, navigate to MainActivity
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users").child(currentUser.getUid());

//            String username, email;

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            username = snapshot.child("name").getValue(String.class);
                            Email = snapshot.child("email").getValue(String.class);
                            teamName = snapshot.child("teamName").getValue(String.class);
                            phone = snapshot.child("mobileNumber").getValue(String.class);

                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK );

                            intent.putExtra("UserEmail",Email);
                            intent.putExtra("UserName",username);
                            intent.putExtra("teamName",teamName);
                            intent.putExtra("Phone",phone);
//
//                            Log.i("userName",username);
//                            Log.i("userEmail",Email);
                            intent.putExtra("UserId",currentUser.getUid());
                            startActivity(intent);

                            // Finish the current activity to prevent the user from coming back to it by pressing the back button
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                // User is not signed in, navigate to LoginActivity
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }
        else{
            // No internet, show a Toast message
            Toast.makeText(this, "Internet connection not available", Toast.LENGTH_SHORT).show();
            // Navigate to LoginActivity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
