package com.example.ezclassapp.Fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezclassapp.Activities.Constants;
import com.example.ezclassapp.Models.Course;
import com.example.ezclassapp.Models.Review;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.example.ezclassapp.Activities.MainActivity;
import com.example.ezclassapp.Activities.SubmitReview;
import com.example.ezclassapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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
    DatabaseReference mQueryReference;

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

        /*

            TODO: need to initialise firebase database here
            -- assuming it gets the classID/key here
            -- need to use firebase to retrieve all the reviews associated with the classID
         */


        if(getArguments() != null) {
            final Bundle args = getArguments();
            mCourseId = args.getString(ARG_PARAM2);
        }
        mQueryReference = FirebaseDatabase.getInstance().getReference().child(Constants.REVIEW);

        ReviewRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_review);
        ReviewRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
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


        public ReviewViewHolder(View v) {
            super(v);
            mReviewtitleTextView = (TextView) v.findViewById(R.id.opinion_textView);
            mReviewerName = (TextView) v.findViewById(R.id.reviewer_textView);
            itemView.setOnClickListener(this);
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
                        Log.e("sda","the review name is " + review.getOpinion());
                        viewHolder.mReviewtitleTextView.setText(review.getOpinion());
                        viewHolder.mReviewerName.setText(review.getId());
                    }
                };

        ReviewRecyclerView.setAdapter(mReviewViewHolderFirebaseRecyclerAdapter);
        ReviewRecyclerView.getAdapter().notifyDataSetChanged();
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


}
