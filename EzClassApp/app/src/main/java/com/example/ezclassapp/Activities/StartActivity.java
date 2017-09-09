package com.example.ezclassapp.Activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ezclassapp.R;
import com.transitionseverywhere.Recolor;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import static android.support.design.R.id.visible;

public class StartActivity extends AppCompatActivity {
    private Button mRegBtn;
    private Button mLoginBtn;
    private TextView mTextView;
    float mScreenHeight;
    float mScreenWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(transition);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenHeight = metrics.heightPixels;
        mScreenWidth = metrics.widthPixels;

        mTextView = (TextView) findViewById(R.id.textView);
        mRegBtn = (Button)findViewById(R.id.start_reg_button);
        mLoginBtn = (Button)findViewById(R.id.start_login_button);


        startIntroAnimation();
        startContentAnimation();

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

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
        ViewPropertyAnimator propertyAnimator = mTextView.animate();
        propertyAnimator.translationX(0).setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(700)
                .setDuration(400)
                .start();
        propertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("animation","end animation");
                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(mTextView, "textColor",
                        new ArgbEvaluator(),
                        ContextCompat.getColor(getApplicationContext(), R.color.colorAccent),
                        ContextCompat.getColor(getApplicationContext(), R.color.material_white));
                objectAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });




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
