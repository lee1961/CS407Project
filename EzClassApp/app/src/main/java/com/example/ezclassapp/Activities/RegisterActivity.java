package com.example.ezclassapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ezclassapp.Models.User;
import com.example.ezclassapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;

    private Toolbar mToolbar;

    private ProgressDialog mRegProgress;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register);


        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create your Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.login_email);
        mPassword = (TextInputLayout) findViewById(R.id.login_password);
        mCreateBtn = (Button) findViewById(R.id.reg_Create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(display_name, email, password);
                }


            }
        });
    }

    private void register_user(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    //send email verification
                    current_user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Email sent", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to sent the email, double check your email address", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this, StartActivity.class));
                                finish();
                            }
                        }
                    });

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    /*HashMap<String,String> userMap = new HashMap<String, String>();
                    userMap.put("name",display_name);
                    userMap.put("major","Computer Science");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default"); */
                    User newUser = new User(display_name, "Computer Science", "default", "default", 0, 0, false);
                    mDatabase.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mRegProgress.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });
                } else {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "Cannot sign in. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Log.e("anim", "doing it");
        Fade fade = new Fade();
        fade.setDuration(1000000);
        getWindow().setEnterTransition(fade);
    }
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void setupWindowAnimations() {
//        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
//        getWindow().setExitTransition(slide);
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }
}

