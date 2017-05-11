package com.application.library.widget.horizontal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 横向滑动的线性布局
 * 
 * @author CCCMAX
 */
public class HorizontalScrollLinearlayout extends MFHorizontalScrollView
{

    public HorizontalScrollLinearlayout(Context context)
    {
        super(context);
    }

    public HorizontalScrollLinearlayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LinearLayout content_layout = null;

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        content_layout = new LinearLayout(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(-2, -1);
        this.addView(content_layout, lp);
    }

    protected int divide_horizontal = 12;

    public void setDivideHorizontal(int divide_horizontal_dip)
    {
        float scale = getContext().getResources().getDisplayMetrics().density;
        divide_horizontal = (int) (divide_horizontal_dip * scale + 0.5f);
    }

    public void addItemView(View childview)
    {
        addItemView(childview, -1);
    }

    public void addItemView(View childview, int index)
    {
        int child_count = content_layout.getChildCount();
        if (index < 0 || index > child_count)
            index = child_count;

        LinearLayout.LayoutParams childlp = (LinearLayout.LayoutParams) childview.getLayoutParams();
        if (childlp == null)
        {
            childlp = new LinearLayout.LayoutParams(-2, -1);
        }

        if (child_count > 0)
        {
            if (index != 0)
            {
                Log.d("cccmax", "addItemView index 2 =" + index + "  child_count=" + child_count);
                childlp.setMargins(childlp.leftMargin + divide_horizontal, childlp.topMargin, childlp.rightMargin,
                        childlp.bottomMargin);
            } else
            {
                Log.d("cccmax", "addItemView index 1 =" + index + "  child_count=" + child_count);
                onAddFirstItemSetMargins(childview, childlp);
            }
        }

        content_layout.addView(childview, index, childlp);

        // if (child_count > 0 && index != 0)
        // {
        // View view = content_layout.getChildAt(index - 1);
        // LinearLayout.LayoutParams view_lp = (android.widget.LinearLayout.LayoutParams) view.getLayoutParams();
        // view_lp.setMargins(view_lp.leftMargin, view_lp.topMargin, divide_horizontal, view_lp.bottomMargin);
        // view.setLayoutParams(view_lp);
        // }
    }

    /**
     * 添加第一个item的时候 设置外间距
     * 
     * @param childlp
     */
    protected void onAddFirstItemSetMargins(View childview, LinearLayout.LayoutParams childlp)
    {

    }
}
