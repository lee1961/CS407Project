package com.example.ezclassapp.Fragments;

/**
 * Created by victorlee95 on 8/23/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.List;

import com.example.ezclassapp.Activities.Constants;
import com.example.ezclassapp.Models.Course;
import com.example.ezclassapp.Models.WonderModel;
import com.example.ezclassapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.R.attr.id;
import static com.example.ezclassapp.R.id.imageView;


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
    FirebaseRecyclerAdapter<Course, CourseViewHolder> mCourseViewHolderFirebaseRecyclerAdapter;
    public static FragmentActivity currentActivity;


    public void onNewQuery(String queryText) {
        mQueryString = queryText;
        mQueryReference = mCourseDatabaseRef.orderByChild(Constants.COURSENAME).equalTo(queryText);
        if (mCourseViewHolderFirebaseRecyclerAdapter != null) {
            /*
                when the person closes the search or when the textfield is blank, show all the classes
            */
            if (queryText.length() == 0) {
                mQueryReference = mCourseDatabaseRef;
            }
            mCourseViewHolderFirebaseRecyclerAdapter.cleanup();
            attachRecyclerViewAdapter(); // reinitialize the recyclerviewAdatper
            //MyRecyclerView.setAdapter(mCourseViewHolderFirebaseRecyclerAdapter);

        } else if (queryText.length() < 3 || mCourseViewHolderFirebaseRecyclerAdapter == null) {
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

        // MyRecyclerView.setAdapter(mCourseViewHolderFirebaseRecyclerAdapter);

    }
    private void attachRecyclerViewAdapter() {
        if(mQueryReference == null || mQueryString == null||mQueryString.length() <= 0) {
            mQueryReference = FirebaseDatabase.getInstance().getReference().child(Constants.COURSE);
        }
        mCourseViewHolderFirebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Course, CourseViewHolder>(
                        Course.class,
                        R.layout.cardview_class,
                        CourseViewHolder.class,
                        mQueryReference
                ) {
                    @Override
                    protected void populateViewHolder(final CourseViewHolder viewHolder, Course course, int position) {
                        viewHolder.setFullCourseNameTextView(course.getFullCourseName());
                        viewHolder.setViewHolderCourseId(course.getId());
                        Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
                        //make sure it is more than lolippop
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            animation.setDuration(delay);
                            viewHolder.itemView.startAnimation(animation);
                        }
                        if (course.getImageUrl() != null || course.getImageUrl().length() <= 0) {
                            Picasso.with(getActivity()).load(course.getImageUrl()).into(viewHolder.coverImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Log.d("classescardFramengent","load image failed retard!");
                                }
                            });
                        }




                    }
                };
        MyRecyclerView.setAdapter(mCourseViewHolderFirebaseRecyclerAdapter);
        MyRecyclerView.getAdapter().notifyDataSetChanged();
        MyRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCourseDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.COURSE);

        View view = inflater.inflate(R.layout.fragment_recyclerview_class, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.courseCardsView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        MyRecyclerView.setLayoutManager(MyLayoutManager);
        attachRecyclerViewAdapter();
        Log.d("debugging","start again");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public String courseId;
        public List<String> reviewListOfId;
        public TextView fullCourseNameTextView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;
        View mView;

        /*
               TODO: send either the the classID or the list of reviewsID associated with it
        */
        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "u clicked " + fullCourseNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            mListener.onCardSelected(fullCourseNameTextView.getText().toString(),courseId,reviewListOfId);
        }

        public CourseViewHolder(View v) {
            super(v);
            mView = v;
            final CourseViewHolder viewHolder = this;
            fullCourseNameTextView = (TextView) v.findViewById(R.id.titleTextView);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            itemView.setOnClickListener(this);
        }

        public void setFullCourseNameTextView(String textView) {
            fullCourseNameTextView = (TextView) mView.findViewById(R.id.titleTextView);
            fullCourseNameTextView.setText(textView);
        }
        public void setViewHolderCourseId(String courseId) {
            this.courseId = courseId;
        }

        public void setReviewListOfId(List<String> reviewListOfId) {
            this.reviewListOfId = reviewListOfId;
        }


    }
    /*
      IMPORTANT: this function/interface specifies what parameter must be passed in to go to the next Fragment(ReviewListFragment)
    */
    public interface onCardSelected {
        void onCardSelected(String name,String id,List<String> reviewListOfId);
    }
}

