package com.tim.app.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.library.log.DLOG;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
import com.tim.app.server.entry.HistoryRunningSportEntry;
import com.tim.app.ui.activity.HistoryItem;
import com.tim.app.ui.activity.SportResultActivity;
import com.tim.app.ui.adapter.viewholder.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.lzy.okhttputils.OkHttpUtils.getContext;


public class HistorySportListAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, HistoryItem>
        implements BaseRecyclerAdapter.OnItemClickListener {
    private Context mContext;
    private static final String TAG = "HistorySportListAdapter";
    private static Typeface typeface;

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
    public void onBindViewHolder(BaseRecyclerViewHolder holder, final int position, HistoryItem data) {
        boolean isDelimit = false;
        if (data == null) {
            return;
        }

        int totalEnergyCost = 0;
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.removeAllViews(R.id.llSportItem);

        for (int i = 0; i < data.historySportEntryList.size(); i++) {
            final LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.history_daily_record_item, null);
            if (data.historySportEntryList.get(i).getType() == AppConstant.RUNNING_TYPE) {
                //向下转型
                final HistoryRunningSportEntry runningSportEntry = (HistoryRunningSportEntry) data.historySportEntryList.get(i);
                //                ll.setClickable(true);
                ll.setTag(runningSportEntry);

                //运动名字
                TextView tvSportDesc = (TextView) ll.findViewById(R.id.tvSportDesc);
                tvSportDesc.setText(runningSportEntry.getSportName());

                ImageView ivSportQualified = (ImageView) ll.findViewById(R.id.ivSportQualified);
                if (runningSportEntry.getEndedAt() == 0) {
                    ivSportQualified.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_runman_orange));
                } else {
                    if (runningSportEntry.isValid()) {
                        if (runningSportEntry.isQualified()) {
                            ivSportQualified.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_runman_blue));
                        } else {
                            ivSportQualified.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_runman_red));
                        }
                    } else {
                        ivSportQualified.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_runman_orange));
                    }
                }

                TextView tvSportTime = (TextView) ll.findViewById(R.id.tvSportTime);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
                tvSportTime.setText(sdf.format(runningSportEntry.getStartTime()));
                tvSportTime.setVisibility(View.VISIBLE);

                //距离/1000后单位为公里
                TextView tvLeft = (TextView) ll.findViewById(R.id.tvLeft);
                Double distance = runningSportEntry.getDistance()*1.0/1000;
                String result = String .format("%.1f",distance);
                tvLeft.setText(result);
                tvLeft.setTypeface(getTypeface(getContext()),Typeface.ITALIC);  //设置字体 斜体

                //耗时
                TextView tvMiddle = (TextView) ll.findViewById(R.id.tvMiddle);
                String time = com.tim.app.util.TimeUtil.formatMillisTime(runningSportEntry.getCostTime() * 1000);
                tvMiddle.setText(time);

                //消耗热量
                TextView tvRight = (TextView) ll.findViewById(R.id.tvRight);
                tvRight.setText(runningSportEntry.getKcalConsumed() + "");

                //累加总共消耗热量
                totalEnergyCost += runningSportEntry.getKcalConsumed();

                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SportResultActivity.start(mContext, runningSportEntry);
                    }
                });


                viewHolder.addView(R.id.llSportItem, ll);
            }
//            else {
//                View view = (View) ll.findViewById(R.id.vTopDelimiter);
//                if (!isDelimit) {
//                    view.setVisibility(View.VISIBLE);
//                    isDelimit = true;
//                } else {
//
//                }
//
//                final HistoryAreaSportEntry areaSportEntry = (HistoryAreaSportEntry) data.historySportEntryList.get(i);
//
//                ImageView iv = (ImageView) ll.findViewById(R.id.ivSporIcon);
//                iv.setBackgroundResource(R.drawable.ic_fix_location_sport);
//
//                //区域名字
//                TextView tvSportDesc = (TextView) ll.findViewById(R.id.tvSportDesc);
//                tvSportDesc.setText(areaSportEntry.getAreaSport());
//
//                TextView tvSportQualified = (TextView) ll.findViewById(R.id.tvSportQualified);
//                if (areaSportEntry.getEndedAt() == 0) {
//                    tvSportQualified.setText("非正常结束");
//                    tvSportQualified.setTextColor(Color.parseColor("#FFAA2B"));
//                } else {
//                    if (data.historySportEntryList.get(i).isQualified()) {
//                        tvSportQualified.setText("达标");
//                        tvSportQualified.setTextColor(Color.parseColor("#42cc42"));
//                    } else {
//                        tvSportQualified.setText("不达标");
//                        tvSportQualified.setTextColor(Color.parseColor("#ff0000"));
//                    }
//                }
//                TextView tvSportTime = (TextView) ll.findViewById(R.id.tvSportTime);
//                SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日HH点mm分");
//                tvSportTime.setText(sdf.format(areaSportEntry.getStartTime()));
//                tvSportTime.setVisibility(View.VISIBLE);
//
//                //距离区域要隐藏
//                LinearLayout llLeft = (LinearLayout) ll.findViewById(R.id.llLeft);
//                llLeft.setVisibility(View.INVISIBLE);
//
//                //耗时
//                TextView tvMiddle = (TextView) ll.findViewById(R.id.tvMiddle);
//                //                String time = com.tim.app.util.TimeUtil.formatMillisTime(areaData.getCostTime() * 1000);
//                tvMiddle.setText(String.valueOf(areaSportEntry.getCostTime() / 60) + " 分钟");
//
//                //耗能
//                TextView tvRight = (TextView) ll.findViewById(R.id.tvRight);
//                tvRight.setText(areaSportEntry.getKcalConsumed() + "");
//
//                //累加总共消耗热量
//                totalEnergyCost += areaSportEntry.getKcalConsumed();
//
//                ll.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        SportResultActivity.start(mContext, areaSportEntry);
//                    }
//                });
//
//                viewHolder.addView(R.id.llSportItem, ll);
//            }
        }
//        //当天日期
//        viewHolder.setText(R.id.tvSportDate, data.date);
//        //一天的热量消耗
//        viewHolder.setText(R.id.tvEnergyCost, mContext.getString(R.string.kcalPlaceholder, String.valueOf(totalEnergyCost)));
    }

    public static Typeface getTypeface(Context context){
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RussoOne-Regular.ttf");
        }
        return typeface;
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        DLOG.d(TAG, "onItemClick");
        DLOG.d(TAG, "position:" + position);
        DLOG.d(TAG, "id:" + id);
        DLOG.d(TAG, "getDataList().get(position).historySportEntryList.get(0):" + getDataList().get(position).historySportEntryList.get(0));
        //        SportResultActivity.start(mContext, getDataList().get(position).historySportEntryList.get());
    }
}
