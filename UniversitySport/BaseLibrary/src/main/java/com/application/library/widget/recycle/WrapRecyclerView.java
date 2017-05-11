package com.application.library.widget.recycle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class WrapRecyclerView extends RecyclerView {

    private ArrayList<View> mHeaderViews = new ArrayList<>();

    private ArrayList<View> mFootViews = new ArrayList<>();

    private Adapter mAdapter;

    private View emptyView;

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View view) {
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof RecyclerWrapAdapter)) {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    public void addFootView(View view) {
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof RecyclerWrapAdapter)) {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    public void removeHeaderView() {
        if (mHeaderViews != null && mHeaderViews.size() > 0) {
            if (mAdapter != null) {
                if (mAdapter instanceof RecyclerWrapAdapter) {
                    ((RecyclerWrapAdapter) mAdapter).removeHeaderView();
                }
            }
        }

    }

//    public void removeHeaderView(View header) {
//        if (mHeaderViews != null && mHeaderViews.size() > 0) {
//            boolean success = mHeaderViews.remove(header);
//            if (success && (mAdapter instanceof RecyclerWrapAdapter)) {
//                ((RecyclerWrapAdapter) mAdapter).notifyDataSetChanged();
//            }
//        }
//    }

    public void removeFootView() {
        if (mFootViews != null && mFootViews.size() > 0) {
            if (mAdapter != null) {
                if (mAdapter instanceof RecyclerWrapAdapter) {
                    ((RecyclerWrapAdapter) mAdapter).removeFooterView();
                }
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeaderViews.isEmpty() && mFootViews.isEmpty()) {
            mAdapter = adapter;
        } else {
            if (emptyView != null) {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, adapter, emptyView);
            } else {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, adapter);
            }

        }
        super.setAdapter(mAdapter);
    }

    public Adapter getWarpAdapter() {
        return mAdapter;
    }

    /**
     * 只使用于带headview的recycleview
     *
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public int getCurrentPosition(){
        if (mAdapter != null && mAdapter instanceof RecyclerWrapAdapter){
            return ((RecyclerWrapAdapter) mAdapter).mCurrentPosition;
        }
        return 0;
    }
}
