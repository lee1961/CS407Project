package com.example.ezclassapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.ezclassapp.Adapters.DetailedCommentsAdapter;
import com.example.ezclassapp.Fragments.CreateCommentDialogFragment;
import com.example.ezclassapp.Fragments.ReviewListFragment;
import com.example.ezclassapp.Models.Comment;
import com.example.ezclassapp.Models.Review;
import com.example.ezclassapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailedReviewActivity extends AppCompatActivity {
    private static final String REVIEW_UID = "REVIEW_UID";
    private static final String REVIEW_ACTIVITY = "REVIEW_ACTIVITY";
    final int FIRST_POS = 0;
    private final String TAG = "CREATE_COMMENT";
    private DatabaseReference reference;
    private RecyclerView mRecyclerView;
    private DetailedCommentsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    // Static method to build and create a new activity to detailedReviewActivity
    public static Intent newInstance(Fragment fragment, String reviewUID) throws IllegalAccessException {
        if (fragment instanceof ReviewListFragment) {
            // Create bundle to store data
            Bundle bundle = new Bundle();
            bundle.putString(REVIEW_UID, reviewUID);
            // Create and return intent
            Intent detailedReview = new Intent(fragment.getContext(), DetailedReviewActivity.class);
            detailedReview.putExtra(REVIEW_ACTIVITY, bundle);
            return detailedReview;
        } else {
            // throws error if a different fragment or activity tries to access this activity using
            // the builder provided
            throw new IllegalAccessException();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_review);
        // Set up all the views
        mRecyclerView = (RecyclerView) findViewById(R.id.detailed_recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data from Bundle
        Bundle extras = getIntent().getExtras();
        final String reviewUID;
        if (extras == null) {
            // TODO: Remove this comment and the reviewUID
            // finish();
            reviewUID = "Kt9Hi-Q8_u6lqsoAt_4";
        } else {
            reviewUID = extras.getString(REVIEW_UID);
        }
        // Get firebase database reference and setup the review
        reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference review = reference.child(Constants.REVIEW).child(reviewUID);
        setUpDetailedReview(review);

        // Initialize FAB
        initializeFABAction(reviewUID);
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

    // Initialize the FAB to create comments
    private void initializeFABAction(final String reviewUID) {
        FloatingActionButton createComment = (FloatingActionButton) findViewById(R.id.detailed_create_comment);
        createComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog box
                createAlertDialog(reviewUID);
            }
        });
    }

    // Creates the comment using a alertDialogBox by calling CreateCommentDialogFragment
    private void createAlertDialog(final String reviewUID) {
        // Create a bundle to store the reviewUID
        CreateCommentDialogFragment.newInstance(reviewUID).show(getSupportFragmentManager(), TAG);
    }

    // Sets up the comment section by getting a review object and calling setViews() and setupRecyclerView()
    private void setUpDetailedReview(DatabaseReference reviewReference) {
        reviewReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Review review = dataSnapshot.getValue(Review.class);
                setViews(review);
                setupRecyclerView(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Set up the views using data in the review object
    private void setViews(Review revilew) {
        // TODO: Set up the views for the review itself
    }

    // Sets up the recyclerView for the comments section
    private void setupRecyclerView(String reviewUID) {
        // Create an empty adapter and set adapter to recyclerView, data is added once childAdded is called
        mAdapter = new DetailedCommentsAdapter();
        mRecyclerView.setAdapter(mAdapter);
        // Contains a childEventListener if someone creates a comment, the user is immediately notified
        reference.child(Constants.COMMENT).child(reviewUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("detailed_review", "onChildAdded called");
                Comment newComment = dataSnapshot.getValue(Comment.class);
                mAdapter.add(FIRST_POS, newComment.getUserUID(), newComment.getComment(), newComment.getCommentUID());
                mRecyclerView.scrollToPosition(FIRST_POS);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Comment newComment = dataSnapshot.getValue(Comment.class);
                mAdapter.remove(newComment.getCommentUID());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
