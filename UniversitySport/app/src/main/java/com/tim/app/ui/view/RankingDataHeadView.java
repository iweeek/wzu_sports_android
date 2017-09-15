package com.tim.app.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.library.widget.roundimg.RoundedImageView;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
import com.tim.app.server.entry.RankingData;
import com.tim.app.ui.cell.GlideApp;

import java.util.List;

/**
 * 排行数据的headerview
 */
public class RankingDataHeadView extends LinearLayout {
    private Context context;

    RoundedImageView rivFirstAvatar;
    RoundedImageView rivSecondAvatar;
    RoundedImageView rivThirdAvatar;

    private TextView tvFirstName;
    private TextView tvSecondName;
    private TextView tvThirdName;

    private TextView tvFirst;
    private TextView tvSecond;
    private TextView tvThird;

    private TextView tvFirstUnit;
    private TextView tvSecondUnit;
    private TextView tvThirdUnit;

    public RankingDataHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rivFirstAvatar = (RoundedImageView) findViewById(R.id.rivFristAvatar);
        rivSecondAvatar = (RoundedImageView) findViewById(R.id.rivSecondAvatar);
        rivThirdAvatar = (RoundedImageView) findViewById(R.id.rivThirdAvatar);

        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvSecondName = (TextView) findViewById(R.id.tvSecondName);
        tvThirdName = (TextView) findViewById(R.id.tvThirdName);

        tvFirst = (TextView) findViewById(R.id.tvFirst);
        tvSecond = (TextView) findViewById(R.id.tvSecond);
        tvThird = (TextView) findViewById(R.id.tvThird);

        tvFirstUnit = (TextView) findViewById(R.id.tvFirstUnit);
        tvSecondUnit = (TextView) findViewById(R.id.tvSecondUnit);
        tvThirdUnit = (TextView) findViewById(R.id.tvThirdUnit);
    }

    public void setData(List<RankingData> dataList, int type) {
        for(int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i) != null) {

                String minuteUnit = " " + context.getString(R.string.minute);
                String kcalUnit = " " + context.getString(R.string.kcal);

                if (i == 0) {
                    tvFirstName.setText(dataList.get(i).getUserName());

                    if (!TextUtils.isEmpty(dataList.get(i).getAvatar())) {
                        GlideApp.with(context)
                                .load(dataList.get(i).getAvatar())
                                .placeholder(R.drawable.ic_default_avatar)
                                .circleCrop()
                                .into(rivFirstAvatar);
                    }

                    if (AppConstant.TYPE_COST_TIME == type) {
                        tvFirst.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(dataList.get(i).getCostValue() / 60)));
                        tvFirstUnit.setText(minuteUnit);
                    } else {
                        tvFirst.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(dataList.get(i).getCostValue())));
                        tvFirstUnit.setText(kcalUnit);
                    }
                }

                if (i == 1) {
                    tvSecondName.setText(dataList.get(i).getUserName());

                    if (!TextUtils.isEmpty(dataList.get(i).getAvatar())) {
                        GlideApp.with(context)
                                .load(dataList.get(i).getAvatar())
                                .placeholder(R.drawable.ic_default_avatar)
                                .circleCrop()
                                .into(rivSecondAvatar);
                    }

                    if (AppConstant.TYPE_COST_TIME == type) {
                        tvSecond.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(dataList.get(i).getCostValue() / 60)));
                        tvSecondUnit.setText(minuteUnit);
                    } else {
                        tvSecond.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(dataList.get(i).getCostValue())));
                        tvSecondUnit.setText(kcalUnit);
                    }
                }

                if (i == 2) {
                    tvThirdName.setText(dataList.get(i).getUserName());

                    if (!TextUtils.isEmpty(dataList.get(i).getAvatar())) {
                        GlideApp.with(context)
                                .load(dataList.get(i).getAvatar())
                                .placeholder(R.drawable.ic_default_avatar)
                                .circleCrop()
                                .into(rivThirdAvatar);
                    }

                    if (AppConstant.TYPE_COST_TIME == type) {
                        tvThird.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(dataList.get(i).getCostValue() / 60)));
                        tvThirdUnit.setText(minuteUnit);
                    } else {
                        tvThird.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(dataList.get(i).getCostValue())));
                        tvThirdUnit.setText(kcalUnit);
                    }
                }


            }
        }
    }
}
