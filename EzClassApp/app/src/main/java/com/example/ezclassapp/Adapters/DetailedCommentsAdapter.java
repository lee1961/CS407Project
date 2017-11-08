package com.example.ezclassapp.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.ezclassapp.Activities.Constants;
import com.example.ezclassapp.Activities.DetailedReviewActivity;
import com.example.ezclassapp.Activities.UserProfileActivity;
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

import static com.example.ezclassapp.Activities.UserProfileActivity.newInstance;

/**
 * Created by tychan on 9/2/2017.
 */

public class DetailedCommentsAdapter extends RecyclerView.Adapter<DetailedCommentsAdapter.ViewHolder> {
    // TODO: Add user picture
    private Context mContext;
    private List<String> userUID;
    private List<String> comment;
    private List<String> commentUID;
    private int lastAnimatedPosition = -1;
    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public DetailedCommentsAdapter() {
        mContext = null;
        userUID = new ArrayList<>();
        comment = new ArrayList<>();
        commentUID = new ArrayList<>();
    }

    public DetailedCommentsAdapter(Context context) {
        this.mContext = context;
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
        //runEnterAnimation(holder.itemView, position);
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
    private void setView(final ViewHolder holder, final User user, final String comment) {
        // Set image using helper function
        if (TextUtils.isEmpty(user.getImage()) || user.getImage().equals("default")) {
            Log.d("Comments_Adapter", "Default Avatar Set");
            holder.mUserImage.setImageResource(R.drawable.default_avatar);
        } else {
            StringImageConverter.getDimensions(holder.mUserImage, new StringImageConverter.setDimensionsListener() {
                @Override
                public void onComplete(int height, int width) {
                    Bitmap bitmap = StringImageConverter.decodeBase64AndSetImage(user.getImage(), height, width);
                    holder.mUserImage.setImageBitmap(bitmap);
                    Log.d("Comments_Adapter", user.getImage());
                    Log.d("Comments_Adapter", "User image set");
                }
            });
        }

        // Attack listeners to both the picture and the username
        holder.mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent user_profile = UserProfileActivity.newInstance(mContext, user);
                    mContext.startActivity(user_profile);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent user_profile = UserProfileActivity.newInstance(mContext, user);
                    mContext.startActivity(user_profile);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

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

    // animations
    private void runEnterAnimation(View view, int position) {

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }
}
