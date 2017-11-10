package com.example.ezclassapp.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.ezclassapp.Adapters.DetailedCommentsAdapter;
import com.example.ezclassapp.Fragments.CreateCommentDialogFragment;
import com.example.ezclassapp.Fragments.ReviewListFragment;
import com.example.ezclassapp.Helpers.StringImageConverter;
import com.example.ezclassapp.Models.Comment;
import com.example.ezclassapp.Models.Heart;
import com.example.ezclassapp.Models.Review;
import com.example.ezclassapp.Models.User;
import com.example.ezclassapp.Models.Utils;
import com.example.ezclassapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ezclassapp.Activities.Constants.PREFS_NAME;
import static com.example.ezclassapp.Activities.Constants.USER_UID;

public class DetailedReviewActivity extends AppCompatActivity {
    private static final String CLASS_UID = "CLASS_UID";
    private static final String REVIEW_UID = "REVIEW_UID";
    private static final String REVIEW_ACTIVITY = "REVIEW_ACTIVITY";
    private static final String ARG_DRAWING_START_LOCATION = "STARTING_LOCATION";
    final int FIRST_POS = 0;
    private final String TAG = "CREATE_COMMENT";
    private DatabaseReference reference;
    private RecyclerView mRecyclerView;
    private DetailedCommentsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private ChildEventListener mChildEventListener;
    private int drawingStartLocation;
    private FloatingActionButton createComment;
    private String classUID;
    private String reviewUID;
    private String userUID;
    private Context mContext;

    private CircleImageView mUserImage;
    private TextView mUsername;
    private TextView mOpinion_label;
    private TextView mOpinion;
    private TextView mTip_label;
    private TextView mTip;
    private LinearLayout mLike_btn;
    private TextView mHeart_count;
    private LinearLayout mDislike_btn;
    private TextView mDisheart_count;
    private ToggleButton mHeart_btn;
    private ToggleButton mDisheart_btn;

    private ValueEventListener mUpvoteListener;
    private ValueEventListener mDownvoteListener;

    // Static method to build and create a new activity to detailedReviewActivity
    public static Intent newInstance(Fragment fragment, String classUID, String reviewUID, int startingLocation) throws IllegalAccessException {
        if (fragment instanceof ReviewListFragment) {
            Log.d("newInstance", "detailed_review newInstance called()");
            // Create bundle to store data
            Bundle bundle = new Bundle();
            bundle.putString(CLASS_UID, classUID);
            bundle.putString(REVIEW_UID, reviewUID);
            bundle.putInt(ARG_DRAWING_START_LOCATION, startingLocation);
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
        mContext = this;
        // Set up all the views
        mRecyclerView = (RecyclerView) findViewById(R.id.detailed_recycler);
        mLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDividerItemDecoration = new DividerItemDecoration(this, mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get userUID
        SharedPreferences preferences = getBaseContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userUID = preferences.getString(USER_UID, null);

        // Get data from Bundle
        Bundle extras = getIntent().getExtras().getBundle(REVIEW_ACTIVITY);
        classUID = "";
        reviewUID = "";
        if (extras == null) {
            finish();
            return;
        } else {
            classUID = extras.getString(CLASS_UID);
            reviewUID = extras.getString(REVIEW_UID);
            // Check for valid input
            if (!TextUtils.isEmpty(classUID) && !TextUtils.isEmpty(reviewUID)) {
                Log.d("detailed_review", "classUID: " + classUID + ", reviewUID: " + reviewUID);
                // Get firebase database reference and setup the review
                reference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference review = reference.child(Constants.REVIEW).child(classUID).child(reviewUID);
                setUpDetailedReview(review);
                // Initialize FAB
                initializeFABAction(reviewUID);
            } else {
                Log.d("detailed_review", "classUID and reviewUID is null");
            }
        }
        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            final View v = findViewById(R.id.detailParent_layout);
            v.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    v.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
    }

    private void startIntroAnimation() {
        final View v = findViewById(R.id.detailParent_layout);
        v.setScaleY(0.1f);
        v.setPivotY(drawingStartLocation);
        createComment.setTranslationY(100);

        v.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        //commentsAdapter.updateItems();
        createComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    protected void onStop() {
        // Remove listeners before activity stops
        if (mChildEventListener != null) {
            reference.removeEventListener(mChildEventListener);
        }
        if (mUpvoteListener != null) {
            reference.removeEventListener(mUpvoteListener);
        }
        if (mDownvoteListener != null) {
            reference.removeEventListener(mDownvoteListener);
        }
        super.onStop();
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
        createComment = (FloatingActionButton) findViewById(R.id.detailed_create_comment);
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
    private void setViews(final Review review) {
        // TODO: Set up the views for the review itself
        // Get the userImage first before setting the views
        Log.d("detailed_review", review.toString());
        Log.d("detailed_review", reference.toString());
        DatabaseReference user = reference.child(Constants.USER).child(review.getForeignID_userID());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                // Log.d("detailed_user", user.toString());
                // Get the final data to be presented
                final String _userPic = user.getImage();
                final String _userName = review.getReviewerName();
                final String _opinion = review.getOpinion();
                final String _tip = review.getTips();
                final int _like_count = review.getUpvote();
                final int _dislike_count = review.getDownvote();
                // Get the views
                mUserImage = (CircleImageView) findViewById(R.id.detailed_user_pic);
                mUsername = (TextView) findViewById(R.id.detailed_username);
                mOpinion_label = (TextView) findViewById(R.id.detailed_opinion_label);
                mOpinion = (TextView) findViewById(R.id.detailed_opinion);
                mTip_label = (TextView) findViewById(R.id.detailed_tips_label);
                mTip = (TextView) findViewById(R.id.detailed_tips);
                mHeart_count = (TextView) findViewById(R.id.detailed_heart_count);
                mDisheart_count = (TextView) findViewById(R.id.detailed_disheart_count);
                mHeart_btn = (ToggleButton) findViewById(R.id.detailed_heart);
                mDisheart_btn = (ToggleButton) findViewById(R.id.detailed_disheart);

                // Set view according to whether post is marked as anonymous or not
                if (review.isPostAnon()) {
                    Log.d("detailed_review", "user image: " + _userPic);
                    mUserImage.setImageResource(R.drawable.default_avatar);
                    setTextView("Anonymous", null, mUsername);
                } else {
                    // If user email is null or "default" then show default avatar
                    if (TextUtils.isEmpty(_userPic) || _userPic.equals("default")) {
                        Log.d("detailed_review", "image set as default image");
                        mUserImage.setImageResource(R.drawable.default_avatar);
                    } else {
                        Log.d("detailed_review", "user image: " + _userPic);
                        // User helper function to decode string into an image
                        StringImageConverter.getDimensions(mUserImage, new StringImageConverter.setDimensionsListener() {
                            @Override
                            public void onComplete(int height, int width) {
                                Bitmap bitmap = StringImageConverter.decodeBase64AndSetImage(_userPic, height, width);
                                mUserImage.setImageBitmap(bitmap);
                            }
                        });
                    }
                    setTextView(_userName, null, mUsername);
                    // Set reviewer icon and name to redirect to their profile page
                    mUserImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent user_profile = UserProfileActivity.newInstance(mContext, review.getForeignID_userID());
                                startActivity(user_profile);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mUsername.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent user_profile = UserProfileActivity.newInstance(mContext, review.getForeignID_userID());
                                startActivity(user_profile);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                // Set text views
                final String empty = "N\\A";
                if (_opinion == null || _opinion.length() <= 0) {
                    setTextView(empty, mOpinion_label, mOpinion);
                } else {
                    setTextView(_opinion, mOpinion_label, mOpinion);
                }
                if (_tip == null || _tip.length() <= 0) {
                    setTextView(empty, mTip_label, mTip);
                } else {
                    setTextView(_tip, mTip_label, mTip);
                }
                setTextView(_like_count, mHeart_count);
                setTextView(_dislike_count, mDisheart_count);

                // Set heart state for review
                setHeartState(review.getUserHeart());
                // Set listener for heart and dishearted icon
                final DatabaseReference reviewReference = reference.child(Constants.REVIEW).child(classUID).child(reviewUID);
                final DatabaseReference upvoteReference = reviewReference.child(Constants.UPVOTE);
                final DatabaseReference downvoteReference = reviewReference.child(Constants.DOWNVOTE);
                mUpvoteListener = upvoteReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Firebase implicitly converts int or longs to long
                        int upvote = ((Long) dataSnapshot.getValue()).intValue();
                        mHeart_count.setText(String.valueOf(upvote));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mDownvoteListener = downvoteReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Firebase implicitly converts int or longs to long
                        int downvote = ((Long) dataSnapshot.getValue()).intValue();
                        mDisheart_count.setText(String.valueOf(downvote));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Animation for the heart and dishearted button
                // ScaleAnimation anim = animateHeart();
                mHeart_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // mHeart_btn.startAnimation(anim);
                        int heart_count = Integer.parseInt(mHeart_count.getText().toString());
                        int disheart_count = Integer.parseInt(mDisheart_count.getText().toString());
                        int increment = 0;
                        if (mHeart_btn.isChecked() && !mDisheart_btn.isChecked()) {
                            heart_count++;
                            increment = 1;
                        } else if (mHeart_btn.isChecked() && mDisheart_btn.isChecked()) {
                            heart_count++;
                            disheart_count--;
                            increment = 2;
                        } else if (!mHeart_btn.isChecked() && !mDisheart_btn.isChecked()) {
                            heart_count--;
                            increment = -1;
                        }
                        mDisheart_btn.setChecked(false);
                        mHeart_count.setText(String.valueOf(heart_count));
                        mDisheart_count.setText(String.valueOf(disheart_count));
                        Log.d("detailed_review", "You pressed the heart button");
                        onHeartClick(reviewReference);
                        if (!review.isPostAnon()) {
                            setKarmaPoints(review, increment);
                        }
                    }
                });

                mDisheart_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  mDisheart_btn.startAnimation(anim);
                        int heart_count = Integer.parseInt(mHeart_count.getText().toString());
                        int disheart_count = Integer.parseInt(mDisheart_count.getText().toString());
                        int increment = 0;
                        if (!mHeart_btn.isChecked() && mDisheart_btn.isChecked()) {
                            disheart_count++;
                            increment = -1;
                        } else if (mHeart_btn.isChecked() && mDisheart_btn.isChecked()) {
                            heart_count--;
                            disheart_count++;
                            increment = -2;
                        } else if (!mHeart_btn.isChecked() && !mDisheart_btn.isChecked()) {
                            disheart_count--;
                            increment = 1;
                        }
                        mHeart_btn.setChecked(false);
                        mHeart_count.setText(String.valueOf(heart_count));
                        mDisheart_count.setText(String.valueOf(disheart_count));
                        Log.d("detailed_review", "You pressed the disheart button");
                        onDisheartedClick(reviewReference);
                        if (!review.isPostAnon()) {
                            setKarmaPoints(review, increment);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ScaleAnimation animateHeart() {
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(10);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mHeart_btn.setEnabled(false);
                mDisheart_btn.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHeart_btn.setEnabled(true);
                mDisheart_btn.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return scaleAnimation;
    }

    // Checks and sets heart count
    private void onHeartClick(final DatabaseReference reviewReference) {
        Log.d("detailed_review", "Entered onHeartClick");
        reviewReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Review review = mutableData.getValue(Review.class);
                if (review == null) {
                    return Transaction.success(mutableData);
                }
                Heart userHeart;
                if (review.getUserHeart().containsKey(userUID)) {
                    userHeart = review.getUserHeart().get(userUID);
                } else {
                    userHeart = Heart.NONE;
                }
                // Initial state: HEARTED
                if (userHeart == Heart.HEARTED) {
                    // Get upvote and reduce it
                    int upvote = review.getUpvote();
                    review.setUpvote(--upvote);
                    // Get userHeart and change state to NONE
                    Map<String, Heart> heart = review.getUserHeart();
                    heart.put(userUID, Heart.NONE);
                    review.setUserHeart(heart);
                }
                // Initial state: DISHEARTED
                if (userHeart == Heart.DISHEARTED) {
                    // Get upvote and increment it
                    int upvote = review.getUpvote();
                    review.setUpvote(++upvote);
                    // Get downvote and decrement it
                    int downvote = review.getDownvote();
                    review.setDownvote(--downvote);
                    // Get userHeart and set it to HEARTED
                    Map<String, Heart> heart = review.getUserHeart();
                    heart.put(userUID, Heart.HEARTED);
                    review.setUserHeart(heart);
                }
                // Initial state: NONE
                if (userHeart == Heart.NONE) {
                    // Get upvote and increment it
                    int upvote = review.getUpvote();
                    review.setUpvote(++upvote);
                    // Get userHeart and set it to HEARTED
                    Map<String, Heart> heart = review.getUserHeart();
                    heart.put(userUID, Heart.HEARTED);
                    review.setUserHeart(heart);
                }
                Log.d("onHeartClickEND", review.toString());
                mutableData.setValue(review);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    // Checks and set dishearted count
    private void onDisheartedClick(DatabaseReference reviewReference) {
        reviewReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Review review = mutableData.getValue(Review.class);
                if (review == null) {
                    return Transaction.success(mutableData);
                }
                Heart userHeart;
                if (review.getUserHeart().containsKey(userUID)) {
                    userHeart = review.getUserHeart().get(userUID);
                } else {
                    userHeart = Heart.NONE;
                }
                // Initial state: DISHEARTED
                if (userHeart == Heart.DISHEARTED) {
                    Log.d("onDisheartedClick", "First");
                    // Get downvotes and reduce it
                    int downvote = review.getDownvote();
                    review.setDownvote(--downvote);
                    // Get userHeart and change state to NONE
                    Map<String, Heart> heart = review.getUserHeart();
                    heart.put(userUID, Heart.NONE);
                    review.setUserHeart(heart);
                }
                // Initial state: HEARTED
                if (userHeart == Heart.HEARTED) {
                    Log.d("onDisheartedClick", "Second");
                    // Get downvote and increment it
                    int downvote = review.getDownvote();
                    review.setDownvote(++downvote);
                    // Get upvote and decrement it
                    int upvote = review.getUpvote();
                    review.setUpvote(--upvote);
                    // Get userHeart and set it to DISHEARTED
                    Map<String, Heart> heart = review.getUserHeart();
                    heart.put(userUID, Heart.DISHEARTED);
                    review.setUserHeart(heart);
                }
                // Initial state: NONE
                if (userHeart == Heart.NONE) {
                    Log.d("onDisheartedClick", "Third");
                    // Get downvote and increment it
                    int downvote = review.getDownvote();
                    review.setDownvote(++downvote);
                    // Get userHeart and set it to DISHEARTED
                    Map<String, Heart> heart = review.getUserHeart();
                    heart.put(userUID, Heart.DISHEARTED);
                    review.setUserHeart(heart);
                }
                Log.d("onDisheartedClickEND", review.toString());
                mutableData.setValue(review);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // setHeartState(dataSnapshot);
            }
        });
    }

    // Set karma points of user
    private void setKarmaPoints(Review review, final int increment) {
        reference.child(Constants.USER).child(review.getForeignID_userID()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User reviewer = mutableData.getValue(User.class);
                if (reviewer == null) {
                    return Transaction.success(mutableData);
                }
                int karma = reviewer.getKarmaPoints();
                reviewer.setKarmaPoints(karma + increment);
                mutableData.setValue(reviewer);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    // Set the current state of the heart and dishearted icon
    private void setHeartState(Map<String, Heart> heartState) {
        if (heartState.containsKey(userUID)) {
            Heart userHeart = heartState.get(userUID);
            if (userHeart == Heart.NONE) {
                mHeart_btn.setChecked(false);
                mDisheart_btn.setChecked(false);
            } else if (userHeart == Heart.HEARTED) {
                mHeart_btn.setChecked(true);
                mDisheart_btn.setChecked(false);
            } else if (userHeart == Heart.DISHEARTED) {
                mHeart_btn.setChecked(false);
                mDisheart_btn.setChecked(true);
            }
        }
    }

    // Set the current state of the heart and dishearted icon
    private void setHeartState(DataSnapshot dataSnapshot) {
        Review review = dataSnapshot.getValue(Review.class);
        Heart userHeart = review.getUserHeart().get(userUID);
        if (userHeart == Heart.NONE) {
            mHeart_btn.setChecked(false);
            mDisheart_btn.setChecked(false);
        } else if (userHeart == Heart.HEARTED) {
            mHeart_btn.setChecked(true);
            mDisheart_btn.setChecked(false);
        } else if (userHeart == Heart.DISHEARTED) {
            mHeart_btn.setChecked(false);
            mDisheart_btn.setChecked(true);
        }
    }

    // Set the detailed review text view, such as opinion, tips and username
    private void setTextView(String input, TextView label, TextView view) {
        if (TextUtils.isEmpty(input)) {
            if (label != null) {
                label.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
            Log.d("detailed_review", String.valueOf(view.getResources().getResourceName(view.getId())) + " is null.");
        } else {
            view.setText(input);
            Log.d("detailed_review", String.valueOf(view.getResources().getResourceName(view.getId())) + " is set");
        }
    }

    // Set the like button with the number of upvotes and downvotes
    private void setTextView(int input, TextView view) {
        view.setText(Integer.toString(input));
    }

    // Sets up the recyclerView for the comments section
    private void setupRecyclerView(String reviewUID) {
        // Create an empty adapter and set adapter to recyclerView, data is added once childAdded is called
        mAdapter = new DetailedCommentsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        // Contains a childEventListener if someone creates a comment, the user is immediately notified
        mChildEventListener = reference.child(Constants.COMMENT).child(reviewUID).addChildEventListener(new ChildEventListener() {
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


    @Override
    public void onBackPressed() {
        View v = findViewById(R.id.detailParent_layout);
        v.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        DetailedReviewActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }


    // animation for upvoting the review
    private void updateUpvoteButton(final ImageView mUpVoteImageView, final ImageView mDownVoteImageView, final TextView mUpVoteTextViewCounter, final String reviewID, final Map<String, Boolean> map, final String userID) {

        int duration = 300;
        AnimatorSet animatorSet = new AnimatorSet();
        mUpVoteImageView.setTag(R.drawable.like);
        mUpVoteImageView.setClickable(false);
        mDownVoteImageView.setTag(R.drawable.dislike);
        mDownVoteImageView.setClickable(false);

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(mUpVoteImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(duration);
        rotationAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(mUpVoteImageView, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(duration);
        bounceAnimX.setInterpolator(new OvershootInterpolator(4f));

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(mUpVoteImageView, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(duration);
        bounceAnimY.setInterpolator(new OvershootInterpolator(4f));
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mUpVoteImageView.setImageResource(R.drawable.like);
                mDownVoteImageView.setImageResource(R.drawable.dislike);
            }
        });

        animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                int count = Integer.parseInt(mUpVoteTextViewCounter.getText().toString());
                count++;
                mUpVoteTextViewCounter.setText(String.valueOf(count));
                DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference().child(Constants.REVIEW).child(classUID);
                DatabaseReference upVoteReference = reviewReference.child(reviewID);
                upVoteReference.child(Constants.UPVOTE).setValue(count);
                map.put(userID, true);
                DatabaseReference mapReference = reviewReference.child(reviewID).child(Constants.MAPCHECK);
                mapReference.setValue(map);
            }
        });

        animatorSet.start();
    }


    // animation for downvoting the review
    private void updateDownvoteButton(final ImageView mDownVoteImageView, final ImageView mUpVoteImageView, final TextView mDownVoteTextCounter, final String reviewID, final Map<String, Boolean> map, final String userID) {

        int duration = 500;
        AnimatorSet animatorSet = new AnimatorSet();
        // set this as already liked so that user cant click again
        mUpVoteImageView.setTag(R.drawable.like);
        mUpVoteImageView.setClickable(false);
        mDownVoteImageView.setTag(R.drawable.dislike);
        mDownVoteImageView.setClickable(false);


        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(mDownVoteImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(duration);
        rotationAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(mDownVoteImageView, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(duration);
        bounceAnimX.setInterpolator(new OvershootInterpolator(4f));

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(mDownVoteImageView, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(duration);
        bounceAnimY.setInterpolator(new OvershootInterpolator(4f));
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mDownVoteImageView.setImageResource(R.drawable.dislike);
                mUpVoteImageView.setImageResource(R.drawable.like);
            }
        });

        animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int count = Integer.parseInt(mDownVoteTextCounter.getText().toString());
                count++;
                mDownVoteTextCounter.setText(String.valueOf(count));
                DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference().child(Constants.REVIEW).child(classUID);
                DatabaseReference downVoteReference = reviewReference.child(reviewID);
                downVoteReference.child(Constants.DOWNVOTE).setValue(count);
                map.put(userID, false);
                DatabaseReference mapReference = reviewReference.child(reviewID).child(Constants.MAPCHECK);
                mapReference.setValue(map);
            }
        });

        animatorSet.start();
    }

    private void updateAnimation(ToggleButton button) {
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // animation
                compoundButton.startAnimation(scaleAnimation);
            }
        });
    }
}
