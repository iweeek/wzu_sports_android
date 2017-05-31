package com.tim.app.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.application.library.widget.roundimg.RoundedImageView;
import com.tim.app.R;
import com.tim.app.constant.AppKey;
import com.tim.app.server.entry.RankData;
import com.tim.app.util.BitmapLoader;

import java.util.List;

/**
 * 排行榜
 */
public class RankDataAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, RankData> {

    private Context mContext;
    private int type;

    public RankDataAdapter(Context mContext, List<RankData> mDataList, int type) {
        super(mDataList);
        this.mContext = mContext;
        this.type = type;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder
                holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_rank_data, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder mHolder, int position, RankData data) {
        if (data == null) {
            return;
        }
        final ViewHolder holder = (ViewHolder) mHolder;

        if (data.getCostValue() > 0) {
            holder.tvCostNumber.setText(String.valueOf(data.getCostValue()));
        }
        if (AppKey.TYPE_COST_TIME == type) {
            holder.tvCostUnit.setText("分钟");
        } else if (AppKey.TYPE_COST_ENERGY == type) {
            holder.tvCostUnit.setText("千卡");
        }
        if (!TextUtils.isEmpty(data.getUserName())) {
            holder.tvName.setText(data.getUserName());
        }
        if (!TextUtils.isEmpty(data.getAvatar())) {
            BitmapLoader.ins().loadImage(data.getAvatar(), R.drawable.ic_default_avatar, holder.rivAvatar);
        }
        holder.tvNo.setText(String.valueOf(position + 4));
        if (position != getDataList().size() - 1) {
            holder.vLine.setVisibility(View.VISIBLE);
        } else {
            holder.vLine.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        TextView tvCostNumber;
        TextView tvCostUnit;
        TextView tvNo;
        RoundedImageView rivAvatar;
        TextView tvName;
        View vLine;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCostNumber = (TextView) itemView.findViewById(R.id.tvCostNumber);
            tvCostUnit = (TextView) itemView.findViewById(R.id.tvCostUnit);
            tvNo = (TextView) itemView.findViewById(R.id.tvNo);
            rivAvatar = (RoundedImageView) itemView.findViewById(R.id.rivAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            vLine = (View) itemView.findViewById(R.id.vLine);
        }

    }

}
