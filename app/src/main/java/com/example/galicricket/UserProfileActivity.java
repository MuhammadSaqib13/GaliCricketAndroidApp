package com.example.galicricket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    TextView Name, Email, Phone, TeamName;
    ImageView img_back, imgLogout;

    CircleImageView imgProfile;
    ImageButton btn_camera;
    ProgressBar loader;

    Button updateBtn, deleteBtn;

    FirebaseAuth auth;
    FirebaseUser user;

    StorageReference storage;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri storeUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Name = findViewById(R.id.txt_full_name);
        Email = findViewById(R.id.txt_email);
        Phone = findViewById(R.id.txt_phone);
        TeamName = findViewById(R.id.txt_team);

        img_back = findViewById(R.id.img_back);
        imgLogout = findViewById(R.id.img_logout);
        imgProfile = findViewById(R.id.imgvw_profile);
        btn_camera = findViewById(R.id.btn_camera);
        updateBtn = findViewById(R.id.btn_edit_profile);
        deleteBtn = findViewById(R.id.btn_delete_account);

        loader = findViewById(R.id.loader);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        storage = FirebaseStorage.getInstance().getReference("User Profile Pictures");
        loader.setVisibility(View.VISIBLE);

        Uri uri = user.getPhotoUrl();
        if (uri!=null) {
            Picasso.get().load(uri).into(imgProfile);
            loader.setVisibility(View.GONE);
        }
        else{
            loader.setVisibility(View.GONE);
        }


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                loader.setVisibility(View.VISIBLE);
                //uploadToFirebase(storeUri);
            }
        });



        Intent intent = getIntent();
        String name = intent.getStringExtra("Name");
        String email = intent.getStringExtra("Email");
        String phone = intent.getStringExtra("Phone");
        String teamName = intent.getStringExtra("teamName");

        Name.setText(name);
        Email.setText(email);
        Phone.setText(phone);
        TeamName.setText(teamName);


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();

                FirebaseUser user = auth.getCurrentUser();

                Intent intent1 = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
                intent1.putExtra("UserName",name);
                    intent1.putExtra("UserEmail",email);
                intent1.putExtra("Phone",phone);
                intent1.putExtra("teamName",teamName);
                intent1.putExtra("UserId",user.getUid());

                startActivity(intent1);
            }
        });



        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();

                FirebaseUser user = auth.getCurrentUser();

                Intent intent1 = new Intent(UserProfileActivity.this, MainActivity.class);
                intent1.putExtra("UserName",name);
                intent1.putExtra("UserEmail",email);
                intent1.putExtra("Phone",phone);
                intent1.putExtra("teamName",teamName);
                intent1.putExtra("UserId",user.getUid());

                startActivity(intent1);
                finish();
            }
        });

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uriImage = data.getData();
            imgProfile.setImageURI(uriImage);
            storeUri = uriImage;
            Log.i("uriImage", uriImage.toString());
            Log.i("uri", storeUri.toString());
            uploadToFirebase(storeUri);

        }
    }

    private void uploadToFirebase(Uri uri)
    {
        if (uri!=null) {

            Log.i("uploadUri",uri.toString());
            StorageReference fileReference = storage.child(user.getUid() + "."
                    + getFileExtension(uri));

            //upload file to storage
            fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();

                            user.updateProfile(profileUpdates);
                        }
                    });

                }
            });
            loader.setVisibility(View.GONE);
            Toast.makeText(this,"Profile Picture Uploaded Successfully", Toast.LENGTH_LONG).show();
        }
        else{
            Log.i("Info", "Uri is Empty "+uri);
        }
    }

    private String getFileExtension(Uri uriImage){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        Log.i("imageUri", mime.getExtensionFromMimeType(cR.getType(uriImage)));
        return mime.getExtensionFromMimeType(cR.getType(uriImage));
    }

}