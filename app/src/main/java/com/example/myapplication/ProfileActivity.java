package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    ImageView profileImageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        setListeners();

    }

    private void setListeners() {
        // Change Password
        Button saveChangesButton = findViewById(R.id.saveChangesButton);
        EditText etPassword = findViewById(R.id.etPassword);
         profileImageView = findViewById(R.id.profileImageView);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        saveChangesButton.setOnClickListener(v -> {
            String newPassword = String.valueOf(etPassword.getText()); // Replace with the new password
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(!newPassword.isEmpty()) {
                user.updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Password updated successfully
                                // You may want to prompt the user to reauthenticate after a password change.
                                // This depends on your security requirements.
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle password change failure
                                Toast.makeText(this, "Password change failed:"+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            if(selectedImageUri!=null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference profileImagesRef = storageRef.child("profile_images/" + userId); // Use the user's ID as the reference

// Assuming 'selectedImageUri' is the URI of the selected image
                UploadTask uploadTask = profileImagesRef.putFile(selectedImageUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    profileImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Save 'downloadUrl' to your database, associating it with the user's profile
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        usersRef.child(user.getUid()).child("profileImageURL").setValue(downloadUrl);

                        Toast.makeText(this, "Profile Image updated successfully", Toast.LENGTH_SHORT).show();
                    });

                }).addOnFailureListener(e -> {
                    // Handle the error
                });
            }
        });

        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.child("profileImageURL").getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Load and display the image using Glide (see next step)
                        Glide.with(getApplicationContext())
                                .load(imageUrl)
                                .into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // User has selected an image
            selectedImageUri = data.getData();

            try {
                // Load the selected image into the ImageView using Glide
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(profileImageView);

                // You can also save the imageUri to Firebase Storage or your database.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}