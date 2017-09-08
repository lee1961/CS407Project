package com.example.ezclassapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ezclassapp.Activities.Constants;
import com.example.ezclassapp.Helpers.StringImageConverter;
import com.example.ezclassapp.Models.User;
import com.example.ezclassapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tychan on 9/2/2017.
 */

public class DetailedCommentsAdapter extends RecyclerView.Adapter<DetailedCommentsAdapter.ViewHolder> {
    // TODO: Add user picture
    private Context mContext;
    private List<String> userUID;
    private List<String> comment;
    private List<String> commentUID;

    public DetailedCommentsAdapter() {
        userUID = new ArrayList<>();
        comment = new ArrayList<>();
        commentUID = new ArrayList<>();
    }

    public DetailedCommentsAdapter(Context context, List<String> userUID, List<String> comment, List<String> commentUID) {
        this.mContext = context;
        this.userUID = userUID;
        this.comment = comment;
        this.commentUID = commentUID;
    }

    public void add(int position, String userUID, String comment, String commentUID) {
        this.userUID.add(position, userUID);
        this.comment.add(position, comment);
        this.commentUID.add(position, commentUID);
        notifyItemInserted(position);
    }

    public void remove(String commentUID) {
        int position = this.commentUID.indexOf(commentUID);
        this.userUID.remove(position);
        this.comment.remove(position);
        this.commentUID.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_detailed_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String _userUID = userUID.get(position);
        final String _comment = comment.get(position);
        Log.d("comments_adapter", "position: " + Integer.toString(position) + ", userUID: " + _userUID + ", comment: " + comment);
        getAndDisplayComment(holder, _userUID, _comment);
    }

    // Creates a comment and displays it
    private void getAndDisplayComment(final ViewHolder holder, final String userUID, final String comment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USER).child(userUID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                setView(holder, user, comment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Sets the views after
    private void setView(ViewHolder holder, User user, String comment) {
        // Set image using helper function
        StringImageConverter.decodeBase64AndSetImage(user.getImage(), holder.mUserImage);
        // Set name and comment
        holder.mUsername.setText(user.getName());
        holder.mComment.setText(comment);
    }

    @Override
    public int getItemCount() {
        return userUID.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private CircleImageView mUserImage;
        private TextView mUsername;
        private TextView mComment;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mUserImage = (CircleImageView) mView.findViewById(R.id.comment_user_image);
            mUsername = (TextView) mView.findViewById(R.id.comment_username);
            mComment = (TextView) mView.findViewById(R.id.comment_item);
        }
    }

}
