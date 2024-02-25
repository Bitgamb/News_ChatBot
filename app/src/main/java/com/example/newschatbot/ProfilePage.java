package com.example.newschatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfilePage extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    TextView titleName,titleUsername,profileName,profileEmail,profilePhone,profileMembership;
    ImageView profilePicButton,backButton,logout,profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profileImg= findViewById(R.id.profileImg);
        titleName=findViewById(R.id.titleName);
        titleUsername=findViewById(R.id.titleUsername);
        profileName=findViewById(R.id.profileName);
        profileEmail=findViewById(R.id.profileEmail);
        profilePhone=findViewById(R.id.profilePhone);
        profileMembership=findViewById(R.id.profileMembership);
        //buttons
        profilePicButton=findViewById(R.id.profilePicButton);
        backButton=findViewById(R.id.backButton);
        logout=findViewById(R.id.logout);

        loadProfilePicture();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                startActivity(intent);
                finish();
            }
        });

        logout.setOnClickListener(view -> logoutUser());

        // Fetch and set each profile attribute
        getAndSetProfileEmail();

        getAndSetProfilePhone();
        getAndSetProfileMembership();
        getAndSetTitleName();
        // Modify your button click listener to choose and upload a profile picture
        profilePicButton.setOnClickListener(view -> {
            // Open a file picker to choose a profile picture
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        });

    }
    private void getAndSetTitleName() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.child(uid).child("name").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String titleName = task.getResult().getValue(String.class);
                if (titleName != null) {
                    this.titleName.setText(titleName);
                    this.profileName.setText(titleName);

                }
            } else {
                // Handle the error
            }
        });
    }

    private void getAndSetTitleUsername() {
        // Similar to the above method, replace "titleName" with "titleUsername"
        // and set the text to titleUsername TextView
    }



    private void getAndSetProfileEmail() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        this.profileEmail.setText(email);
    }

    private void getAndSetProfilePhone() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.child(uid).child("phone").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String profilePhone = task.getResult().getValue(String.class);
                if (profileName != null) {
                    this.profilePhone.setText(profilePhone);
                }
            } else {
                // Handle the error
            }
        });
    }

    private void getAndSetProfileMembership() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.child(uid).child("orderId").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String orderId = task.getResult().getValue(String.class);

                if (orderId != null && !orderId.isEmpty()) {
                    // If orderId is not empty, set text to "Premium"
                    this.profileMembership.setText("Premium");
                } else {
                    // If orderId is empty, set text to "Trial"
                    this.profileMembership.setText("Trial");
                }
            } else {
                // Handle the error
            }
        });
    }
    private void logoutUser() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        // Redirect to the login or splash screen
        Intent intent = new Intent(ProfilePage.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    // Method to upload profile picture to Firebase Storage
    private void uploadProfilePicture(Uri filePath) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        StorageReference profilePicRef = storageReference.child("profile_pictures/" + uid + ".jpg");

        profilePicRef.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> {

                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }
    // Method to load and display profile picture from Firebase Storage
    private void loadProfilePicture() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        StorageReference profilePicRef = storageReference.child("profile_pictures/" + uid + ".jpg");

        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load and display the profile picture using Picasso or any other image loading library
            Picasso.get().load(uri).into(profileImg);
        }).addOnFailureListener(e -> {
            // Handle the error
        });
    }
    // Handle the result of choosing a profile picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            uploadProfilePicture(filePath);
        }
    }
}


