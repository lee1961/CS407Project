package com.example.ezclassapp.Activities;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.example.ezclassapp.R;

public class SubmitReview extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mSubmit_btn;
    private TextInputLayout mReviewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_review);


    }
}
