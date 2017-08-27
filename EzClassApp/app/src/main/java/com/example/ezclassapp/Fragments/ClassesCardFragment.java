package com.example.ezclassapp.Fragments;

/**
 * Created by victorlee95 on 8/23/2017.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.ezclassapp.Activities.Constants;
import com.example.ezclassapp.Models.Course;
import com.example.ezclassapp.Models.WonderModel;
import com.example.ezclassapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ClassesCardFragment extends Fragment {
    private static final String ARG_PARAM1 = "query";

    ArrayList<WonderModel> permanentItems = new ArrayList<>();
    ArrayList<WonderModel> listitems = new ArrayList<>();

    RecyclerView MyRecyclerView;
    //private MyAdapter mAdapter;
    private onCardSelected mListener;

    private DatabaseReference mCourseDatabaseRef;

    public void clearItems() {
        listitems.clear();
       // mAdapter.notifyDataSetChanged();
    }
    public void onNewQuery(String text) {
        Query query = mCourseDatabaseRef.orderByChild(Constants.COURSENAME).equalTo(text);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do with your result
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); 
       /* listitems.clear();
        text = text.trim();
        for (WonderModel wonderModel : permanentItems) {
            if(wonderModel.getCardName().contains(text) || wonderModel.getCardName().toLowerCase().contains(text)) {
                listitems.add(wonderModel);
                Log.d("matched stuff","the matching wonder name is " + wonderModel.getCardName());
            }
        }
        mAdapter.notifyDataSetChanged();
    */
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
        args.putString(ARG_PARAM1,param1);
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
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Course,MyViewHolder> courseMyViewHolderFirebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Course, MyViewHolder>(
                        Course.class,
                        R.layout.cardview_class,
                        MyViewHolder.class,
                        mCourseDatabaseRef
                ) {
                    @Override
                    protected void populateViewHolder(MyViewHolder viewHolder, Course course, int position) {
                        viewHolder.setTitleTextView(course.getCourseName());
                    }
                };
        MyRecyclerView.setAdapter(courseMyViewHolderFirebaseRecyclerAdapter);

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

         /*
            TODO: need to initialise firebase database here
            -- need to use firebase to retrieve all the class according to the text input into the query
         */

//        if (listitems.size() > 0 & MyRecyclerView != null) {
//            //MyRecyclerView.setAdapter(new MyAdapter(listitems));
//            MyAdapter adapter = new MyAdapter((listitems));
//            this.mAdapter = adapter;
//            MyRecyclerView.setAdapter(this.mAdapter);
//            this.mAdapter.notifyDataSetChanged();
//        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        if(getArguments() != null) {
            onNewQuery(getArguments().getString(ARG_PARAM1));
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


//    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
//        private ArrayList<WonderModel> list;
//
//        public MyAdapter(ArrayList<WonderModel> Data) {
//            list = Data;
//        }
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            // create a new view
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_class, parent, false);
//            MyViewHolder holder = new MyViewHolder(view);
//            return holder;
//        }
//
//        @Override
//        public void onBindViewHolder(final MyViewHolder holder, int position) {
//
//            holder.titleTextView.setText(list.get(position).getCardName());
//            holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
//            holder.coverImageView.setTag(list.get(position).getImageResourceId());
//            holder.likeImageView.setTag(R.drawable.ic_like);
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return list.size();
//        }
//    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleTextView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;
        View mView;

        @Override
        public void onClick(View view) {

        }

        public MyViewHolder(View v) {
            super(v);
            mView = v;
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
            shareImageView = (ImageView) v.findViewById(R.id.shareImageView);

            itemView.setOnClickListener(this);
        }
        public void setTitleTextView(String textView) {
            titleTextView = (TextView) mView.findViewById(R.id.titleTextView);
            titleTextView.setText(textView);
        }

        /*
                TODO should launch to a more specific activity LOL
         */

    }

    public interface onCardSelected {
        void onCardSelected(String name);
    }


    /*
        TODO: to be removed, just for a dummy objects
     */
//    public void initializeList() {
//        listitems.clear();
//
//        for (int i = 0; i < 7; i++) {
//            WonderModel item = new WonderModel();
//            item.setCardName(Wonders[i]);
//            item.setImageResourceId(Images[i]);
//            item.setIsfav(0);
//            item.setIsturned(0);
//            listitems.add(item);
//            permanentItems.add(item);
//        }
//    }
}
