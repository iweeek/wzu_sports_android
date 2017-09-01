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

    public void setData(RankingData data[], int type) {
        tvFirstName.setText(data[0].getUserName());
        tvSecondName.setText(data[1].getUserName());
        tvThirdName.setText(data[2].getUserName());

        if (!TextUtils.isEmpty(data[0].getAvatar())) {
            GlideApp.with(context)
                    .load(data[0].getAvatar())
                    .placeholder(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(rivFirstAvatar);
        }
        if (!TextUtils.isEmpty(data[1].getAvatar())) {
            GlideApp.with(context)
                    .load(data[1].getAvatar())
                    .placeholder(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(rivSecondAvatar);
        }
        if (!TextUtils.isEmpty(data[2].getAvatar())) {
            GlideApp.with(context)
                    .load(data[2].getAvatar())
                    .placeholder(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(rivThirdAvatar);
        }
        if (AppConstant.TYPE_COST_TIME == type) {
            tvFirst.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(data[0].getCostValue() / 60)));
            tvSecond.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(data[1].getCostValue() / 60)));
            tvThird.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(data[2].getCostValue() / 60)));

            String unit = " " + context.getString(R.string.minute);
            tvFirstUnit.setText(unit);
            tvSecondUnit.setText(unit);
            tvThirdUnit.setText(unit);
        } else {
            tvFirst.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(data[0].getCostValue())));
            tvSecond.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(data[1].getCostValue())));
            tvThird.setText(context.getString(R.string.digitalPlaceholder,String.valueOf(data[2].getCostValue())));

            String unit = " " + context.getString(R.string.kcal);
            tvFirstUnit.setText(unit);
            tvSecondUnit.setText(unit);
            tvThirdUnit.setText(unit);
        }
    }

}
