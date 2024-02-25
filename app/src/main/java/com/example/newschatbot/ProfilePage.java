package com.example.newschatbot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfilePage extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    TextView titleName, titleUsername, profileName, profileEmail, profilePhone, profileMembership;
    ImageView profilePicButton, backButton, logout, profileImg;

    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_CROP_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profileImg = findViewById(R.id.profileImg);
        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);
        profileMembership = findViewById(R.id.profileMembership);
        //buttons
        profilePicButton = findViewById(R.id.profilePicButton);
        backButton = findViewById(R.id.backButton);
        logout = findViewById(R.id.logout);

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
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
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
                    this.profileMembership.setText("Premium");
                } else {
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

        Intent intent = new Intent(ProfilePage.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void uploadProfilePicture(Uri croppedUri) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        StorageReference profilePicRef = storageReference.child("profile_pictures/" + uid + ".jpg");

        profilePicRef.putFile(croppedUri)
                .addOnSuccessListener(taskSnapshot -> {
                    loadProfilePicture();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfilePage.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProfilePicture() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        StorageReference profilePicRef = storageReference.child("profile_pictures/" + uid + ".jpg");

        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(profileImg);
        }).addOnFailureListener(e -> {
            // Handle the error
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            startCropActivity(selectedImageUri);
        } else if (requestCode == REQUEST_CROP_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri croppedImageUri = data.getParcelableExtra("croppedImageUri");
            uploadProfilePicture(croppedImageUri);
        }
    }

    private void startCropActivity(Uri selectedImageUri) {
        Intent cropIntent = new Intent(this, CropImageActivity.class);
        cropIntent.putExtra("imageUri", selectedImageUri);
        startActivityForResult(cropIntent, REQUEST_CROP_IMAGE);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Chat.class);
        startActivity(intent);
        finish();

    }

    public static class CropImageActivity extends AppCompatActivity {

        private ImageView imageView;
        private Uri imageUri;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_crop_image);

            imageView = findViewById(R.id.cropImageView);

            imageUri = getIntent().getParcelableExtra("imageUri");

            if (imageUri != null) {
                imageView.setImageURI(imageUri);
            }

            findViewById(R.id.cropButton).setOnClickListener(v -> cropImage());
        }

        private void cropImage() {
            try {
                // Retrieve the original bitmap from the ImageView
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap originalBitmap = bitmapDrawable.getBitmap();

                // Calculate the desired 1:1 aspect ratio
                int targetWidth = Math.min(originalBitmap.getWidth(), originalBitmap.getHeight());
                int targetHeight = targetWidth;

                // Calculate the crop position to center the square
                int x = (originalBitmap.getWidth() - targetWidth) / 2;
                int y = (originalBitmap.getHeight() - targetHeight) / 2;

                // Create the cropped bitmap
                Bitmap croppedBitmap = Bitmap.createBitmap(originalBitmap, x, y, targetWidth, targetHeight);

                // Save the cropped bitmap to a temporary file
                File outputFile = new File(getCacheDir(), "cropped_image.jpg");
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Pass the cropped image URI back to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("croppedImageUri", Uri.fromFile(outputFile));
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions (e.g., if the image is not a valid bitmap)
                Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}