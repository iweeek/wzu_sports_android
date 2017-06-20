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

import java.math.BigDecimal;
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
            holder.tvLeft.setText(mContext.getString(R.string.percents, data.getSpeed()));
        }

        if (data.getSportTime() > 0) {
            holder.tvMiddle.setText(String.valueOf(data.getSportTime() / 60) + "分");
        }

        if (data.getSportDistance() > 0) {
            holder.tvRight.setText(String.valueOf(data.getSportDistance()) + "米");
        }

        if (data.getSportTime() > 0 && data.getSportDistance() > 0) {

            if(data.getSportTime() > 60){
                BigDecimal bd = new BigDecimal(data.getSportTime() / 60);
                bd.setScale(2);
                holder.tvLeft.setText(String.valueOf(data.getSportDistance() / bd.doubleValue()) + "米/分");
            }else if(data.getSportTime() < 60){
                holder.tvLeft.setText(String.valueOf(data.getSportDistance() / (data.getSportTime())) + "米/秒");
            }
        }
        if (data.getCostEnergy() > 0) {
            holder.tvSportCost.setText(mContext.getString(R.string.curConsumeEnergy, String.valueOf(data.getCostEnergy())));
        }
        if (data.getSportTime() > 0) {
            holder.tvSportCostTime.setText(mContext.getString(R.string.sportCostTime, String.valueOf(data.getSportTime() / 60)));
        }
    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        TextView tvSportDesc;
        TextView tvSportTime;

        TextView tvLeftLabel;
        TextView tvMiddleLabel;
        TextView tvRightLabel;

        TextView tvLeft;
        TextView tvMiddle;
        TextView tvRight;

        TextView tvSportCost;
        TextView tvSportCostTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSportDesc = (TextView) itemView.findViewById(R.id.tvSportDesc);
            tvSportTime = (TextView) itemView.findViewById(R.id.tvSportTime);

            tvLeft = (TextView) itemView.findViewById(R.id.tvLeft);
            tvMiddle = (TextView) itemView.findViewById(R.id.tvMiddle);
            tvRight = (TextView) itemView.findViewById(R.id.tvRight);

            tvLeft = (TextView) itemView.findViewById(R.id.tvLeft);
            tvMiddle = (TextView) itemView.findViewById(R.id.tvMiddle);
            tvRight = (TextView) itemView.findViewById(R.id.tvRight);

            tvSportCost = (TextView) itemView.findViewById(R.id.tvSportCost);
            tvSportCostTime = (TextView) itemView.findViewById(R.id.tvSportCostTime);
        }

    }

}
