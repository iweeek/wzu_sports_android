package com.application.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.application.library.R;


public class SlidingIndicator extends LinearLayout
{
    private int mCount;
    private int mCurrSelectPositon;
    private LayoutInflater mLayoutInflater;
    private View[] indicators;
    // private final static LinearLayout.LayoutParams LAYOUT_PARAMS = new LinearLayout.LayoutParams(-2, -2);
    private Drawable mIndicatorDrawable = new ColorDrawable();
    private int margin = 0;

    public SlidingIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.indicator);
        final Drawable d = a.getDrawable(R.styleable.indicator_indicator_drawable);
        if (d != null)
        {
            mIndicatorDrawable = d;
        }
        margin = (int) a.getDimension(R.styleable.indicator_indicator_margin, 0);
        mLayoutInflater = LayoutInflater.from(context);
        a.recycle();
        setOrientation(LinearLayout.HORIZONTAL);

    }

    public synchronized void setCount(int count)
    {
        this.mCount = count;
        removeAllViews();
        int w = mIndicatorDrawable.getIntrinsicWidth();
        int h = mIndicatorDrawable.getIntrinsicHeight();
        indicators = new View[this.mCount];

        for (int i = 0; i < this.mCount; i++)
        {
            // indicators[i] = mLayoutInflater.inflate(R.layout.custom_gallery_indicator, null);
            indicators[i] = new View(getContext());
            indicators[i].setBackgroundDrawable(mIndicatorDrawable.getConstantState().newDrawable());
            LayoutParams layout_params = new LayoutParams(-2, -2);
            layout_params.width = w;
            layout_params.height = h;
            layout_params.setMargins(margin, 0, margin, 0);
            layout_params.gravity = Gravity.CENTER_VERTICAL;
            addView(indicators[i], layout_params);

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        View child = null;

        int w = mIndicatorDrawable.getIntrinsicWidth();
        int h = mIndicatorDrawable.getIntrinsicHeight();
        for (int i = 0; i < count; i++)
        {
            child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
        }
    }

    public synchronized void onItemSelect(int pos)
    {
        this.mCurrSelectPositon = pos;
        boolean select = false;
        for (int i = mCount - 1; i >= 0; i--)
        {

            if (this.mCurrSelectPositon == i)
            {
                select = true;
            } else
            {
                select = false;
            }
            indicators[i].setSelected(select);
        }
    }

}
