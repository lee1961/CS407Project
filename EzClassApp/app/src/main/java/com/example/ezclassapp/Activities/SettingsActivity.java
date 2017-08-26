package com.example.ezclassapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezclassapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.ezclassapp.Models.Class;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private DatabaseReference mClassDatabase;
    private FirebaseUser mCurrentUser;

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mMajor;

    private Button mStatusBtn;
    private Button mImageBtn;
    private Button mAddClassBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = (CircleImageView)findViewById(R.id.settings_image);
        mName = (TextView)findViewById(R.id.settings_display_name);
        mMajor = (TextView)findViewById(R.id.settings_status);
        mStatusBtn = (Button)findViewById(R.id.settings_status_btn);
        mAddClassBtn = (Button)findViewById(R.id.add_class_btn);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(SettingsActivity.this, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                String name = dataSnapshot.child("name").getValue().toString();
                String major = dataSnapshot.child("major").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mMajor.setText(major);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent status_intent = new Intent(SettingsActivity.this,StatusActivity.class);
                startActivity(status_intent);
            }
        });

        mAddClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Class> classes = Class.getDummyClassList();
                mClassDatabase = FirebaseDatabase.getInstance().getReference().child("Class");
                for (Class currentClass: classes) {
                    String key = mClassDatabase.push().getKey();
                    mClassDatabase.child(key).setValue(currentClass);
                    Toast.makeText(SettingsActivity.this, key, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
