package com.example.ezclassapp.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.ezclassapp.Adapters.DetailedCommentsAdapter;
import com.example.ezclassapp.Fragments.CreateCommentDialogFragment;
import com.example.ezclassapp.R;

import java.util.ArrayList;
import java.util.List;

public class DetailedReviewActivity extends AppCompatActivity implements CreateCommentDialogFragment.CreateCommentListener{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final String TAG="CREATECOMMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_review);
        mRecyclerView = (RecyclerView) findViewById(R.id.detailed_recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Dummy values
        List<String> name = new ArrayList<>();
        List<String> comment = new ArrayList<>();
        for (int i = 0, j = 100; i < 100; i++, j--) {
            name.add(Integer.toString(i));
            comment.add(Integer.toString(j));
        }
        Log.d("dummy", name.toString());
        Log.d("dummy", comment.toString());
        mAdapter = new DetailedCommentsAdapter(this, name, comment);
        mRecyclerView.setAdapter(mAdapter);

        // Initialize FAB
        initializeFABAction();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Initialize the FAB to create comments
    public void initializeFABAction() {
        FloatingActionButton createComment = (FloatingActionButton) findViewById(R.id.detailed_create_comment);
        createComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog box
                createAlertDialog();
            }
        });
    }

    // Creates the comment using a alertDialogBox by calling CreateCommentDialogFragment
    public void createAlertDialog() {
        DialogFragment fragment = new CreateCommentDialogFragment();
        fragment.show(getSupportFragmentManager(), TAG);
    }

    // Method implement from CreateCommentListener in CreateCommentDialogFragment
    @Override
    public void onDialogPositiveClick(DialogFragment fragment, String comment) {

    }
}
