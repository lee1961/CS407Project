package com.example.ezclassapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ezclassapp.R;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile);

        // Show back nav button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
