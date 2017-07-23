package com.tim.app.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.server.entry.WifiInfo;

import java.util.List;


public class WifiListAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, WifiInfo> {

    private Context mContext;
    private static final String TAG = "WifiListAdapter";

    public WifiListAdapter(Context mContext, List<WifiInfo> mDataList) {
        super(mDataList);
        this.mContext = mContext;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder
                holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_wifi_info, parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder mHolder, int position, WifiInfo data) {
        if (data == null) {
            return;
        }
        final ViewHolder holder = (ViewHolder) mHolder;

        if (!TextUtils.isEmpty(data.getWifiName())) {
            holder.tvWifiName.setText(data.getWifiName());
        }

        if (!TextUtils.isEmpty(data.getSsid())) {
            holder.tvWifiSsid.setText(data.getSsid());
        }

    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        TextView tvWifiName;
        TextView tvWifiSsid;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWifiName = (TextView) itemView.findViewById(R.id.tvWifiName);
            tvWifiSsid = (TextView) itemView.findViewById(R.id.tvWifiSsid);
        }

    }

}
