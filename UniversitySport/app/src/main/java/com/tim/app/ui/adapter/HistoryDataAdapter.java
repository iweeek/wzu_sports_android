package com.tim.app.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.library.util.TimeUtil;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.server.entry.HistoryData;

import java.util.List;


public class HistoryDataAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, HistoryData> {
    private Context mContext;

    public HistoryDataAdapter(Context mContext, List<HistoryData> mDataList) {
        super(mDataList);
        this.mContext = mContext;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder
                holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_history_data, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder mHolder, int position, HistoryData data) {
        if (data == null) {
            return;
        }
        final ViewHolder holder = (ViewHolder) mHolder;

        if (!TextUtils.isEmpty(data.getSportDesc())) {
            holder.tvSportDesc.setText(data.getSportDesc());
        }
        holder.tvSportTime.setText(TimeUtil.formatDate(mContext, data.getTime()));
        if (!TextUtils.isEmpty(data.getSpeed())) {
            holder.tvSportSpeed.setText(mContext.getString(R.string.targetSpeed, data.getSpeed()));
        }
        if (data.getCompleteCount() > 0) {
            holder.tvSportCompleteCount.setText(mContext.getString(R.string.completeCount, String.valueOf(data.getCompleteCount())));
        }
        if (data.getMinDistance() > 0) {
            holder.tvSportDistance.setText(mContext.getString(R.string.targetDistance, String.valueOf(data.getMinDistance())));
        }
        if (data.getCostNumber() > 0) {
            holder.tvSportCost.setText(mContext.getString(R.string.sportCostQuantity, String.valueOf(data.getCostNumber())));
        }
        if (data.getSportTime() > 0) {
            holder.tvSportCostTime.setText(mContext.getString(R.string.sportCostTime, String.valueOf(data.getSportTime())));
        }
    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        TextView tvSportDesc;
        TextView tvSportTime;
        TextView tvSportSpeed;
        TextView tvSportCompleteCount;
        TextView tvSportDistance;
        TextView tvSportCost;
        TextView tvSportCostTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSportDesc = (TextView) itemView.findViewById(R.id.tvSportDesc);
            tvSportTime = (TextView) itemView.findViewById(R.id.tvSportTime);
            tvSportSpeed = (TextView) itemView.findViewById(R.id.tvSportSpeed);
            tvSportCompleteCount = (TextView) itemView.findViewById(R.id.tvSportCompleteCount);
            tvSportDistance = (TextView) itemView.findViewById(R.id.tvSportDistance);
            tvSportCost = (TextView) itemView.findViewById(R.id.tvSportCost);
            tvSportCostTime = (TextView) itemView.findViewById(R.id.tvSportCostTime);
        }

    }

}
