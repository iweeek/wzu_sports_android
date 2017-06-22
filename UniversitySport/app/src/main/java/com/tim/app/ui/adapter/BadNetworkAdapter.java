package com.tim.app.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.server.entry.BadNetWork;

import java.util.List;

/**
 * Created by nimon on 2017/6/21.
 */

public class BadNetworkAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder,BadNetWork> {

    private Context mContext;
    private static final String TAG = "BadNetworkAdapter";
    public static final String BAD_NETWORK = "badnetwork";

    public BadNetworkAdapter(Context mContext, List<BadNetWork> mDataList) {
        super(mDataList);
        this.mContext = mContext;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bad_network_view, null);
        view.setTag(BAD_NETWORK);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position, BadNetWork data) {
        Log.d(TAG, "onBindViewHolder: ");
    }


    public class ViewHolder extends BaseRecyclerViewHolder {
        LinearLayout llBadNetworkContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            llBadNetworkContainer = (LinearLayout) itemView.findViewById(R.id.llBadNetworkContainer);
            llBadNetworkContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    RT.getScreenWidth(),(int)(RT.getScreenWidth()*0.43)));
        }
    }
}
