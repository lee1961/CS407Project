package com.example.ezclassapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ezclassapp.Helpers.StringImageConverter;
import com.example.ezclassapp.Models.User;
import com.example.ezclassapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private static final String USER_UID = "USER_UID";
    private static final String INTENT_EXTRA = "INTENT_EXTRA";
    private DatabaseReference userRef;

    // Static method to build and create a new activity to UserProfileActivity
    public static Intent newInstance(Context context, String userUID) throws IllegalAccessException {
        // Create a bundle to store data
        if (context instanceof DetailedReviewActivity) {
            Bundle bundle = new Bundle();
            bundle.putString(USER_UID, userUID);
            Intent userProfile = new Intent(context, UserProfileActivity.class);
            userProfile.putExtra(INTENT_EXTRA, bundle);
            return userProfile;
        } else {
            throw new IllegalAccessException();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile);

        // Get intent and bundle
        Bundle bundle = getIntent().getExtras().getBundle(INTENT_EXTRA);
        if (bundle == null) {
            Log.d("user_profile", "bundle is null");
            finish();
            return;
        }

        String userUID = bundle.getString(USER_UID);
        if (userUID == null) {
            Log.d("user_profile", "user is null");
            finish();
            return;
        }
        // Set up user reference
        userRef = FirebaseDatabase.getInstance().getReference().child(Constants.USER).child(userUID);
        // Get user information and display
        getUserProfile();
        // Show back nav button
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    // Gets user profile before populating view
    private void getUserProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    initializeViews(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // Set up all the views in the profile page
    private void initializeViews(User user) {
        String user_name = user.getName();
        final String user_img = user.getImage();
        String user_major = user.getMajor();
        int user_karma = user.getKarmaPoints();
        int user_post = user.getPostCount();

        final ImageView profile_img = (ImageView) findViewById(R.id.user_profile_image);
        TextView profile_name = (TextView) findViewById(R.id.user_profile_name);
        TextView profile_major = (TextView) findViewById(R.id.user_profile_major);
        TextView profile_post_count = (TextView) findViewById(R.id.user_profile_post_count);
        TextView profile_karma_count = (TextView) findViewById(R.id.user_profile_karma_count);

        // Safety check for user image
        if (TextUtils.isEmpty(user_img) || user_img.equals("default")) {
            Log.d("user_profile", "profile image is empty");
            profile_img.setImageResource(R.drawable.default_avatar);
        } else {
            StringImageConverter.getDimensions(profile_img, new StringImageConverter.setDimensionsListener() {
                @Override
                public void onComplete(int height, int width) {
                    Bitmap bitmap = StringImageConverter.decodeBase64AndSetImage(user_img, height, width);
                    profile_img.setImageBitmap(bitmap);
                }
            });
        }

        // Safety checks for user name and major
        final String emptyString = "N\\A";
        if (TextUtils.isEmpty(user_name)) {
            profile_name.setText(emptyString.trim());
        } else {
            profile_name.setText(user_name.trim());
        }

        if (TextUtils.isEmpty(user_major)) {
            profile_major.setText(emptyString.trim());
        } else {
            profile_major.setText(user_major.trim());
        }

        profile_karma_count.setText(String.valueOf(user_karma));
        profile_post_count.setText(String.valueOf(user_post));
    }
}
