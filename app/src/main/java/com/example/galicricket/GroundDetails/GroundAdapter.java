package com.example.galicricket.GroundDetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galicricket.AddGroundActivity;
import com.example.galicricket.GroundActivity;
import com.example.galicricket.PlayersRecyclerView.PlayersAdapter;
import com.example.galicricket.R;
import com.example.galicricket.TeamDetails.ReadWriteTeamDetails;
import com.example.galicricket.TeamsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GroundAdapter extends RecyclerView.Adapter<GroundAdapter.GroundViewHolder>{

    private final Context context;
    private List<ReadWriteGroundDetails> groundList;

    private EditText groundTxt, locTxt;
    GroundActivity ground;

    int count;
    SharedPreferences preferences;

    private RecyclerView playerRecView;

    String groundName, groundKey;

    FirebaseAuth auth;
    FirebaseUser user;

    public GroundAdapter(Context context, List<ReadWriteGroundDetails> groundList, GroundActivity ground) {
        this.context = context;
        this.groundList = groundList;
        this.ground = ground;
    }

    public GroundAdapter(Context context, List<ReadWriteGroundDetails> groundList) {
        this.context = context;
        this.groundList = groundList;
    }

    @NonNull
    @Override
    public GroundAdapter.GroundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(R.layout.ground_item_layout,null);
        GroundAdapter.GroundViewHolder holder =new GroundAdapter.GroundViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroundAdapter.GroundViewHolder holder, int position) {
        ReadWriteGroundDetails model = groundList.get(position);
        holder.ground_name.setText(model.getName());
        holder.location.setText(model.getAddress());
        holder.imgText.setText(String.valueOf(model.getName().charAt(0)));

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AddGroundActivity.class);
                intent.putExtra("groundName",holder.ground_name.getText().toString());
                intent.putExtra("location",holder.location.getText().toString());

//                intent.putExtra("slots",model.getSlots().get(position).toString());
                intent.putExtra("adapter","fromAdapter");
                context.startActivity(intent);

                //holder.showBottomDialog(ground);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference groundReference = FirebaseDatabase.getInstance().getReference("Grounds");

                groundReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                            String groundName = childSnapshot.child("name").getValue(String.class);


                            if (holder.ground_name.getText().toString().equals(groundName)) {
                                    groundKey = childSnapshot.getKey();
                                    Log.i("key", groundKey);
                                    holder.deleteGround(groundKey);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return groundList.size();
    }


    class GroundViewHolder extends RecyclerView.ViewHolder{
        CardView ground_Card;
        TextView ground_name, location, imgText;
        ImageView edit, delete;

        public GroundViewHolder(@NonNull View itemView) {
            super(itemView);
            ground_Card = itemView.findViewById(R.id.recycler_Card);
            ground_name = itemView.findViewById(R.id.grndText);
            location = itemView.findViewById(R.id.locText);
            edit = itemView.findViewById(R.id.img_edit);
            imgText = itemView.findViewById(R.id.imgText);
            delete = itemView.findViewById(R.id.img_delete);
        }

        void showBottomDialog(GroundActivity ground){
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ground,R.style.BottomSheetDialogTheme);
            View bottomSheetView = null;

            bottomSheetView = LayoutInflater.from(context)
                    .inflate(
                            R.layout.layout_add_ground,
                            ground.findViewById(R.id.bottomSheetContainer)
                    );

            groundTxt = bottomSheetView.findViewById(R.id.txt_groundName);
            groundTxt.setText(ground_name.getText().toString());

            locTxt = bottomSheetView.findViewById(R.id.txt_Address);
            locTxt.setText(location.getText().toString());

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grounds");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String groundName = childSnapshot.child("name").getValue(String.class);

                        if (ground_name.getText().toString().equals(groundName)) {
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

            bottomSheetView.findViewById(R.id.fb_check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateGroundDetails(groundTxt.getText().toString(),locTxt.getText().toString(), groundKey);
                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();


        }
        void updateGroundDetails(String groundName,String location, String key){

            ReadWriteGroundDetails writeGroundDetails = new ReadWriteGroundDetails(groundName, location);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grounds");

            reference.child(key).setValue(writeGroundDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        groundTxt.setText("");
                        locTxt.setText("");
                        Toast.makeText(context,"Changes applied",Toast.LENGTH_LONG).show();
//                        GroundActivity groundActivity = new GroundActivity();
//                        groundActivity.getGrounds();
                        notifyDataSetChanged();
                    }
                }
            });
        }
        void deleteGround(String key) {

            DatabaseReference groundReference = FirebaseDatabase.getInstance().getReference("Grounds").child(key);

            groundReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Ground deleted Successfully", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(context, "Error deleting ground", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
}
