package com.tim.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanxiaofeng
 * @time 2013年7月12日
 * @description 列表的adapter
 */
public abstract class AdapterBaseList<T> extends BaseAdapter {

	protected List<T> mList;
	protected Context mContext;

	public AdapterBaseList(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public T getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView,
			ViewGroup parent);

	public void setList(List<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	public List<T> getList() {
		return mList;
	}

	public void clearAll() {
		if (null != mList) {
			mList.clear();
		}
		notifyDataSetChanged();
	}

	public void addAll(ArrayList<T> list) {
		if (null == mList) {
			mList = list;
		} else {
			mList.addAll(list);
		}

		notifyDataSetChanged();
	}

	public void setList(T[] list) {
		ArrayList<T> arrayList = new ArrayList<T>(list.length);
		for (T t : list) {
			arrayList.add(t);
		}
		setList(arrayList);
	}
}
