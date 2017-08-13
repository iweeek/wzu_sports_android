package com.tim.app.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * @创建者 倪军
 * @创建时间 2017/8/13
 * @描述
 */

public class RecyclerAdapter<T extends ViewHolder, D> extends RecyclerView.Adapter<T> {
    private List<D> mDataList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public RecyclerAdapter(List<D> dataList) {
        mDataList = dataList;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    interface OnItemClickListener {}
    interface OnItemLongClickListener {}

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public void setOnLongItemClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }
}
