package com.example.ezclassapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezclassapp.Helpers.StringImageConverter;
import com.example.ezclassapp.Models.User;
import com.example.ezclassapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mClassDatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mMajor;
    private TextView mPostCount;
    private TextView mKarmaCount;
    private TextView mProfileName;
    private TextView mProfileEmail;
    private TextView mChangeMajor;
    private TextView mChangeProfilePic;
    private ProgressDialog mProgressDialog;

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        // Set up the views for the settings page
        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_user_name);
        mMajor = (TextView) findViewById(R.id.settings_user_major);
        mPostCount = (TextView) findViewById(R.id.settings_post_count);
        mKarmaCount = (TextView) findViewById(R.id.settings_karma_count);
        mProfileName = (TextView) findViewById(R.id.settings_show_name);
        mProfileEmail = (TextView) findViewById(R.id.settings_show_email);
        mChangeMajor = (TextView) findViewById(R.id.settings_change_major);
        mChangeProfilePic = (TextView) findViewById(R.id.settings_change_profile_pic);
        // Get the current user and the user database instance
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the user information from firebase
                String name = "";
                String thumb_image = "";
                String major = "";
                String image = "";

                User user = dataSnapshot.getValue(User.class);
                if (user.getName() != null) {
                    name = dataSnapshot.child("name").getValue().toString();
                }
                if (user.getMajor() != null) {
                    major = dataSnapshot.child("major").getValue().toString();
                }
                if (user.getImage() != null) {
                    image = dataSnapshot.child("image").getValue().toString();
                }

                // Set profile image
                if (image != null && !image.equals("default")) {
                    // Set the image for the user if found
                    setUserImage(image);
                } else {
                    // If image is not found, then user image is given color primaryDark
                    mDisplayImage.setImageResource(R.drawable.default_avatar);
                }
                // Set name and major
                mName.setText(name);
                if (major != null) {
                    mMajor.setText(major);
                }
                // Set post count and karma count
                mPostCount.setText(String.valueOf(user.getPostCount()));
                mKarmaCount.setText(String.valueOf(user.getKarmaPoints()));
                // Set user profile name
                mProfileName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Get user email from Firebase and display it
        mProfileEmail.setText(mCurrentUser.getEmail());
        mChangeMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);
                startActivity(status_intent);
            }
        });

        mChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        String title = "Settings";
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // User picked an image from the Gallery
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            // Sends an activity to get a cropped image
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        // User cropped a photo
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                // Gets the cropped picture uri
                Uri resultUri = result.getUri();
                // Gets the absolute path of the image stored
                File file = new File(resultUri.getPath());
                String filepath = file.getAbsolutePath();
                // Decode that picture into a base64 string
                final String convertedImage = StringImageConverter.getBase64String(filepath);

                // Save the image string into firebase
                mUserDatabase.child("image").setValue(convertedImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            setUserImage(convertedImage);
                            Toast.makeText(SettingsActivity.this, "Success Uploading", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    // Set up circleImageView by decoding Base64 String which contains the image
    private void setUserImage(final String image) {
        StringImageConverter.getDimensions(mDisplayImage, new StringImageConverter.setDimensionsListener() {
            @Override
            public void onComplete(int height, int width) {
                Bitmap bitmap = StringImageConverter.decodeBase64AndSetImage(image, height, width);
                mDisplayImage.setImageBitmap(bitmap);
                Log.d("settings_activity", "picture set");
            }
        });
    }
}
