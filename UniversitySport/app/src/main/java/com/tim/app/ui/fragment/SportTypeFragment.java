package com.tim.app.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.library.base.BaseFragment;
import com.bumptech.glide.request.RequestOptions;
import com.tim.app.R;
import com.tim.app.server.entry.SportEntry;
import com.tim.app.ui.activity.MainActivity;
import com.tim.app.ui.activity.SportDetailActivity;
import com.tim.app.ui.activity.SportsAreaListActivity;
import com.tim.app.ui.cell.GlideApp;

public class SportTypeFragment extends BaseFragment {
    Context mContext;

    SportEntry data;

    String SportName;


    //    public Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//                case 1:
//
//                    break;
//            }
//        }
//    };
//

    public void setSportEntry(SportEntry data) {
        this.data = data;
    }

    public static SportTypeFragment newInstance(SportEntry sportEntry) {
        SportTypeFragment fragment = new SportTypeFragment();
        fragment.setSportEntry(sportEntry);
//      Bundle args = new Bundle();
//      args.putString("SportName", SportName);
//      fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sport_type, container, false);

        /**
         * 显示相关数据时出错，提示为java.lang.IllegalStateException: FragmentManager is already executing transactions
         */
        ImageView rivSportBg = (ImageView) rootView.findViewById(R.id.rivSportBg);
        ImageView btStart = (ImageView) rootView.findViewById(R.id.btStart);
        TextView tvParticipantNum = (TextView) rootView.findViewById(R.id.tvParticipantNum);
        TextView tvDistance = (TextView) rootView.findViewById(R.id.TvDistance);
        TextView tvTargetValue = (TextView) rootView.findViewById(R.id.tvTargetValue);
        View llBottom = rootView.findViewById(R.id.llBottom);
//        GlideApp.with(getContext()).load(R.drawable.font_go).dontAnimate().into(btStart);
        if (SportEntry.RUNNING_SPORT == data.getType()) {
            GlideApp.with(getContext())
                    .load(R.drawable.sport_running)
                    .into(rivSportBg);

            tvParticipantNum.setText("当前" + data.getParticipantNum() + "人正在参加");
            tvDistance.setText(data.getQualifiedDistance() + "米");
            tvTargetValue.setText(data.getTargetSpeed() + "米/秒");
        } else if (SportEntry.AREA_SPORT == data.getType()) {

            llBottom.setVisibility(View.INVISIBLE);
            //rivSportBg.setImageResource(R.drawable.sport_area);
            GlideApp.with(getContext())
                    .load(R.drawable.sport_area)
                    .into(rivSportBg);

        }

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getType() == SportEntry.RUNNING_SPORT) {
                    SportDetailActivity.start(getActivity(), data);
                } else {
                    SportsAreaListActivity.start(getActivity(), data);
                }
            }
        });
        return rootView;
    }

}