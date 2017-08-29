package com.example.ezclassapp.Fragments;

/**
 * Created by victorlee95 on 8/23/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.ezclassapp.Activities.Constants;
import com.example.ezclassapp.Models.Course;
import com.example.ezclassapp.Models.WonderModel;
import com.example.ezclassapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class ClassesCardFragment extends Fragment {
    private static final String ARG_PARAM1 = "query";

    ArrayList<WonderModel> permanentItems = new ArrayList<>();
    ArrayList<WonderModel> listitems = new ArrayList<>();

    RecyclerView MyRecyclerView;
    //private MyAdapter mAdapter;
    private static onCardSelected mListener;
    Query mQueryReference;
    public static int delay = 300;
    private String mQueryString;

    private DatabaseReference mCourseDatabaseRef;
    FirebaseRecyclerAdapter<Course, CourseViewHolder> mCourseCourseViewHolderFirebaseRecyclerAdapter;
    public static FragmentActivity currentActivity;


    public void onNewQuery(String queryText) {
        mQueryString = queryText;
        mQueryReference = mCourseDatabaseRef.orderByChild(Constants.COURSENAME).equalTo(queryText);
        if (mCourseCourseViewHolderFirebaseRecyclerAdapter != null) {
            /*
                when the person closes the search or when the textfield is blank, show all the classes
            */
            if (queryText.length() == 0) {
                mQueryReference = mCourseDatabaseRef;
            }
            mCourseCourseViewHolderFirebaseRecyclerAdapter.cleanup();
            attachRecyclerViewAdapter(); // reinitialize the recyclerviewAdatper
            //MyRecyclerView.setAdapter(mCourseCourseViewHolderFirebaseRecyclerAdapter);

        } else if (queryText.length() < 3 || mCourseCourseViewHolderFirebaseRecyclerAdapter == null) {
            mQueryReference = mCourseDatabaseRef;
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ReviewListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassesCardFragment newInstance(String param1) {
        final Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        ClassesCardFragment fragment = new ClassesCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onCardSelected) {
            mListener = (onCardSelected) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement onCardSelected.");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* TODO should listen to firebase data changed here*/
        //initializeList(); // initialise all this hardcoded list but should listen from firebase
        getActivity().setTitle("7 Wonders of the Modern World");
        currentActivity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mQueryString != null) {
            mQueryReference = mCourseDatabaseRef.orderByChild(Constants.COURSENAME).equalTo(mQueryString);
        } else {
            mQueryReference = mCourseDatabaseRef;
        }

        attachRecyclerViewAdapter(); //initialise the adapter
        // MyRecyclerView.setAdapter(mCourseCourseViewHolderFirebaseRecyclerAdapter);

    }

    private void attachRecyclerViewAdapter() {

        mCourseCourseViewHolderFirebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Course, CourseViewHolder>(
                        Course.class,
                        R.layout.cardview_class,
                        CourseViewHolder.class,
                        mQueryReference
                ) {
                    @Override
                    protected void populateViewHolder(final CourseViewHolder viewHolder, Course course, int position) {
                        viewHolder.setTitleTextView(course.getCourseName());
                        Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
                        //make sure it is more than lolippop
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            animation.setDuration(delay);
                            viewHolder.itemView.startAnimation(animation);
                        }
                    }
                };
        MyRecyclerView.setAdapter(mCourseCourseViewHolderFirebaseRecyclerAdapter);
        MyRecyclerView.getAdapter().notifyDataSetChanged();
        MyRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCourseDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.COURSE);


        View view = inflater.inflate(R.layout.fragment_recyclerview_class, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleTextView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;
        View mView;

        /*
               TODO: send either the the classID or the list of reviewsID associated with it
        */
        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "u clicked " + titleTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            mListener.onCardSelected(titleTextView.getText().toString());
            Bundle args = new Bundle();
        }

        public CourseViewHolder(View v) {
            super(v);
            mView = v;
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
            shareImageView = (ImageView) v.findViewById(R.id.shareImageView);
            itemView.setOnClickListener(this);
            likeImageView.setTag(R.drawable.ic_like);
            likeImageView.setImageResource(R.drawable.ic_like);
            final CourseViewHolder viewHolder = this;
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int) likeImageView.getTag();
                    if (id == R.drawable.ic_like) {
                        Toast.makeText(v.getContext(), titleTextView.getText() + " added to favourites", Toast.LENGTH_SHORT).show();
                        updateHeartButton(viewHolder,false);
                    } else {
                        Log.d("already liked","already liked");
                        likeImageView.setTag(R.drawable.ic_like);
                        likeImageView.setImageResource(R.drawable.ic_like);
                        Toast.makeText(v.getContext(), titleTextView.getText() + " removed from favourites", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void setTitleTextView(String textView) {
            titleTextView = (TextView) mView.findViewById(R.id.titleTextView);
            titleTextView.setText(textView);
        }


    }
    /*
      IMPORTANT: this function/interface specifies what parameter must be passed in to go to the next Fragment(ReviewFragment)
    */
    public interface onCardSelected {
        void onCardSelected(String name);
    }

    private static void updateHeartButton(final CourseViewHolder holder, boolean animated) {


        AnimatorSet animatorSet = new AnimatorSet();
        // set this as already liked so that user cant click again
        //holder.likeImageView.setImageResource(R.drawable.ic_liked);
        holder.likeImageView.setTag(R.drawable.ic_liked);


        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.likeImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.likeImageView, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(new OvershootInterpolator(4f));

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.likeImageView, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
        bounceAnimY.setInterpolator(new OvershootInterpolator(4f));
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.likeImageView.setImageResource(R.drawable.ic_liked);
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
                        //        else {
                //            if (likedPositions.contains(holder.getPosition())) {
                //                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                //            } else {
                //                holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
                //            }
                //        }
    }

    private static void resetLikeAnimationState(CourseViewHolder holder) {
        holder.likeImageView.setVisibility(View.GONE);
    }


}

