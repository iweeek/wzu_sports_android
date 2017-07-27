package com.application.library.widget.recycle;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RecyclerWrapAdapter extends RecyclerView.Adapter implements WrapperAdapter {

    private RecyclerView.Adapter mAdapter;

    private ArrayList<View> mHeaderViews;

    private ArrayList<View> mFootViews;

    private View emptyView;

    static final ArrayList<View> EMPTY_INFO_LIST =
            new ArrayList<View>();

    public int mCurrentPosition;

    public RecyclerWrapAdapter(ArrayList<View> mHeaderViews, ArrayList<View> mFootViews, RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
        if (mHeaderViews == null) {
            this.mHeaderViews = EMPTY_INFO_LIST;
        } else {
            this.mHeaderViews = mHeaderViews;
        }
        if (mHeaderViews == null) {
            this.mFootViews = EMPTY_INFO_LIST;
        } else {
            this.mFootViews = mFootViews;
        }
    }

    public RecyclerWrapAdapter(ArrayList<View> mHeaderViews, ArrayList<View> mFootViews, RecyclerView.Adapter mAdapter, View emptyView) {
        this.mAdapter = mAdapter;
        this.emptyView = emptyView;
        if (mHeaderViews == null) {
            this.mHeaderViews = EMPTY_INFO_LIST;
        } else {
            this.mHeaderViews = mHeaderViews;
        }
        if (mHeaderViews == null) {
            this.mFootViews = EMPTY_INFO_LIST;
        } else {
            this.mFootViews = mFootViews;
        }
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFootViews.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == RecyclerView.INVALID_TYPE) {
            return new HeaderViewHolder(mHeaderViews.get(0));
        } else if (viewType == RecyclerView.INVALID_TYPE - 1) {
            return new HeaderViewHolder(mFootViews.get(0));
        } else if (viewType == RecyclerView.INVALID_TYPE - 2) {
            return new HeaderViewHolder(emptyView);
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return;
        }
        int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                mAdapter.onBindViewHolder(holder, adjPosition);//调整过的位置，为了跳过头 header 。
                return;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mAdapter != null) {
            if (emptyView != null && mAdapter.getItemCount() == 0) {
                return getHeadersCount() + getFootersCount() + 1;
            }
            return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
        } else {//除了头和尾没有其他数据元素
            if (emptyView != null) {  //有一个空的元素
                return getHeadersCount() + getFootersCount() + 1;
            }
            return getHeadersCount() + getFootersCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        mCurrentPosition = position;
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {//位置小于 header 的数量，返回为 header
            return RecyclerView.INVALID_TYPE;
        }
        int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            if (emptyView != null && adapterCount == 0 && position == getHeadersCount()) {
                //位置在 header 数量的后一个，但是又没有实体数据，empty不为空，说明需要显示 emptyView
                return RecyclerView.INVALID_TYPE - 2;
            }
            if (adjPosition < adapterCount) {
                //否则说明有要显示的数据。 返回正常的 itemView
                return mAdapter.getItemViewType(adjPosition);
            }
        } else {
            if (emptyView != null && position == getHeadersCount()) {
                // 适配器直接为 null ，emptyView 不为空，而且在header 的后一个位置
                return RecyclerView.INVALID_TYPE - 2;
            }
        }
        //都不满足就返回 footer
        return RecyclerView.INVALID_TYPE - 1;
    }


    /**
     * todo 这里好像有问题
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        int numHeaders = getHeadersCount();
        if (mAdapter != null && position >= numHeaders) {
            //越过 header 的位置，并且有要显示的数据
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getItemCount();
            //显示 emptyView
            if (emptyView != null && adapterCount == 0 && position == numHeaders) {
                return -1;
            }
            //递归调用 getItemId
            if (adjPosition < adapterCount) {
                return mAdapter.getItemId(adjPosition);
            }
        } else {
            //显示 emptyView
            if (emptyView != null && position == numHeaders) {
                return -1;
            }
        }
        return -1;
    }


    @Override
    public RecyclerView.Adapter getWrappedAdapter() {
        return mAdapter;
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void removeHeaderView() {
        mHeaderViews.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void removeFooterView() {
        mFootViews.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mAdapter.registerAdapterDataObserver(observer);
    }

    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mAdapter.unregisterAdapterDataObserver(observer);
    }
}
