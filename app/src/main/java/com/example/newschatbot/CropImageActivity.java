package com.example.newschatbot; // Replace with your actual package name

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;

import java.io.File;

public class CropImageActivity extends AppCompatActivity {

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        // Retrieve the image URI from the intent
        imageUri = getIntent().getParcelableExtra("imageUri");

        // Start the cropping process immediately
        cropImage();
    }

    private void cropImage() {
        // Set the destination URI for the cropped image
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

        // Set the maximum result size
        int maxWidth = 800; // Replace with your desired maximum width
        int maxHeight = 800; // Replace with your desired maximum height

        // Start the UCrop activity
        UCrop.of(imageUri, destinationUri)
                .withAspectRatio(1, 1)  // Set your desired aspect ratio (e.g., 1:1)
                .withMaxResultSize(maxWidth, maxHeight)  // Set maximum result size
                .start(this);
    }

    @Override
    public void onBackPressed() {
        // Handle the back button press
        // Navigate back to ProfilePage.class
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from the UCrop activity
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            // Retrieve the cropped image URI from the result
            final Uri resultUri = UCrop.getOutput(data);

            // Pass the cropped image URI back to the calling activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("croppedImageUri", resultUri);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Handle the error, e.g., show a Toast
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
