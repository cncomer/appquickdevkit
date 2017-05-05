package com.cncom.app.kit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bestjoy.library.scan.utils.DebugUtils;
import com.cncom.app.kit.utils.WebViewHelper;
import com.shwy.bestjoy.utils.DensityUtil;
import com.shwy.bestjoy.utils.TouchEventUtil;

/**
 * Created by bestjoy on 16/9/27.
 */

public class FixedWebView extends WebView {
    private static final String TAG = "FixedWebView";
    private double SCROLL_ANGLE_THRESHOLD = 1.0471975511965976D;
    private float mActionDownX;
    private float mActionDownY;
    private boolean mInterceptTouch = false;
    protected float currentScale = 1;
    private int documentHeight = 0;

    private class HeightGetter {
        @JavascriptInterface
        public void run(final float height) {
            documentHeight = DensityUtil.dip2px(getContext(), height);
            DebugUtils.logD(TAG, "HeightGetter documentHeight=" + documentHeight);
            FixedWebView.this.post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                }
            });
        }
    }

    public FixedWebView(Context context) {
        this(context, null);
    }

    public FixedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebView();
    }


    protected void initWebView() {
        if (isInEditMode()) {
            return;
        }
        WebViewHelper.initWebView(this);
        this.addJavascriptInterface(new HeightGetter(), "jo");
        setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                DebugUtils.logD(TAG, "getContentHeight()=" + getContentHeight());
                view.loadUrl("javascript:window.jo.run(document.documentElement.clientHeight);");//body.getBoundingClientRect().height
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                currentScale = newScale;
            }


        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (documentHeight == 0) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            DebugUtils.logD(TAG, "onMeasure() documentHeight = " + documentHeight);
            setMeasuredDimension(getMeasuredWidth(), documentHeight);
        }
    }

    public void checkDisallowInterceptTouchEventOrNot(boolean handled, MotionEvent motionEvent) {
        DebugUtils.logD(TAG, "getScrollY() " + getScrollY() + " currentScale=" + currentScale + ", handled="+handled);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.mActionDownX = motionEvent.getX();
                this.mActionDownY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int scrollY = getScrollY();

                if((scrollY > 0 && (this.mInterceptTouch && handled))
                        || motionEvent.getPointerCount() >= 2) {
                    this.getParent().requestDisallowInterceptTouchEvent(true);
                    return;
                }
                break;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean bool = super.onInterceptTouchEvent(motionEvent);
        checkDisallowInterceptTouchEventOrNot(bool, motionEvent);
        return bool;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.d(TAG, "onTouchEvent " + TouchEventUtil.getTouchAction(motionEvent.getAction()));
        boolean bool = super.onTouchEvent(motionEvent);
        checkDisallowInterceptTouchEventOrNot(bool, motionEvent);
        return bool;
    }

    public void setInterceptTouch(boolean interceptTouch) {
        this.mInterceptTouch = interceptTouch;
    }
}
