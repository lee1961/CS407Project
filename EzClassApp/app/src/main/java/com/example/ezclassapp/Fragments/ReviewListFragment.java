package com.example.ezclassapp.Fragments;


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
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezclassapp.Activities.MainActivity;
import com.example.ezclassapp.Activities.SubmitReview;
import com.example.ezclassapp.R;

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
    private MyAdapter mAdapter;
    ArrayList<String> listitems = new ArrayList<>();
    FloatingActionButton mFloatingActionButton;
    private String mCourseId;
    private ArrayList<String> reviewListId;


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
    public static ReviewListFragment newInstance(String fullCourseName,String courseID,ArrayList<String> reviewListId) {
        final Bundle args = new Bundle();
        args.putString(ARG_PARAM1,fullCourseName);
        args.putString(ARG_PARAM2,courseID);
        args.putStringArrayList(ARG_PARAM3,reviewListId);
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
        ReviewRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_review);
        ReviewRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        /*

            TODO: need to initialise firebase database here
            -- assuming it gets the classID/key here
            -- need to use firebase to retrieve all the reviews associated with the classID
         */


        if (ReviewRecyclerView != null) {
            //MyRecyclerView.setAdapter(new MyAdapter(listitems));
            //put the arrayList into the constructor of the adapter
            MyAdapter adapter = new MyAdapter((listitems));
            this.mAdapter = adapter;
            ReviewRecyclerView.setAdapter(this.mAdapter);
            this.mAdapter.notifyDataSetChanged();
        }
        if(getArguments() != null) {
            final Bundle args = getArguments();
            listitems.add(args.getString(ARG_PARAM1));
            mCourseId = args.getString(ARG_PARAM2);
            this.mAdapter.notifyDataSetChanged();
        }
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



    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<String> list;

        public MyAdapter(ArrayList<String> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            // each individual item in the card layout

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_review, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.mReviewtitleTextView.setText(mParam1);
            holder.mReviewerName.setText(mParam2);


        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mReviewtitleTextView;
        public TextView mReviewerName;


        public MyViewHolder(View v) {
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
            Toast.makeText(getActivity(),"u click " + mReviewtitleTextView.getText().toString(),Toast.LENGTH_SHORT).show();
        }
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
