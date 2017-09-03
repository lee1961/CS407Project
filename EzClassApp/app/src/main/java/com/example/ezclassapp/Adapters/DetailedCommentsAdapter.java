package com.example.ezclassapp.Adapters;

import android.content.Context;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ezclassapp.R;

import java.util.List;

/**
 * Created by tychan on 9/2/2017.
 */

public class DetailedCommentsAdapter extends RecyclerView.Adapter<DetailedCommentsAdapter.ViewHolder> {
    // TODO: Add user picture
    private Context mContext;
    private List<String> name;
    private List<String> comment;

    public DetailedCommentsAdapter(Context context, List <String> name, List<String> comment) {
        this.mContext = context;
        this.name = name;
        this.comment = comment;
    }

    public void add(int position, String name, String comment) {
        this.name.add(position, name);
        this.comment.add(position, comment);
    }

    public void remove(int position) {
        this.name.remove(position);
        this.comment.remove(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_detailed_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String _name = name.get(position);
        final String _comment = comment.get(position);
        holder.mUser.setText(_name);
        holder.mComment.setText(_comment);
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView mUser;
        private TextView mComment;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mUser = (TextView) mView.findViewById(R.id.comment_user);
            mComment = (TextView) mView.findViewById(R.id.comment_item);
        }
    }
}
