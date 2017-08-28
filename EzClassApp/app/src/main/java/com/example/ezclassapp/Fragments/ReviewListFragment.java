package com.example.ezclassapp.Fragments;


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
import com.example.ezclassapp.R;

import java.util.ArrayList;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView ReviewRecyclerView;
    private MyAdapter mAdapter;
    ArrayList<String> listitems = new ArrayList<>();
    FloatingActionButton mFloatingActionButton;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ReviewListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewListFragment newInstance(String param1) {
        final Bundle args = new Bundle();
        args.putString(ARG_PARAM1,param1);
        ReviewListFragment fragment = new ReviewListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
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
            this.mAdapter.notifyDataSetChanged();
        }
        ReviewRecyclerView.setLayoutManager(MyLayoutManager);
        mFloatingActionButton = (FloatingActionButton)  view.findViewById(R.id.floating_actionBtn);

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

            holder.reviewtitleTextView.setText(list.get(position));
            /*
                TODO: currently gonna hardcode the image
             */
            holder.reviewImage.setImageResource(R.drawable.chichen_itza);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView reviewtitleTextView;
        public ImageView reviewImage;


        public MyViewHolder(View v) {
            super(v);
            reviewtitleTextView = (TextView) v.findViewById(R.id.review_title);
            reviewImage = (ImageView) v.findViewById(R.id.review_thumbnail);
            itemView.setOnClickListener(this);
        }

        /*
                TODO should launch to a more specific activity LOL
         */
        @Override
        public void onClick(View v) {

            Toast.makeText(getActivity(),"u click " + reviewtitleTextView.getText().toString(),Toast.LENGTH_SHORT).show();
            Log.d("manage to click","yes u are clicking for the title " +  reviewtitleTextView.getText().toString());

//            Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getId());
//            startActivity(intent);
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
