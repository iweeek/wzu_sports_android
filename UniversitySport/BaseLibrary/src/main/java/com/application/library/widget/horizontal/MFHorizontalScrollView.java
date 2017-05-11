package com.application.library.widget.horizontal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

/**
 * 横滑控件 不影响父控件的横滑
 * 
 * @author CCCMAX
 */
public class MFHorizontalScrollView extends HorizontalScrollView
{
    private boolean ondown = true;
    private float mLastMotionX;
    private float mLastMotionY;
    ViewGroup mParentSwipeView;

    public MFHorizontalScrollView(Context context)
    {
        super(context);
        setHorizontalScrollBarEnabled(false);
    }

    public MFHorizontalScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
    }

    public void setParentSwipeView(ViewGroup mParentSwipeView)
    {
        this.mParentSwipeView = mParentSwipeView;
    }

    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction())
        {
        case MotionEvent.ACTION_DOWN :
            getParent().requestDisallowInterceptTouchEvent(true);
            Log.d("cccmax", "requestDisallowInterceptTouchEvent  true   ACTION_DOWN");
            ondown = true;
            mLastMotionX = x;
            mLastMotionY = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE :
            if (ondown)
            {
                int dx = (int) Math.abs(x - mLastMotionX);
                int dy = (int) Math.abs(y - mLastMotionY);
                if (dx > dy)
                {
                    if (getScrollX() <= 0 /* && x - mLastMotionX > 5 */&& !subclassNeedTouchEvent())
                    {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        Log.d("cccmax", "requestDisallowInterceptTouchEvent  false  ACTION_MOVE");
                    } else
                    {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        Log.d("cccmax", "requestDisallowInterceptTouchEvent  true   ACTION_MOVE");
                    }
                } else if (dy > dx)
                {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    Log.d("cccmax", "requestDisallowInterceptTouchEvent  true   ACTION_MOVE");
                }
            }
            break;
        case MotionEvent.ACTION_UP :
        case MotionEvent.ACTION_CANCEL :
            getParent().requestDisallowInterceptTouchEvent(false);
            Log.d("cccmax", "requestDisallowInterceptTouchEvent  true   ACTION_UP");
            break;
        }
        mLastMotionX = x;
        mLastMotionY = y;
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 子类是否需要TouchEvent
     * 
     * @return
     */
    protected boolean subclassNeedTouchEvent()
    {
        return false;
    }
}
