package com.tim.app.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.tim.app.R;
import com.tim.app.server.entry.Score;

import java.util.List;

/**
 * 成绩
 */
public class ScoreDataAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, Score> {

    private Context mContext;

    public ScoreDataAdapter(Context mContext, List<Score> mDataList) {
        super(mDataList);
        this.mContext = mContext;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder
                holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_score, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder mHolder, int position, Score data) {
        if (data == null) {
            return;
        }
        final ViewHolder holder = (ViewHolder) mHolder;

        if (!TextUtils.isEmpty(data.getSportDesc())) {
            holder.tvSportType.setText(data.getSportDesc());
        }
        if (!TextUtils.isEmpty(data.getScoreDesc())) {
            holder.tvScoreDesc.setText(mContext.getString(R.string.scoreDesc, data.getScoreDesc()));
        }

        if (data.getScore() >0) {
            holder.tvScore.setText(mContext.getString(R.string.score, String.valueOf(data.getScore())));
        }
    }

    public class ViewHolder extends BaseRecyclerViewHolder {

        TextView tvSportType;
        TextView tvScoreDesc;
        TextView tvScore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSportType = (TextView) itemView.findViewById(R.id.tvSportType);
            tvScoreDesc = (TextView) itemView.findViewById(R.id.tvScoreDesc);
            tvScore = (TextView) itemView.findViewById(R.id.tvScore);
        }

    }

}
