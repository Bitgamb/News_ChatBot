package com.example.newschatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfilePage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    TextView titleName,titleUsername,profileName,profileEmail,profilePhone,profileMembership;
    ImageView profilePicButton,backButton,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

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

}