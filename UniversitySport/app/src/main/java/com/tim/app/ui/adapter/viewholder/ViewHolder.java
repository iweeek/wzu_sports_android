package com.tim.app.ui.adapter.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.library.widget.recycle.BaseRecyclerAdapter;

/**
 * @创建者 倪军
 * @创建时间 2017/8/4
 * @描述  万能的ViewHolder
 */

public class ViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder {
    private SparseArray<View> mViews;
    private View mContentView;
    private Context mContext;


    public ViewHolder(Context context, View itemView) {
        super(itemView);
        mViews = new SparseArray<View>();
        mContentView = itemView;
        mContext = context;
    }

    public static ViewHolder createViewHolder(Context context, View itemView) {
        ViewHolder holder = new ViewHolder(context, itemView);
        return holder;
    }

    public static ViewHolder createViewHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(context, itemView);
        return holder;
    }

    public <T extends View> T findView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mContentView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /******************    可以继续扩展  ***********************/
    public ViewHolder setText(int viewId, String text) {
        TextView tv = findView(viewId);
        tv.setText(text);
        return this;
    }

    public ViewHolder setBackground(int viewId, Drawable background) {
        ImageView iv = findView(viewId);
        iv.setBackground(background);
        return this;
    }

    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = findView(viewId);
        view.setVisibility(visible ? View.VISIBLE : view.GONE);
        return this;
    }

    public ViewHolder addView(int parentId, View childView){
        ViewGroup parent = findView(parentId);
        parent.addView(childView);
        return this;
    }

    public ViewHolder removeAllViews(int parentId){
        ViewGroup parent = findView(parentId);
        parent.removeAllViews();
        return this;
    }

    public ViewHolder setLayoutParams(int parentId, ViewGroup.LayoutParams params){
        ViewGroup view = findView(parentId);
        view.setLayoutParams(params);
        return this;
    }

}
