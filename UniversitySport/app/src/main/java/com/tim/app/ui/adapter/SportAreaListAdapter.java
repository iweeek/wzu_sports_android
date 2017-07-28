package com.tim.app.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.library.log.DLOG;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.server.entry.SportArea;

import java.util.List;


public class SportAreaListAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, SportArea>
        implements BaseRecyclerAdapter.OnItemClickListener {

    private Context mContext;
    private static final String TAG = "SportAreaListAdapter";

    public SportAreaListAdapter(Context mContext, List<SportArea> mDataList) {
        super(mDataList);
        this.mContext = mContext;

        this.setOnItemClickListener(this);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder
                holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sport_area, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder mHolder, int position, SportArea data) {
        if (data == null) {
            return;
        }
        final ViewHolder holder = (ViewHolder) mHolder;

        if (!TextUtils.isEmpty(data.getAreaName())) {
            holder.tvAreaName.setText(data.getAreaName());
        }

        if (!TextUtils.isEmpty(data.getAddress())) {
            holder.tvAddress.setText(data.getAddress());
        }

        holder.tvTargetTime.setText(mContext.getString(R.string.targetTime, String.valueOf(data.getTargetTime())));
        if (data.isSelected()) {
            holder.ivSelectIcon.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelectIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        DLOG.d(TAG, "onItemClick");

    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        TextView tvAreaName;
        TextView tvTargetTime;
        TextView tvAddress;

        ImageView ivSelectIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAreaName = (TextView) itemView.findViewById(R.id.tvAreaName);
            tvTargetTime = (TextView) itemView.findViewById(R.id.tvTargetTime);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            ivSelectIcon = (ImageView) itemView.findViewById(R.id.ivSelectIcon);
        }

    }

}
