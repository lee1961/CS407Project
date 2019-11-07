package com.example.ezclassapp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.ezclassapp.Models.Review;
import com.example.ezclassapp.Models.User;
import com.example.ezclassapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubmitReview extends AppCompatActivity {

    public static final String ARG_PARAM1 = "courseName";
    public static final String ARG_PARAM2 = "reviewListId";
    private Toolbar mToolbar;
    private Button mSubmit_btn;
    private EditText mReviewText;
    private EditText mOpinionText;
    private RatingBar mDiffcultyRating;
    private RatingBar mUsefulRating;
    private CheckBox mPostAnon;
    private DatabaseReference mDatabase;
    private DatabaseReference reviewReference;
    private DatabaseReference particularCourseReference;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_review);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mReviewText = (EditText) findViewById(R.id.review_input);
        mOpinionText = (EditText) findViewById(R.id.opinion_input);
        mDiffcultyRating = (RatingBar) findViewById(R.id.difficulty_bar);
        mUsefulRating = (RatingBar) findViewById(R.id.useful_bar);
        mSubmit_btn = (Button) findViewById(R.id.submit_btn);
        mPostAnon = (CheckBox) findViewById(R.id.submit_review_post_anonymous);
        Bundle currentBundle = getIntent().getExtras();
        final String courseid = currentBundle.getString(ARG_PARAM1);
        final ArrayList<String> reviewListId = currentBundle.getStringArrayList(ARG_PARAM2);
        reviewReference = mDatabase.child(Constants.REVIEW);
        particularCourseReference = mDatabase.child(Constants.COURSE).child(courseid).child(Constants.REVIEWLIST);

        // Get user data from SharedPreferences
        final SharedPreferences user = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        final String username = user.getString(Constants.USER_NAME, null);

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USER).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name");
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String name = dataSnapshot.getValue().toString();
                    userName = name;
                } else {
                    userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // listen at the review --> ForeignKey
                final DatabaseReference foreignKeyReference = mDatabase.child(Constants.REVIEW).child(courseid);
                foreignKeyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (checkValidInput()){
                            DatabaseReference reviewReference = foreignKeyReference;
                            final String key = reviewReference.push().getKey();
                            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Review review = new Review(
                                    key,
                                    userName,
                                    courseid,
                                    user_id,
                                    mOpinionText.getText().toString(),
                                    mReviewText.getText().toString(),
                                    (int) mDiffcultyRating.getRating(),
                                    (int) mUsefulRating.getRating(),
                                    mPostAnon.isChecked()
                            );
                            reviewReference.child(key).setValue(review);
                            // Add number of posts the user has in his profile
                            if (!mPostAnon.isChecked()) {
                                setUserPostCount();
                            }
                            finish();
                        } else {
                            String message = "Please fill in the required fields";
                            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        finish();
                    }
                });

            }
        });

    }

    // Method to increment user post count
    private void setUserPostCount() {
        // Obtain userUID from user preferences
        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        final String userUID = preferences.getString(Constants.USER_UID, null);
        // Check to make sure that userUID is not empty
        if (userUID != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userRef= reference.child(Constants.USER).child(userUID);
            userRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    User user = mutableData.getValue(User.class);
                    // Check just in case mutableData returned null
                    if (user == null) {
                        return Transaction.success(mutableData);
                    }
                    // Get post count, increment it, and set value
                    int postCount = user.getPostCount();
                    user.setPostCount(++postCount);
                    mutableData.setValue(user);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });
        }
    }

    // Check to make sure that all required fields are filled
    private boolean checkValidInput() {
        String opinionText = mOpinionText.getText().toString();
        String tipsText = mReviewText.getText().toString();
        return !TextUtils.isEmpty(opinionText) && !TextUtils.isEmpty(tipsText);
    }

}
