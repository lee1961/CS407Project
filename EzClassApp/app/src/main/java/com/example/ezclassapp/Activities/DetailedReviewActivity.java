package com.example.ezclassapp.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
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
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ezclassapp.Adapters.DetailedCommentsAdapter;
import com.example.ezclassapp.Fragments.CreateCommentDialogFragment;
import com.example.ezclassapp.Fragments.ReviewListFragment;
import com.example.ezclassapp.Helpers.StringImageConverter;
import com.example.ezclassapp.Models.Comment;
import com.example.ezclassapp.Models.Review;
import com.example.ezclassapp.Models.User;
import com.example.ezclassapp.Models.Utils;
import com.example.ezclassapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

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
    // Static method to build and create a new activity to detailedReviewActivity
    public static Intent newInstance(Fragment fragment, String classUID, String reviewUID,int startingLocation) throws IllegalAccessException {
        if (fragment instanceof ReviewListFragment) {
            Log.d("newInstance", "detailed_review newInstance called()");
            // Create bundle to store data
            Bundle bundle = new Bundle();
            bundle.putString(CLASS_UID, classUID);
            bundle.putString(REVIEW_UID, reviewUID);
            bundle.putInt(ARG_DRAWING_START_LOCATION,startingLocation);
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

        // Get data from Bundle
        Bundle extras = getIntent().getExtras().getBundle(REVIEW_ACTIVITY);
        classUID = "";
        reviewUID = "";
        if (extras == null) {
            finish();
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
                User user = dataSnapshot.getValue(User.class);
                Log.d("detailed_user", user.toString());
                // Get the final data to be presented
                final String _userPic = user.getImage();
                final String _userName = review.getReviewerName();
                final String _opinion = review.getOpinion();
                final String _tip = review.getTips();
                final int _like_count = review.getUpvote();
                final int _dislike_count = review.getDownvote();
                // Get the views
                final CircleImageView userImage = (CircleImageView) findViewById(R.id.detailed_user_pic);
                final TextView username = (TextView) findViewById(R.id.detailed_username);
                final TextView opinion_label = (TextView) findViewById(R.id.detailed_opinion_label);
                final TextView opinion = (TextView) findViewById(R.id.detailed_opinion);
                final TextView tip_label = (TextView) findViewById(R.id.detailed_tips_label);
                final TextView tip = (TextView) findViewById(R.id.detailed_tips);
                final LinearLayout like_btn = (LinearLayout) findViewById(R.id.detailed_like);
                final TextView like_count = (TextView) findViewById(R.id.detailed_like_count);
                final LinearLayout dislike_btn = (LinearLayout) findViewById(R.id.detailed_dislike);
                final TextView dislike_count = (TextView) findViewById(R.id.detailed_dislike_count);
                final ImageView like_image_btn = (ImageView) findViewById(R.id.like_image);
                final ImageView dislike_image_btn = (ImageView) findViewById(R.id.dislike_image);
                // populate the views
                // If user email is null or "default" then show default avatar
                if (TextUtils.isEmpty(_userPic) || _userPic.equals("default")) {
                    Log.d("detailed_review", "image is empty or default");
                    userImage.setImageResource(R.drawable.default_avatar);
                } else {
                    Log.d("detailed_review", "user image: " + _userPic);
                    // User helper function to decode string into an image
                    StringImageConverter.getDimensions(userImage, new StringImageConverter.setDimensionsListener() {
                        @Override
                        public void onComplete(int height, int width) {
                            Bitmap bitmap = StringImageConverter.decodeBase64AndSetImage(_userPic, height, width);
                            userImage.setImageBitmap(bitmap);
                        }
                    });
                }
                // Set the text views
                setTextView(_userName, null,  username);
                final String empty = "N\\A";
                if(_opinion == null || _opinion.length() <= 0) {
                    setTextView(empty, opinion_label, opinion);
                } else {
                    setTextView(_opinion, opinion_label, opinion);
                }
                if(_tip == null || _tip.length() <= 0) {
                    setTextView(empty, tip_label, tip);
                } else {
                    setTextView(_tip, tip_label, tip);
                }
                setTextView(_like_count, like_count);
                setTextView(_dislike_count, dislike_count);
                // TODO:Set like_btn and dislike_btn onClickListener
                final Map<String, Boolean> map = review.getCheckUserVoted();
                final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(map.containsKey(userID)) {
                    like_image_btn.setTag(R.drawable.like);
                    like_image_btn.setImageResource(R.drawable.like);
                    dislike_image_btn.setTag(R.drawable.dislike);
                    dislike_image_btn.setImageResource(R.drawable.dislike);
                } else {
                    like_image_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("liking", "you are liking the b utton");
                            like_image_btn.setClickable(false);
                            dislike_image_btn.setClickable(false);
                            updateUpvoteButton(like_image_btn,dislike_image_btn,like_count,reviewUID,map,userID);
                        }
                    });
                    dislike_image_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("disliking", "you are disliking the b utton");
                            like_image_btn.setClickable(false);
                            dislike_image_btn.setClickable(false);
                            updateDownvoteButton(dislike_image_btn,like_image_btn,dislike_count,reviewUID,map,userID);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        mAdapter = new DetailedCommentsAdapter();
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
    private void updateUpvoteButton(final ImageView mUpVoteImageView,final ImageView mDownVoteImageView,final TextView mUpVoteTextViewCounter, final String reviewID, final Map<String, Boolean> map, final String userID) {


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
    private void updateDownvoteButton(final ImageView mDownVoteImageView,final ImageView mUpVoteImageView,final TextView mDownVoteTextCounter, final String reviewID, final Map<String, Boolean> map, final String userID) {

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

}
