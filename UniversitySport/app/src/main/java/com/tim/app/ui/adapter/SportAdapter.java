package com.tim.app.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.RT;
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
        BaseRecyclerViewHolder holder = null;
        if (viewType == SportEntry.RUNNING_SPORT) {
            holder = new ViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sport, null));
        }else if(viewType == SportEntry.AREA_SPORT){
            holder = new ViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sport, null));
            holder.findView(R.id.llBottom).setVisibility(View.GONE);
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position, SportEntry data) {
        if (data.getType() == SportEntry.RUNNING_SPORT) {
            return SportEntry.RUNNING_SPORT;
        } else if (data.getType() == SportEntry.AREA_SPORT) {
            return SportEntry.AREA_SPORT;
        }
        return position;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder mHolder, int position, SportEntry data) {
        if (data == null) {
            return;
        }

        final ViewHolder holder = (ViewHolder) mHolder;

        holder.setLayoutParams(R.id.rlContainer, new RelativeLayout.LayoutParams(RT.getScreenWidth(), (int) (RT.getScreenWidth() * 0.43)));

        if (!TextUtils.isEmpty(data.getBgUrl())) {
            BitmapLoader.ins().loadImage(data.getBgUrl(), R.drawable.ic_def_empty, (ImageView) holder.findView(R.id.rivSportBg));
        } else {
            holder.setBackground(R.id.rivSportBg, mContext.getResources().getDrawable(data.getBgDrawableId()));
        }

        if (!TextUtils.isEmpty(data.getName())) {
            holder.setText(R.id.tvSportName, data.getName());
        }

        if (SportEntry.RUNNING_SPORT == data.getType()) {

            //            if (data.getParticipantNum() > 0) {
            holder.setText(R.id.tvParticipantNum, mContext.getString(R.string.joinPrompt, String.valueOf(data.getParticipantNum())));
            //            }else{
            //                holder.setText(R.id.tvParticipantNum, "当前没有人正在进行运动");
            //            }

            if (data.getQualifiedDistance() > 0) {
                holder.setText(R.id.tvTargetDistance, mContext.getString(R.string.digitalPlaceholder, String.valueOf(data.getQualifiedDistance())) + "米");
            }
            //            if (data.getTargetTime() > 0) {
            //                holder.setText(R.id.tvTargetTime, mContext.getString(R.string.digitalPlaceholder, String.valueOf(data.getTargetTime())) + "分");
            //            }

            if (!data.getTargetSpeed().equals("") || data.getTargetSpeed() != null) {
                holder.setText(R.id.tvTargetValue, mContext.getString(R.string.digitalPlaceholder, data.getTargetSpeed()) + "米/秒");
            }
        } else if (SportEntry.AREA_SPORT == data.getType()) {
//            holder.setVisible(R.id.llBottom, false);
        }
    }


}
