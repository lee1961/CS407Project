package com.example.ezclassapp.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezclassapp.Activities.Constants;
import com.example.ezclassapp.Activities.DetailedReviewActivity;
import com.example.ezclassapp.Activities.SubmitReview;
import com.example.ezclassapp.Models.Review;
import com.example.ezclassapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "sortByDateParameter";
    private static DatabaseReference reviewReference;
    private static ReviewListFragment mFragment;
    RecyclerView ReviewRecyclerView;
    FirebaseRecyclerAdapter<Review, ReviewViewHolder> mReviewViewHolderFirebaseRecyclerAdapter;
    ArrayList<String> listitems = new ArrayList<>();
    FloatingActionButton mFloatingActionButton;
    Query mQueryReference;
    LinearLayoutManager MyLayoutManager;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> mParam3;
    private String mCourseId;
    private ArrayList<String> reviewListId;
    private boolean isSortByDate;
    private boolean isSortByLikes;
    private CoordinatorLayout coordinatorLayout;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReviewListFragment.
     * @param1 fullCourseName .
     * @param2 courseID
     * @param3 ReviewListCourseID
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewListFragment newInstance(String fullCourseName, String courseID, List<String> reviewListId, boolean isSortByDate) {
        final Bundle args = new Bundle();
        args.putString(ARG_PARAM1, fullCourseName);
        args.putString(ARG_PARAM2, courseID);
        args.putBoolean(ARG_PARAM4, isSortByDate);
        if (reviewListId == null) {
            args.putStringArrayList(ARG_PARAM3, new ArrayList<String>());
        } else {
            args.putStringArrayList(ARG_PARAM3, new ArrayList<String>(reviewListId));
        }
        ReviewListFragment fragment = new ReviewListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // animation for upvoting the review
    private static void updateUpvoteButton(final ReviewViewHolder holder, final String reviewID, final Map<String, Boolean> map, final String userID) {

        int duration = 300;
        AnimatorSet animatorSet = new AnimatorSet();
        holder.mUpVoteImageView.setTag(R.drawable.like);

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.mUpVoteImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(duration);
        rotationAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.mUpVoteImageView, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(duration);
        bounceAnimX.setInterpolator(new OvershootInterpolator(4f));

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.mUpVoteImageView, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(duration);
        bounceAnimY.setInterpolator(new OvershootInterpolator(4f));
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.mUpVoteImageView.setImageResource(R.drawable.like);
            }
        });

        animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int count = Integer.parseInt(holder.mUpVoteTextViewCounter.getText().toString());
                count++;
                DatabaseReference upVoteReference = reviewReference.child(reviewID);
                upVoteReference.child(Constants.UPVOTE).setValue(count);
                map.put(userID, true);
                DatabaseReference mapReference = reviewReference.child(reviewID).child(Constants.MAPCHECK);
                mapReference.setValue(map);
                HashMap<String, String> notificationData = new HashMap<>();
                notificationData.put("from",userID);
                notificationData.put("type","request");
                //Toast.makeText(getFragment(), "", Toast.LENGTH_SHORT).show();
                DatabaseReference mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
                mNotificationDatabase.child(userID).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("databasee","I have to be here");
                    }
                });
                Log.d("checking","Have I reached here?");

            }
        });

        animatorSet.start();
    }

    // animation for downvoting the review
    private static void updateDownvoteButton(final ReviewViewHolder holder, final String reviewID, final Map<String, Boolean> map, final String userID) {

        int duration = 500;
        AnimatorSet animatorSet = new AnimatorSet();
        // set this as already liked so that user cant click again
        holder.mDownVoteImageView.setTag(R.drawable.dislike);

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.mDownVoteImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(duration);
        rotationAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.mDownVoteImageView, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(duration);
        bounceAnimX.setInterpolator(new OvershootInterpolator(4f));

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.mDownVoteImageView, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(duration);
        bounceAnimY.setInterpolator(new OvershootInterpolator(4f));
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.mDownVoteImageView.setImageResource(R.drawable.dislike);
            }
        });

        animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int count = Integer.parseInt(holder.mDownVoteTextViewCounter.getText().toString());
                count++;
                DatabaseReference downVoteReference = reviewReference.child(reviewID);
                downVoteReference.child(Constants.DOWNVOTE).setValue(count);
                map.put(userID, false);
                DatabaseReference mapReference = reviewReference.child(reviewID).child(Constants.MAPCHECK);
                mapReference.setValue(map);
            }
        });

        animatorSet.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d("working","Have I reached onCreate?");
        mFragment = this;
        setHasOptionsMenu(true);
    }

    /*
        how the filter works
     */
    public void changeFilter(boolean sortByDate) {
        Log.d("sorting", " sorting value changed to " + sortByDate);
        ReviewRecyclerView.setHasFixedSize(true);
        MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        MyLayoutManager.setReverseLayout(true);
        MyLayoutManager.setStackFromEnd(true);
        if (!sortByDate) {
            //if u want it sort by number of likes
            mQueryReference = reviewReference.orderByChild("upvote");
        } else {
            mQueryReference = reviewReference.orderByKey();
        }
        ReviewRecyclerView.setLayoutManager(MyLayoutManager);
        ReviewRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);

                if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    mFloatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    mFloatingActionButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        attachRecyclerViewAdapter();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recyclerview_review, container, false);
        if (getArguments() != null) {
            final Bundle args = getArguments();
            mCourseId = args.getString(ARG_PARAM2);
            isSortByDate = args.getBoolean(ARG_PARAM4);
        }
        reviewReference = FirebaseDatabase.getInstance().getReference().child(Constants.REVIEW).child(mCourseId);
        ReviewRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_review);
        changeFilter(isSortByDate);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_actionBtn);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SubmitReviewIntent = new Intent(getActivity(), SubmitReview.class);
                SubmitReviewIntent.putExtra(SubmitReview.ARG_PARAM1, mCourseId);
                SubmitReviewIntent.putExtra(SubmitReview.ARG_PARAM2, reviewListId);
                startActivity(SubmitReviewIntent);
            }
        });
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.LEFT) {    //if swipe left
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); //alert for confirm to delete

                    builder.setMessage("Are you sure you want to delete?");    //set message
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //do whatever you want the back key to do
                            Log.d("cancel dialog", "cancel dialog");
                            mReviewViewHolderFirebaseRecyclerAdapter.notifyDataSetChanged();
                        }
                    });

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /*
                                TODO check if admin then can only delete u know!
                             */
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            Review toBeDeletedReview = mReviewViewHolderFirebaseRecyclerAdapter.getItem(viewHolder.getAdapterPosition());
                            Log.d("remove", "removing review by " + toBeDeletedReview.getReviewerName() + " and the reviewID is " + toBeDeletedReview.getID());
                            databaseReference.child(Constants.REVIEW).child(toBeDeletedReview.getForeignID_classID()).child(toBeDeletedReview.getID()).removeValue();
                            mReviewViewHolderFirebaseRecyclerAdapter.notifyDataSetChanged();
                            return;
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("cancel", "cancel deleting");
                            mReviewViewHolderFirebaseRecyclerAdapter.notifyDataSetChanged();
                            //ReviewRecyclerView.scrollToPosition(viewHolder.getAdapterPosition());
                            return;
                        }
                    }).show();  //show alert dialog
                }
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(ReviewRecyclerView);

        startIntroAnimation();

        /*
            this is just to make sure the floating action button is diplayed properly
        */
        view.post(new Runnable() {
            @Override
            public void run() {
                view.requestLayout();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void attachRecyclerViewAdapter() {
        mReviewViewHolderFirebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Review, ReviewViewHolder>(
                        Review.class,
                        R.layout.cardview_review,
                        ReviewViewHolder.class,
                        mQueryReference
                ) {
                    @Override
                    protected void populateViewHolder(final ReviewViewHolder viewHolder, Review review, int position) {
                        final DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference().child(Constants.REVIEW).child(mCourseId);
                        if (review.getOpinion().length() > 60) {
                            viewHolder.mReviewtitleTextView.setText(review.getOpinion().toString().substring(0, 60) + "....");
                        } else {
                            viewHolder.mReviewtitleTextView.setText(review.getOpinion());
                        }

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (viewHolder.getReviewUID() != null) {
                                        Log.d("review_list", viewHolder.getReviewUID());
                                    } else {
                                        Log.d("review_list", "reviewUID is null");
                                    }
                                    //Get location on screen for tapped view
                                    int[] startingLocation = new int[2];
                                    v.getLocationOnScreen(startingLocation);
                                    Intent detailedReview = DetailedReviewActivity.newInstance(mFragment, viewHolder.getReviewClassUID(), viewHolder.getReviewUID(), startingLocation[1]);
                                    mFragment.getActivity().startActivity(detailedReview);
                                    getActivity().overridePendingTransition(0, 0);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        if (review.isPostAnon()) {
                            String anonymous = "Anonymous";
                            viewHolder.mReviewerName.setText(anonymous);
                        } else {
                            viewHolder.mReviewerName.setText(review.getReviewerName());
                        }

                        viewHolder.mUpVoteTextViewCounter.setText(String.valueOf(review.getUpvote()));
                        viewHolder.mUpVoteImageView.setTag(R.drawable.neutral_like);
                        viewHolder.mUpVoteImageView.setImageResource(R.drawable.neutral_like);

                        viewHolder.setReviewUID(getRef(position).getKey());
                        viewHolder.setReviewClassUID(mCourseId);

                        viewHolder.mDownVoteTextViewCounter.setText(String.valueOf(review.getDownvote()));
                        viewHolder.mDownVoteImageView.setTag(R.drawable.neutral_dislike);
                        viewHolder.mDownVoteImageView.setImageResource(R.drawable.neutral_dislike);

                        final String reviewID = review.getID();
                        final Map<String, Boolean> map = review.getCheckUserVoted();
                        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if (map.containsKey(userID)) {
                            viewHolder.mUpVoteImageView.setTag(R.drawable.like);
                            viewHolder.mUpVoteImageView.setImageResource(R.drawable.like);
                            viewHolder.mDownVoteImageView.setTag(R.drawable.dislike);
                            viewHolder.mDownVoteImageView.setImageResource(R.drawable.dislike);
                        } else {
                            //REACHES HERE means he hasnt upvote the post yet
                            /* WHEN THE PERSON UPVOTE THE POST */
                            viewHolder.mUpVoteImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("tag", "upvoting it");
                                    viewHolder.mUpVoteImageView.setClickable(false);
                                    viewHolder.mDownVoteImageView.setClickable(false);
                                    updateUpvoteButton(viewHolder, reviewID, map, userID);

                                }
                            });
                            viewHolder.mDownVoteImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("tag", "downvoting it");
                                    viewHolder.mDownVoteImageView.setClickable(false);
                                    viewHolder.mUpVoteImageView.setClickable(false);
                                    updateDownvoteButton(viewHolder, reviewID, map, userID);
                                }
                            });
                            viewHolder.mIsAnimated = true;
                        }
                    }
                };


        ReviewRecyclerView.setAdapter(mReviewViewHolderFirebaseRecyclerAdapter);
        ReviewRecyclerView.getAdapter().notifyDataSetChanged();
        //mQueryReference.keepSynced(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //((MainActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        //((MainActivity)getActivity()).getSupportActionBar().show();
    }

    private void startIntroAnimation() {
        mFloatingActionButton.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        mFloatingActionButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(500)
                .start();

    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        public TextView mReviewtitleTextView;
        public TextView mReviewerName;
        public ImageView mUpVoteImageView;
        public ImageView mDownVoteImageView;
        public TextView mUpVoteTextViewCounter;
        public TextView mDownVoteTextViewCounter;
        public boolean mIsAnimated;
        private String reviewUID;
        private String reviewClassUID;

        public ReviewViewHolder(View v) {
            super(v);
            final ReviewViewHolder viewHolder = this;
            mUpVoteTextViewCounter = (TextView) v.findViewById(R.id.upVoteCounterTextView);
            mDownVoteTextViewCounter = (TextView) v.findViewById(R.id.downVoteCounterTextView);
            mReviewtitleTextView = (TextView) v.findViewById(R.id.opinion_textView);
            mReviewerName = (TextView) v.findViewById(R.id.reviewer_textView);
            mIsAnimated = false;


            mUpVoteImageView = (ImageView) v.findViewById(R.id.upVoteImageView);
            mDownVoteImageView = (ImageView) v.findViewById(R.id.downVoteImageView);
            // Initialize the strings to null
            reviewUID = null;
            reviewClassUID = null;
        }

        String getReviewUID() {
            return this.reviewUID;
        }

        void setReviewUID(String reviewUID) {
            this.reviewUID = reviewUID;
        }

        String getReviewClassUID() {
            return reviewClassUID;
        }

        void setReviewClassUID(String reviewClassUID) {
            this.reviewClassUID = reviewClassUID;
        }

    }

}
