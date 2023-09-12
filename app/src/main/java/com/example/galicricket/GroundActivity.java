package com.example.galicricket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.galicricket.GroundDetails.GroundAdapter;
import com.example.galicricket.GroundDetails.ReadWriteGroundDetails;
import com.example.galicricket.PlayersRecyclerView.PlayersAdapter;
import com.example.galicricket.TeamDetails.ReadWriteTeamDetails;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class GroundActivity extends AppCompatActivity {

    FloatingActionButton fbPlus;

    RecyclerView grounds;
    SpinKitView spinKit;

    ArrayList<ReadWriteGroundDetails> groundList = null;
    GroundAdapter adapter;

    SwipeRefreshLayout swipeGrounds;


    ImageView back_btn;
    EditText grnd_name, grnd_add;

    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ground);

        back_btn = findViewById(R.id.img_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroundActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            }
        });

        spinKit = findViewById(R.id.spin_kit);
        spinKit.setVisibility(View.VISIBLE);
        groundList = new ArrayList<>();
        grounds = findViewById(R.id.groundList);

        swipeGrounds = findViewById(R.id.groundSwipe);
        swipeGrounds.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGrounds();
            }
        });


        getGrounds();

        fbPlus = findViewById(R.id.fb_plus);
        fbPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroundActivity.this, AddGroundActivity.class);
                startActivity(intent);
            }
        });

    }
    public void getGrounds(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        groundList = null;
        groundList = new ArrayList<ReadWriteGroundDetails>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grounds");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String groundName = childSnapshot.child("name").getValue(String.class); //  "groundName" to get particular ground
                    String address = childSnapshot.child("address").getValue(String.class);
                    if (groundName != null) {
                        groundList.add(new ReadWriteGroundDetails(groundName,address)); // Add ground to the list
                    }

                }
                adapter = new GroundAdapter(GroundActivity.this,groundList, GroundActivity.this);
                grounds.setLayoutManager(new LinearLayoutManager(GroundActivity.this,LinearLayoutManager.VERTICAL,false));
                grounds.setAdapter(adapter);

                spinKit.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(GroundActivity.this,"No Ground Found", Toast.LENGTH_LONG).show();
                spinKit.setVisibility(View.GONE);
            }
        });

        swipeGrounds.setRefreshing(false);

    }

//    private void showGroundDialog(){
//        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(GroundActivity.this,R.style.BottomSheetDialogTheme);
//        View bottomSheetView = null;
//
//        bottomSheetView = LayoutInflater.from(getApplicationContext())
//                .inflate(
//                        R.layout.layout_add_ground,
//                        findViewById(R.id.bottomSheetContainer)
//                );
//
//
//        grnd_name = bottomSheetView.findViewById(R.id.txt_groundName);
//        grnd_add = bottomSheetView.findViewById(R.id.txt_Address);
//
//        bottomSheetView.findViewById(R.id.fb_check).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(grnd_name.getText().toString())) {
//                    Toast.makeText(GroundActivity.this, "Ground Name cannot be empty", Toast.LENGTH_LONG).show();
//                    grnd_name.setError("this field is required");
//                    grnd_name.requestFocus();
//                }
//                else if(TextUtils.isEmpty(grnd_add.getText().toString())){
//                    Toast.makeText(GroundActivity.this, "Address cannot be empty", Toast.LENGTH_LONG).show();
//                    grnd_add.setError("this field is required");
//                    grnd_add.requestFocus();
//                }
//                else{
//                    addGround(grnd_name.getText().toString(), grnd_add.getText().toString());
//                    bottomSheetDialog.dismiss();
//
//                }
//            }
//        });
//
//        bottomSheetDialog.setContentView(bottomSheetView);
//        bottomSheetDialog.show();
//
//    }
//    public void addGround(String GroundName, String Address){
//
//        ReadWriteGroundDetails writeGroundDetails = new ReadWriteGroundDetails(GroundName, Address);
//
//        //Extracting User reference from Database for "Grounds"
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grounds");
//        String groundKey = reference.push().getKey();
//
//
//        reference.child(groundKey).setValue(writeGroundDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    grnd_name.setText("");
//                    grnd_add.setText("");
//                    Toast.makeText(GroundActivity.this,"Ground added Succesfully",Toast.LENGTH_LONG).show();
//                    //getPlayers();
//                }
//            }
//        });
//    }
}