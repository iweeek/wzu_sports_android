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
import java.math.RoundingMode;
import java.util.List;

import static android.R.attr.data;


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
//        holder.tvSportTime.setText(TimeUtil.formatDate(mContext, data.getTime()));

        //异常数据处理，距离
        if (data.getSportDistance() < 0) {
            data.setSportDistance(0);
        }
        holder.tvLeft.setText(String.valueOf(data.getSportDistance()));

        //耗时
        if (data.getSportTime() < 0) {
            data.setSportTime(0);
        }
        int min = data.getSportTime() / 60;
        int sec = data.getSportTime() % 60;
        String time = min + "\'" + sec + "\"";
        holder.tvMiddle.setText(time);

        //速度
        if (data.getSportDistance() == 0) {
            holder.tvRight.setText("0.0");
        } else {
            double d = data.getSportDistance();
            double t = data.getSportTime();
            double v =  d / t;
            BigDecimal bd = new BigDecimal(v);
            bd = bd.setScale(1, RoundingMode.HALF_UP);
            holder.tvRight.setText(String.valueOf(bd));
        }

        if (data.getCostEnergy() >= 0) {
            holder.tvSportCost.setText(mContext.getString(R.string.curConsumeEnergy, String.valueOf(data.getCostEnergy())));
        }

        if (data.getSportTime() >= 0) {
            BigDecimal bd = new BigDecimal(data.getSportTime() / 60);
            bd.setScale(1, RoundingMode.HALF_UP);
            holder.tvSportCostTime.setText(mContext.getString(R.string.sportCostTime, String.valueOf(bd)));
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
