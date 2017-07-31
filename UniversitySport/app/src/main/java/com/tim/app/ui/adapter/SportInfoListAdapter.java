package com.tim.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.library.log.DLOG;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.server.entry.SportInfo;

import java.util.List;


public class SportInfoListAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, SportInfo>
        implements BaseRecyclerAdapter.OnItemClickListener {

    private Context mContext;
    private static final String TAG = "SportAreaListAdapter";

    public SportInfoListAdapter(Context mContext, List<SportInfo> mDataList) {
        super(mDataList);
        this.mContext = mContext;

        this.setOnItemClickListener(this);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder
                holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sport_info, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder mHolder, int position, SportInfo data) {
        if (data == null) {
            return;
        }
        final ViewHolder holder = (ViewHolder) mHolder;

        if (!TextUtils.isEmpty(data.getFeild())) {
            holder.tvSportField.setText(data.getFeild());
        }

        if (!TextUtils.isEmpty(data.getDesc())) {
            holder.tvDesc.setText(data.getDesc());
        }

        holder.tvTargetTime.setText(mContext.getString(R.string.targetTime, String.valueOf(data.getQualifiedCostTime())));
        holder.tvSportCount.setText(mContext.getString(R.string.joinPrompt, String.valueOf(data.getSportCount())));
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        DLOG.d(TAG, "onItemClick");

    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        TextView tvSportField;
        TextView tvDesc;
        TextView tvTargetTime;

        ImageView ivConsultation;
        TextView tvSportCount;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RT.getScreenWidth() - (int) RT.getDensity() * 40, RecyclerView.LayoutParams.WRAP_CONTENT));
            tvSportField = (TextView) itemView.findViewById(R.id.tvSportField);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            tvTargetTime = (TextView) itemView.findViewById(R.id.tvTargetTime);
            ivConsultation = (ImageView) itemView.findViewById(R.id.ivConsultation);
            tvSportCount = (TextView) itemView.findViewById(R.id.tvSportCount);
        }

    }

}
