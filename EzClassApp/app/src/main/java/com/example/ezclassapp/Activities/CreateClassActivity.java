package com.example.ezclassapp.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ezclassapp.Models.Course;
import com.example.ezclassapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by tychan on 8/26/2017.
 */

public class CreateClassActivity extends AppCompatActivity {

    private final String TITLENAME = "Create Class";
    private DatabaseReference mDatabase;
    private EditText mCourseNumber;
    private EditText mCourseName;
    private Button mCreateClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        // Initialize variables
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCourseNumber = (EditText) findViewById(R.id.label_course_num);
        mCourseName = (EditText) findViewById(R.id.label_course_name);
        mCreateClass = (Button) findViewById(R.id.btn_create_class);

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_class);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(TITLENAME);

        createClass();
    }

    // Check that coursename and coursenumber is filled and create the class
    private void createClass() {
        mCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coursename = mCourseName.getText().toString().trim();
                String coursenum = mCourseNumber.getText().toString().trim();
                if (!TextUtils.isEmpty(coursename) && !TextUtils.isEmpty(coursenum)) {
                    DatabaseReference classReference = mDatabase.child(Constants.COURSE);
                    String key = classReference.push().getKey();
                    Course course = new Course(coursenum, coursename,key);
                    classReference.child(key).setValue(course);
                    Toast.makeText(CreateClassActivity.this, coursenum + " Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CreateClassActivity.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
