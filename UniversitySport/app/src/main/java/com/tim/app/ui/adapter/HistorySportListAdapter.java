package com.tim.app.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.library.log.DLOG;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.server.entry.HistoryAreaSportEntry;
import com.tim.app.server.entry.HistoryRunningSportEntry;
import com.tim.app.server.entry.HistorySportEntry;
import com.tim.app.ui.activity.HistoryItem;
import com.tim.app.ui.adapter.viewholder.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;


public class HistorySportListAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, HistoryItem>
        implements BaseRecyclerAdapter.OnItemClickListener{
    private Context mContext;
    private static final String TAG = "HistorySportListAdapter";

    public HistorySportListAdapter(Context context, List<HistoryItem> dataList) {
        super(dataList);
        this.mContext = context;
        this.setOnItemClickListener(this);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder
                holder = new ViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.history_daily_record, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position, HistoryItem data) {
        boolean isDelimit = false;
        if (data == null) {
            return;
        }

        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setText(R.id.tvSportDate, data.date);

        for (int i = 0; i < data.historySportEntryList.size(); i++) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.history_daily_record_item, null);

            if (data.historySportEntryList.get(i).getType() == HistorySportEntry.RUNNING_TYPE) {
                //向下转型
                HistoryRunningSportEntry runningData = (HistoryRunningSportEntry) data.historySportEntryList.get(i);
//                ll.setClickable(true);
                ll.setTag(runningData);

                ImageView iv = (ImageView) ll.findViewById(R.id.ivSporIcon);
                iv.setBackgroundResource(R.drawable.ic_run_sport);

                TextView tvSportDesc = (TextView) ll.findViewById(R.id.tvSportDesc);
                tvSportDesc.setText(runningData.getSportName());

                TextView tvSportQualified = (TextView) ll.findViewById(R.id.tvSportQualified);
                if (runningData.isQualified()) {
                    tvSportQualified.setText("达标");
                    tvSportQualified.setTextColor(Color.parseColor("#42cc42"));
                } else {
                    tvSportQualified.setText("不达标");
                    tvSportQualified.setTextColor(Color.parseColor("#ff0000"));
                }

                TextView tvSportTime = (TextView) ll.findViewById(R.id.tvSportTime);
                SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日HH点mm分");
                tvSportTime.setText(sdf.format(runningData.getStartTime()));
                tvSportTime.setVisibility(View.VISIBLE);

                //距离
                TextView tvLeft = (TextView) ll.findViewById(R.id.tvLeft);
                tvLeft.setText(runningData.getDistance() + "");

                //耗时
                TextView tvMiddle = (TextView) ll.findViewById(R.id.tvMiddle);
                String time = com.tim.app.util.TimeUtil.formatMillisTime(runningData.getCostTime() * 1000);
                tvMiddle.setText(time);

                //消耗热量
                TextView tvRight = (TextView) ll.findViewById(R.id.tvRight);
                tvRight.setText(runningData.getKcalConsumed() + "");

                viewHolder.addView(R.id.llSportItem, ll);
            } else {
                View view = (View) ll.findViewById(R.id.vTopDelimiter);
                if (!isDelimit) {
                    view.setVisibility(View.VISIBLE);
                    isDelimit = true;
                } else {

                }

                HistoryAreaSportEntry areaData = (HistoryAreaSportEntry) data.historySportEntryList.get(i);

                ImageView iv = (ImageView) ll.findViewById(R.id.ivSporIcon);
                iv.setBackgroundResource(R.drawable.ic_fix_location_sport);

                TextView tvSportDesc = (TextView) ll.findViewById(R.id.tvSportDesc);
                tvSportDesc.setText(areaData.getSportName());

                TextView tvSportQualified = (TextView) ll.findViewById(R.id.tvSportQualified);
                if (data.historySportEntryList.get(i).isQualified()) {
                    tvSportQualified.setText("达标");
                    tvSportQualified.setTextColor(Color.parseColor("#42cc42"));
                } else {
                    tvSportQualified.setText("不达标");
                    tvSportQualified.setTextColor(Color.parseColor("#ff0000"));
                }

                TextView tvSportTime = (TextView) ll.findViewById(R.id.tvSportTime);
                SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日HH点mm分");
                tvSportTime.setText(sdf.format(areaData.getStartTime()));
                tvSportTime.setVisibility(View.VISIBLE);

                //距离区域要隐藏
                LinearLayout llLeft = (LinearLayout) ll.findViewById(R.id.llLeft);
                llLeft.setVisibility(View.INVISIBLE);

                //耗时
                TextView tvMiddle = (TextView) ll.findViewById(R.id.tvMiddle);
                String time = com.tim.app.util.TimeUtil.formatMillisTime(areaData.getCostTime() * 1000);
                tvMiddle.setText(time);

                //耗时
                TextView tvRight = (TextView) ll.findViewById(R.id.tvRight);
                tvRight.setText(areaData.getKcalConsumed() + "");

                viewHolder.addView(R.id.llSportItem, ll);

            }
        }
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        DLOG.d(TAG, "onItemClick");
        // todo       SportResultActivity.start(mContext, getDataList().get(position).historySportEntryList.get());

    }
}
