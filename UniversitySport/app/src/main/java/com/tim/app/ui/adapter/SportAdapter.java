package com.tim.app.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.server.entry.SportEntry;
import com.tim.app.ui.adapter.viewholder.ViewHolder;
import com.tim.app.util.BitmapLoader;

import java.util.List;

public class SportAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, SportEntry> {
    private Context mContext;

    private boolean isShowSection;

    public SportAdapter(Context mContext, List<SportEntry> mDataList) {
        super(mDataList);
        this.mContext = mContext;
    }

    public boolean isShowSection() {
        return isShowSection;
    }

    public void setShowSection(boolean showSection) {
        isShowSection = showSection;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder
                holder = new ViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sport, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder mHolder, int position, SportEntry data) {
        if (data == null) {
            return;
        }

        final ViewHolder holder = (ViewHolder) mHolder;
        if (!TextUtils.isEmpty(data.getBgUrl())) {
//            BitmapLoader.ins().loadImage(data.getBgUrl(), R.drawable.ic_def_empty, holder.rivSportBg);
            BitmapLoader.ins().loadImage(data.getBgUrl(), R.drawable.ic_def_empty, (ImageView) holder.findView(R.id.rivSportBg));
        } else {
//            holder.rivSportBg.setBackground(mContext.getResources().getDrawable(data.getBgDrawableId()));
            holder.setBackground(R.id.rivSportBg,mContext.getResources().getDrawable(data.getBgDrawableId()));
        }

        if (!TextUtils.isEmpty(data.getSportName())) {
            //  之前的          holder.tvSportName.setText();
            //  初步改进          ((TextView)holder.findView(R.id.tvSportName)).setText(data.getSportName());
            //最终
            holder.setText(R.id.tvSportName, data.getSportName());
        }

        if (SportEntry.RUNNING_SPORT == data.getType()) {

            if (data.getParticipantNum() > 0) {
//                holder.tvSportJoinNumber.setText(mContext.getString(R.string.joinPrompt, String.valueOf(data.getParticipantNum())));
                holder.setText(R.id.tvSportJoinNumber,mContext.getString(R.string.joinPrompt, String.valueOf(data.getParticipantNum())));
            }


            if (data.getTargetDistance() > 0) {
                /** TODO: 这里 {@link R.string.percent}    需要改掉，统一使用 %s米  */
                //                holder.tvTargetDistance.setText(mContext.getString(R.string.percent, String.valueOf(data.getTargetDistance())) + "米");
                holder.setText(R.id.tvTargetDistance,mContext.getString(R.string.percent, String.valueOf(data.getTargetDistance())) + "米");
            }
            if (data.getTargetTime() > 0) {
//                holder.tvTargetTime.setText(mContext.getString(R.string.percent, String.valueOf(data.getTargetTime())) + "分");
                holder.setText(R.id.tvTargetTime,mContext.getString(R.string.percent, String.valueOf(data.getTargetTime())) + "分");
            }

            if(!data.getTargetSpeed().equals("")||data.getTargetSpeed() != null){
//                holder.tvTargetValue.setText(mContext.getString(R.string.percent, data.getTargetSpeed()) + "米/秒");
                holder.setText(R.id.tvTargetValue,mContext.getString(R.string.percent, data.getTargetSpeed()) + "米/秒");
            }
        } else if (SportEntry.AREA_SPORT == data.getType()) {
//            holder.llBottom.setVisibility(View.INVISIBLE);
            holder.setVisible(R.id.llBottom, false);
        }
    }


}
