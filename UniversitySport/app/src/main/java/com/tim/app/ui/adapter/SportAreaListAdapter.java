package com.tim.app.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.application.library.log.DLOG;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.server.entry.FixLocationOutdoorSportPoint;
import com.tim.app.ui.adapter.viewholder.ViewHolder;

import java.util.List;


public class SportAreaListAdapter extends BaseRecyclerAdapter<ViewHolder, FixLocationOutdoorSportPoint>
        implements BaseRecyclerAdapter.OnItemClickListener {

    private Context mContext;
    private static final String TAG = "SportAreaListAdapter";

    public SportAreaListAdapter(Context mContext, List<FixLocationOutdoorSportPoint> mDataList) {
        super(mDataList);
        this.mContext = mContext;

        this.setOnItemClickListener(this);
    }

    @Override
    public com.tim.app.ui.adapter.viewholder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, R.layout.cell_sport_area);
        return holder;
    }


    @Override
    public void onBindViewHolder(com.tim.app.ui.adapter.viewholder.ViewHolder holder, int position, FixLocationOutdoorSportPoint data) {
        if (data == null) {
            return;
        }

        if (!TextUtils.isEmpty(data.getAreaName())) {
            holder.setText(R.id.tvAreaName, data.getAreaName());
        }

        if (!TextUtils.isEmpty(data.getAddress())) {
            holder.setText(R.id.tvAddress, data.getAddress());

        }

        if (data.getQualifiedCostTime() > 0) {
            String time = mContext.getString(R.string.digitalPlaceholder, String.valueOf(data.getQualifiedCostTime() / 60));
            holder.setText(R.id.tvTargetTime, time);
        }
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        DLOG.d(TAG, "onItemClick");

    }
}
