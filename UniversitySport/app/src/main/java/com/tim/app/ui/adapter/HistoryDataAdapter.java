package com.tim.app.ui.adapter;

import android.content.Context;
import android.graphics.Color;
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
import java.text.SimpleDateFormat;
import java.util.Date;
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

        Date date = new Date(data.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日HH点mm分");
        holder.tvSportTime.setText(sdf.format(date));

        if (data.isQualified()) {
            holder.tvSportQualified.setText("达标");
            holder.tvSportQualified.setTextColor(Color.parseColor("#42cc42"));
        } else {
            holder.tvSportQualified.setText("不达标");
            holder.tvSportQualified.setTextColor(Color.parseColor("#ff0000"));
        }

        //异常数据处理，距离
        if (data.getSportDistance() < 0) {
            data.setSportDistance(0);
        }
        holder.tvLeft.setText(String.valueOf(data.getSportDistance()));

        //耗时
        if (data.getSportTime() < 0) {
            data.setSportTime(0);
        }

        String time = com.tim.app.util.TimeUtil.formatMillisTime(data.getSportTime() * 1000);
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
            double t = data.getSportTime();
            BigDecimal bd = new BigDecimal(t / 60);
            bd = bd.setScale(1, RoundingMode.HALF_UP);
            holder.tvSportCostTime.setText(mContext.getString(R.string.sportCostTime, String.valueOf(bd)));
        }
    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        TextView tvSportDesc;
        TextView tvSportTime;
        TextView tvSportQualified;

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
            tvSportQualified = (TextView) itemView.findViewById(R.id.tvSportQualified);

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
