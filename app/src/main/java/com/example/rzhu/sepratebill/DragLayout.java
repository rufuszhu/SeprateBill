package com.example.rzhu.sepratebill;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by rzhu on 4/22/2015.
 */
public class DragLayout extends RelativeLayout {
    private static final String TAG = "DragLayout";
    private static final int PEOPLE_VIEW_RIGHT_PADDING = 50;
    private final ViewDragHelper mDragHelper;
    private LinearLayout llPeople;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onFinishInflate() {
        llPeople = (LinearLayout) findViewById(R.id.ll_people);
        llPeople.post(new Runnable() {
            @Override
            public void run() {
                llPeople.setLeft(getWidth() - PEOPLE_VIEW_RIGHT_PADDING);
            }
        });

    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback(){

            @Override
            public boolean tryCaptureView(View view, int i) {
                return true;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                if(child == llPeople)
                    return llPeople.getWidth();
                else
                    return 0;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {

                final int leftBound = getWidth() - llPeople.getWidth();
                final int rightBound = getWidth() - PEOPLE_VIEW_RIGHT_PADDING;

                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

                return newLeft;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if(xvel>0.4f){
                    mDragHelper.smoothSlideViewTo(llPeople, getWidth() - PEOPLE_VIEW_RIGHT_PADDING, getTop());
                }
                else if(xvel<-0.4f){
                    mDragHelper.smoothSlideViewTo(llPeople, getWidth() - llPeople.getWidth(), getTop());
                }

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        if(action == MotionEvent.ACTION_MOVE){
            if(mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL)){

            }
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e(TAG, "calling mDragHelper.processTouchEvent(ev);");
        mDragHelper.processTouchEvent(ev);
        return true;
    }
}
