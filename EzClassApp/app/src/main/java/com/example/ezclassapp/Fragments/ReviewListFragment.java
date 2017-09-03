package com.example.ezclassapp.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezclassapp.Activities.Constants;
import com.example.ezclassapp.Models.Review;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.example.ezclassapp.Activities.MainActivity;
import com.example.ezclassapp.Activities.SubmitReview;
import com.example.ezclassapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> mParam3;
    RecyclerView ReviewRecyclerView;
    FirebaseRecyclerAdapter<Review, ReviewViewHolder> mReviewViewHolderFirebaseRecyclerAdapter;
    ArrayList<String> listitems = new ArrayList<>();
    FloatingActionButton mFloatingActionButton;
    private String mCourseId;
    private ArrayList<String> reviewListId;
    Query mQueryReference;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param1 fullCourseName .
     * @param2 courseID
     *  @param3 ReviewListCourseID
     * @return A new instance of fragment ReviewListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewListFragment newInstance(String fullCourseName,String courseID,List<String> reviewListId) {
        final Bundle args = new Bundle();
        args.putString(ARG_PARAM1,fullCourseName);
        args.putString(ARG_PARAM2,courseID);
        if(reviewListId == null) {
            args.putStringArrayList(ARG_PARAM3,new ArrayList<String>());
        } else {
            args.putStringArrayList(ARG_PARAM3,new ArrayList<String>(reviewListId));
        }
        ReviewListFragment fragment = new ReviewListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recyclerview_review, container, false);
        if(getArguments() != null) {
            final Bundle args = getArguments();
            mCourseId = args.getString(ARG_PARAM2);
        }
        DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference().child(Constants.REVIEW).child(mCourseId) ;
        // sort by the number of upvotes
        mQueryReference = reviewReference.orderByChild("upvote");
        // sort by the the date posted
        //mQueryReference = reviewReference.orderByKey();

        ReviewRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_review);
        ReviewRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //if u want it sort by order
        MyLayoutManager.setReverseLayout(true);
        MyLayoutManager.setStackFromEnd(true);
        ReviewRecyclerView.setLayoutManager(MyLayoutManager);

        mFloatingActionButton = (FloatingActionButton)  view.findViewById(R.id.floating_actionBtn);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SubmitReviewIntent = new Intent(getActivity(), SubmitReview.class);
                SubmitReviewIntent.putExtra(SubmitReview.ARG_PARAM1,mCourseId);
                SubmitReviewIntent.putExtra(SubmitReview.ARG_PARAM2,reviewListId);
                startActivity(SubmitReviewIntent);
            }
        });
        attachRecyclerViewAdapter();
        // when you are scrolling the recyclerView
        ReviewRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);

                if(scrollState == RecyclerView.SCROLL_STATE_IDLE) {
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




    public static class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mReviewtitleTextView;
        public TextView mReviewerName;
        public ImageView mUpVoteImageView;
        public ImageView mDownVoteImageView;
        public TextView mUpVoteTextViewCounter;
        public TextView mDownVoteTextViewCounter;

        public ReviewViewHolder(View v) {
            super(v);
            final ReviewViewHolder viewHolder = this;
            mUpVoteTextViewCounter = (TextView) v.findViewById(R.id.upVoteCounterTextView);
            mDownVoteTextViewCounter = (TextView) v.findViewById(R.id.downVoteCounterTextView);
            mReviewtitleTextView = (TextView) v.findViewById(R.id.opinion_textView);
            mReviewerName = (TextView) v.findViewById(R.id.reviewer_textView);

            itemView.setOnClickListener(this);

            mUpVoteImageView = (ImageView) v.findViewById(R.id.upVoteImageView);

            mDownVoteImageView = (ImageView) v.findViewById(R.id.downVoteImageView);
            mDownVoteImageView.setImageResource(R.drawable.neutral_dislike);
            mDownVoteImageView.setTag(R.drawable.neutral_dislike);
            mDownVoteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int) mDownVoteImageView.getTag();
                    Log.d("tag","downvoting it");
                    if(id == R.drawable.neutral_dislike) {
                        updateDownvoteButton(viewHolder);
                        Log.d("tag","DOWNVOTING it");

                    } else {

                    }
                }
            });

        }

        /*
                TODO should launch to a more specific activity LOL
         */
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(),"u click " + mReviewtitleTextView.getText().toString(),Toast.LENGTH_SHORT).show();
        }
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
                        viewHolder.mReviewtitleTextView.setText(review.getOpinion());
                        viewHolder.mReviewerName.setText(review.getReviewerName());
                        viewHolder.mUpVoteTextViewCounter.setText(String.valueOf(review.getUpvote()));
                        viewHolder.mDownVoteTextViewCounter.setText(String.valueOf(review.getDownvote()));


                        viewHolder.mUpVoteImageView.setTag(R.drawable.neutral_like);
                        viewHolder.mUpVoteImageView.setImageResource(R.drawable.neutral_like);
                        /* WHEN THE PERSON UPVOTE THE POST */
                        viewHolder.mUpVoteImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int id = (int) viewHolder.mUpVoteImageView.getTag();
                                Log.d("tag","upvoting it");
                                if(id == R.drawable.neutral_like) {
                                    updateUpvoteButton(viewHolder);
                                    Log.d("tag","upvoting it");
                                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    
                                } else {

                                }

                            }
                        });
                    }
                };
        ReviewRecyclerView.setAdapter(mReviewViewHolderFirebaseRecyclerAdapter);
        ReviewRecyclerView.getAdapter().notifyDataSetChanged();
        //mQueryReference.keepSynced(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity)getActivity()).getSupportActionBar().show();
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

    // animation for upvoting the review
    private static void updateUpvoteButton(final ReviewViewHolder holder) {


        AnimatorSet animatorSet = new AnimatorSet();
        holder.mUpVoteImageView.setTag(R.drawable.like);

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.mUpVoteImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.mUpVoteImageView, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(new OvershootInterpolator(4f));

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.mUpVoteImageView, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
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
            }
        });

        animatorSet.start();
    }


    // animation for downvoting the review
    private static void updateDownvoteButton(final ReviewViewHolder holder) {


        AnimatorSet animatorSet = new AnimatorSet();
        // set this as already liked so that user cant click again
        holder.mDownVoteImageView.setTag(R.drawable.dislike);

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.mDownVoteImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.mDownVoteImageView, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(new OvershootInterpolator(4f));

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.mDownVoteImageView, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
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
            }
        });

        animatorSet.start();
    }

}
