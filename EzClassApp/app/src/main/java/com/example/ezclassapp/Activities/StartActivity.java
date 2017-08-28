package com.example.ezclassapp.Activities;

import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.example.ezclassapp.R;
import com.transitionseverywhere.Explode;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Recolor;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import static android.support.design.R.id.visible;

public class StartActivity extends AppCompatActivity {
    private Button mRegBtn;
    private Button mLoginBtn;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);




        mTextView = (TextView) findViewById(R.id.textView);
        mRegBtn = (Button)findViewById(R.id.start_reg_button);
        mLoginBtn = (Button)findViewById(R.id.start_login_button);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        animation.setDuration(300);
        //mRegBtn.startAnimation(animation);


        startIntroAnimation();
        startContentAnimation();

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);

            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent = new Intent(StartActivity.this,LoginActivity.class);
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(login_intent,options.toBundle());

            }
        });


    }
    private void startIntroAnimation() {
        mRegBtn.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        mLoginBtn.setTranslationY(-2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        mTextView.setTranslationX(5 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

    }
    private void startContentAnimation() {
        mRegBtn.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(500)
                .setDuration(400)
                .start();
        mLoginBtn.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(500)
                .setDuration(400)
                .start();

        mTextView.animate()
                .translationX(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(700)
                .setDuration(400)
                .start();

    }

    public abstract class VisibleToggleClickListener implements View.OnClickListener {

        private boolean mVisible;

        @Override
        public void onClick(View v) {
            mVisible = !mVisible;
            changeVisibility(mVisible);
        }

        protected abstract void changeVisibility(boolean visible);

    }
}
