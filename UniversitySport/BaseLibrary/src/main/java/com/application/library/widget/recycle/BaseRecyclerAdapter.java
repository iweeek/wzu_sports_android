package com.application.library.widget.recycle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseRecyclerAdapter<T extends BaseRecyclerAdapter.BaseRecyclerViewHolder, D> extends RecyclerView.Adapter<T> {

    private List<D> mDataList;

    protected OnItemClickListener mListener;
    protected OnItemLongClickListener mLongListener;

    public BaseRecyclerAdapter(List<D> mDataList) {
        this.mDataList = mDataList;
    }

    public List<D> getDataList() {
        return mDataList;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        T holder;
        holder = createViewHolder(LayoutInflater.from(parent.getContext()), parent, viewType);
        if (holder == null) {
            holder = createViewHolder(LayoutInflater.from(parent.getContext()), viewType);
        }

        return holder;
    }


    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(v, position, getItemId(position));
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongListener != null) {
                    return mLongListener.onItemLongClick(v, position, getItemId(position));
                }
                return false;
            }
        });
        onBindViewHolder((T) holder, position, mDataList.get(position));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongListener = listener;
    }

    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        } else {
            return mDataList.size();
        }
    }

    static public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
        public BaseRecyclerViewHolder(View itemView) {
            super(itemView);
        }

        public <K extends View> K findView(int id) {
            return (K) itemView.findViewById(id);
        }
    }

    public void updateData(List<D> datas) {
        this.mDataList = datas;
        notifyDataSetChanged();
    }
    public void updateDataDiff(List<D> datas) {
        this.mDataList = datas;
    }

    @Deprecated
    public T createViewHolder(LayoutInflater inflater, int viewType) {
        return null;
    }

    public T createViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return null;
    }

    public abstract void onBindViewHolder(T holder, int position, D data);

    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position, long id);
    }
}
