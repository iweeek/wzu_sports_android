package com.tim.app.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.library.widget.RatioImageView;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.server.entry.Sport;
import com.tim.app.util.BitmapLoader;

import java.util.List;

public class SportAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, Sport> {
    private Context mContext;

    private boolean isShowSection;

    public SportAdapter(Context mContext, List<Sport> mDataList) {
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
                holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sport, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder mHolder, int position, Sport data) {
        if (data == null) {
            return;
        }
        final ViewHolder holder = (ViewHolder) mHolder;
        if (!TextUtils.isEmpty(data.getBgUrl())) {
            BitmapLoader.ins().loadImage(data.getBgUrl(), R.drawable.ic_def_empty, holder.rivSportBg);
        } else {
            holder.rivSportBg.setBackground(mContext.getResources().getDrawable(data.getBgDrawableId()));
        }

        if (!TextUtils.isEmpty(data.getTitle())) {
            holder.tvSportName.setText(data.getTitle());
        }
        if (data.getJoinNumber() > 0) {
            holder.tvSportJoinNumber.setText(mContext.getString(R.string.joinPrompt, String.valueOf(data.getJoinNumber())));
        }
        if (data.getTargetDistance() > 0) {
            holder.tvTargetDistance.setText(mContext.getString(R.string.targetDistance, String.valueOf(data.getTargetDistance())));
        }
        if (data.getTargetTime() > 0) {
            holder.tvTargetTime.setText(mContext.getString(R.string.targetTime, String.valueOf(data.getTargetTime())));
        }
        if (Sport.TYPE_FOUR == data.getType()) {
            holder.tvTargetTitle.setText(mContext.getString(R.string.targetTitleStep));
            holder.tvTargetValue.setText(mContext.getString(R.string.targetStep,String.valueOf(data.getSteps())));
        } else {
            holder.tvTargetTitle.setText(mContext.getString(R.string.targetTitleSpeed));
            holder.tvTargetValue.setText(mContext.getString(R.string.targetSpeed,data.getTargetSpeed()));
        }


    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        RatioImageView rivSportBg;
        TextView tvSportName;
        TextView tvSportJoinNumber;
        TextView tvTargetDistance;
        TextView tvTargetTime;
        TextView tvTargetTitle;
        TextView tvTargetValue;
        RelativeLayout rlContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            rivSportBg = (RatioImageView) itemView.findViewById(R.id.rivSportBg);
            tvSportName = (TextView) itemView.findViewById(R.id.tvSportName);
            tvSportJoinNumber = (TextView) itemView.findViewById(R.id.tvSportJoinNumber);
            tvTargetDistance = (TextView) itemView.findViewById(R.id.tvTargetDistance);
            tvTargetTime = (TextView) itemView.findViewById(R.id.tvTargetTime);
            tvTargetTitle = (TextView) itemView.findViewById(R.id.tvTargetTitle);
            tvTargetValue = (TextView) itemView.findViewById(R.id.tvTargetValue);
            rlContainer = (RelativeLayout) itemView.findViewById(R.id.rlContainer);
            rlContainer.setLayoutParams(new RelativeLayout.LayoutParams(RT.getScreenWidth(),(int)(RT.getScreenWidth()*0.43)));
        }

    }

}