package com.cncom.app.kit.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;


/**
 * Created by bestjoy on 17/1/12.
 */

public class SupportAnimatorUrlSingleTouchImageViewActivity extends SupportUrlSingleTouchImageViewActivity{

    private boolean supportShareElement = false;
    private Bundle bundle;
    int mOriginLeft;
    int mOriginTop;
    int mOriginHeight;
    int mOriginWidth;
    int mOriginCenterX;
    int mOriginCenterY;
    private float mTargetHeight;
    private float mTargetWidth;
    private float mScaleX;
    private float mScaleY;
    private float mTranslationX;
    private float mTranslationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        supportShareElement = bundle.getBoolean("support_share_element");
        if (supportShareElement) {
            if (Build.VERSION.SDK_INT >= 21) {
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
            touchImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onGlobalLayout() {
                    touchImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    mOriginLeft = bundle.getInt("e_x", 0);
                    mOriginTop = bundle.getInt("e_y", 0);
                    mOriginHeight = bundle.getInt("e_h", 0);
                    mOriginWidth = bundle.getInt("e_w", 0);
                    mOriginCenterX = mOriginLeft + mOriginWidth / 2;
                    mOriginCenterY = mOriginTop + mOriginHeight / 2;


                    int[] location = new int[2];
                    touchImageView.getLocationOnScreen(location);
                    mTargetHeight = (float) touchImageView.getHeight();
                    mTargetWidth = (float) touchImageView.getWidth();

                    mScaleX = (float) mOriginWidth / mTargetWidth;
                    mScaleY = mScaleX;//(float) mOriginHeight / mTargetHeight;

                    float targetCenterX = location[0] + mTargetWidth / 2;
                    float targetCenterY = location[1] + mTargetHeight / 2;

                    mTranslationX = mOriginCenterX - targetCenterX;
                    mTranslationY = mOriginCenterY - targetCenterY;

                    touchImageView.setTranslationX(mTranslationX);
                    touchImageView.setTranslationY(mTranslationY);

                    touchImageView.setScaleX(mScaleX);
                    touchImageView.setScaleY(mScaleY);
                    performEnterAnimation();
                }
            });
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void performExitAnimation() {
        if (!supportShareElement) {
            super.performExitAnimation();
            return;
        }
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, mTranslationX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                touchImageView.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, mTranslationY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                touchImageView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, mScaleY);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                touchImageView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, mScaleX);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                touchImageView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });

        scaleXAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void performEnterAnimation() {

        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(touchImageView.getX(), 0);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                touchImageView.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(touchImageView.getY(), 0);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                touchImageView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(mScaleY, 1);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                touchImageView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(mScaleX, 1);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                touchImageView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();

    }
}
