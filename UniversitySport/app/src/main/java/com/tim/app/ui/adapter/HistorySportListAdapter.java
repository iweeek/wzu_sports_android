package com.tim.app.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.library.log.DLOG;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.application.library.widget.refresh.PtrFrameLayout;
import com.tim.app.R;
import com.tim.app.server.entry.HistoryRunningSportEntry;
import com.tim.app.server.entry.HistorySportEntry;
import com.tim.app.ui.activity.HistoryItem;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.amap.api.mapcore.util.cz.F;
import static com.application.library.log.DLOG.V;
import static com.tim.app.R.id.tvLeft;
import static com.tim.app.R.id.tvSportDesc;
import static com.tim.app.R.id.tvSportQualified;


public class HistorySportListAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, HistoryItem>
        implements  BaseRecyclerAdapter.OnItemClickListener{
    private Context context;
    private static final String TAG = "HistorySportListAdapter";

    public HistorySportListAdapter(Context context, List<HistoryItem> dataList) {
        super(dataList);
        this.context = context;

        this.setOnItemClickListener(this);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder
                holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_daily_record, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position, HistoryItem data) {
        boolean isDelimit = false;
        if (data == null) {
            return;
        }

//        Log.d(TAG, "data.historySportEntryList.size(): " + data.historySportEntryList.size());

        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvSportDate.setText(data.date);

        for (int i = 0; i < data.historySportEntryList.size(); i ++) {
//            Log.d(TAG, "data.historySportEntryList item: " + data.historySportEntryList.get(i).getSportDate());
            LinearLayout ll = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.history_daily_record_item, null);
            if (data.historySportEntryList.get(i).getType() == HistorySportEntry.RUNNING_TYPE) {
                TextView tvSportDesc = (TextView) ll.findViewById(R.id.tvSportDesc);
                tvSportDesc.setText(data.historySportEntryList.get(i).getSportName());

                TextView tvSportQualified = (TextView) ll.findViewById(R.id.tvSportQualified);
//                tvSportQualified.setText(data.historySportEntryList.get(i));

                TextView tvSportTime = (TextView) ll.findViewById(R.id.tvSportTime);
                SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日HH点mm分");
                tvSportTime.setText(sdf.format(data.historySportEntryList.get(i).getStartTime()));
                tvSportTime.setVisibility(View.VISIBLE);

                //距离
                TextView tvLeft = (TextView) ll.findViewById(R.id.tvLeft);
                tvLeft.setText(data.historySportEntryList.get(i).getDistance() + "");

                //耗时
                TextView tvMiddle = (TextView) ll.findViewById(R.id.tvMiddle);
                String time = com.tim.app.util.TimeUtil.formatMillisTime(data.historySportEntryList.get(i).getCostTime() * 1000);
                tvMiddle.setText(time);

                //消耗热量
                TextView tvRight = (TextView) ll.findViewById(R.id.tvRight);
                tvRight.setText(data.historySportEntryList.get(i).getKcalConsumed() + "");

                ((ViewHolder) holder).llSportItem.addView(ll);
            } else {
                View view = (View) ll.findViewById(R.id.vTopDelimiter);
                if (!isDelimit) {
                    view.setVisibility(View.VISIBLE);
                    isDelimit = true;
                } else {

                }
                TextView tvSportDesc = (TextView) ll.findViewById(R.id.tvSportDesc);
                tvSportDesc.setText(data.historySportEntryList.get(i).getSportName());

                TextView tvSportQualified = (TextView) ll.findViewById(R.id.tvSportQualified);
//                tvSportQualified.setText(data.historySportEntryList.get(i));

                TextView tvSportTime = (TextView) ll.findViewById(R.id.tvSportTime);
                SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日HH点mm分");
                tvSportTime.setText(sdf.format(data.historySportEntryList.get(i).getStartTime()));
                tvSportTime.setVisibility(View.VISIBLE);

                //距离区域要隐藏
                LinearLayout llLeft = (LinearLayout) ll.findViewById(R.id.llLeft);
                llLeft.setVisibility(View.INVISIBLE);

                //耗时
                TextView tvMiddle = (TextView) ll.findViewById(R.id.tvMiddle);
                String time = com.tim.app.util.TimeUtil.formatMillisTime(data.historySportEntryList.get(i).getCostTime() * 1000);
                tvMiddle.setText(time);

                //耗时
                TextView tvRight = (TextView) ll.findViewById(R.id.tvRight);
                tvRight.setText(data.historySportEntryList.get(i).getKcalConsumed() + "");

                ((ViewHolder) holder).llSportItem.addView(ll);
            }
        }
//
//        if (!TextUtils.isEmpty(data.getSportName())) {
//            holder.tvSportDesc.setText(data.getSportName());
//        }
//
//        Date date = new Date(data.getStartTime());
//        SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日HH点mm分");
//        holder.tvSportTime.setText(sdf.format(date));
//
//        if (data.isQualified()) {
//            holder.tvSportQualified.setText("达标");
//            holder.tvSportQualified.setTextColor(Color.parseColor("#42cc42"));
//        } else {
//            holder.tvSportQualified.setText("不达标");
//            holder.tvSportQualified.setTextColor(Color.parseColor("#ff0000"));
//        }
//
//        //异常数据处理，距离
//        if (data.getDistance() < 0) {
//            data.setDistance(0);
//        }
//        holder.tvLeft.setText(String.valueOf(data.getDistance()));
//
//        //耗时
//        if (data.getCostTime() < 0) {
//            data.setCostTime(0);
//        }
//
//        String time = com.tim.app.util.TimeUtil.formatMillisTime(data.getCostTime() * 1000);
//        holder.tvMiddle.setText(time);
//
//        //速度
//        if (data.getDistance() == 0) {
//            holder.tvRight.setText("0.0");
//        } else {
//            double d = data.getDistance();
//            double t = data.getCostTime();
//            double v =  d / t;
//            BigDecimal bd = new BigDecimal(v);
//            bd = bd.setScale(1, RoundingMode.HALF_UP);
//            holder.tvRight.setText(String.valueOf(bd));
//        }
//
//        if (data.getKcalConsumed() >= 0) {
//            holder.tvSportCost.setText(mContext.getString(R.string.curConsumeEnergy, String.valueOf(data.getKcalConsumed())));
//        }
//
//        if (data.getCostTime() >= 0) {
//            double t = data.getCostTime();
//            BigDecimal bd = new BigDecimal(t / 60);
//            bd = bd.setScale(1, RoundingMode.HALF_UP);
//            holder.tvSportCostTime.setText(mContext.getString(R.string.sportCostTime, String.valueOf(bd)));
//        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        DLOG.d(TAG, "onItemClick");
//        SportResultActivity.start(mContext, getDataList().get(position).historySportEntryList.get());

    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        TextView tvSportDate;
        TextView tvEnergyCost;
        LinearLayout llSportItem;
//        LinearLayout llAreaItem;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSportDate = (TextView) itemView.findViewById(R.id.tvSportDate);
            tvEnergyCost = (TextView) itemView.findViewById(R.id.tvEnergyCost);
            llSportItem = (LinearLayout)itemView.findViewById(R.id.llSportItem);
//            llAreaItem = (LinearLayout)itemView.findViewById(R.id.llAreaItem);
        }

    }

}
