package com.example.ezclassapp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.ezclassapp.Models.Review;
import com.example.ezclassapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubmitReview extends AppCompatActivity {

    public static final String ARG_PARAM1 = "courseName";
    public static final String ARG_PARAM2 = "reviewListId";
    private Toolbar mToolbar;
    private Button mSubmit_btn;
    private TextInputLayout mReviewText;
    private DatabaseReference mDatabase;
    private DatabaseReference reviewReference;
    private DatabaseReference particularCourseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_review);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mReviewText = (TextInputLayout) findViewById(R.id.review_input);
        mSubmit_btn = (Button) findViewById(R.id.submit_btn);
        Bundle currentBundle = getIntent().getExtras();
        final String courseid = currentBundle.getString(ARG_PARAM1);
        final ArrayList<String> reviewListId = currentBundle.getStringArrayList(ARG_PARAM2);
        reviewReference = mDatabase.child(Constants.REVIEW);
        particularCourseReference = mDatabase.child(Constants.COURSE).child(courseid).child(Constants.REVIEWLIST);

        // Get user data from SharedPreferences
        final SharedPreferences user = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        final String username = user.getString(Constants.USER_NAME, null);

        mSubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // listen at the review --> ForeignKey
                final DatabaseReference foreignKeyReference = mDatabase.child(Constants.REVIEW).child(courseid);
                foreignKeyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DatabaseReference reviewReference = foreignKeyReference;
                        final String key = reviewReference.push().getKey();
                        // Get the userUID using SharedPreferences
                        final String userUID = user.getString(Constants.USER_UID, null);
                        // Create a Review class and add it to the Review table
                        Review review = new Review(key, username, courseid, mReviewText.getEditText().getText().toString(), userUID);
                        reviewReference.child(key).setValue(review);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        finish();
                    }
                });

            }
        });

    }

}
