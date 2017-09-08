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

    /**
     * 这段代码基本没用返回的都是null ， 所以需要子类重写
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        T holder;
        holder = createViewHolder(LayoutInflater.from(parent.getContext()), parent, viewType);
        if (holder == null) {
            holder = createViewHolder(LayoutInflater.from(parent.getContext()), viewType);
        }
        return holder;
    }

    public abstract void onBindViewHolder(T holder, int position, D data);

    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder holder, final int position) {
        //提供点击事件
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
                    return mLongListener.onItemLongClick(v, position, getItemId(position));  //todo  这里id没有提供正确。
                }
                return false;
            }
        });
        //调用子类重写的
        onBindViewHolder((T) holder, position, mDataList.get(position));
    }

    public int getItemViewType(int position, D data) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        } else {
            return mDataList.size();
        }
    }

    /**
     * 可以利用 SparseArray 创建一个保存 findViewById 出来的 view 的数组集合，下次取的时候先从这个数组中查找。
     */
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


    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position, long id);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongListener = listener;
    }
}
