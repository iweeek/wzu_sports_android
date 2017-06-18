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
import com.tim.app.util.BitmapLoader;

/**
 * 排行数据的headerview
 */
public class RankingDataHeadView extends LinearLayout {

    RoundedImageView rivFirstAvatar;
    RoundedImageView rivSecondAvatar;
    RoundedImageView rivThirdAvatar;

    private TextView tvSecondName;
    private TextView tvSecondUnit;

    private TextView tvFirstName;
    private TextView tvFirstUnit;

    private TextView tvThirdName;
    private TextView tvThirdUnit;

    public RankingDataHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rivFirstAvatar = (RoundedImageView) findViewById(R.id.rivFristAvatar);
        rivSecondAvatar = (RoundedImageView) findViewById(R.id.rivSecondAvatar);
        rivThirdAvatar = (RoundedImageView) findViewById(R.id.rivThirdAvatar);

        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvFirstUnit = (TextView) findViewById(R.id.tvFirstUnit);
        tvSecondName = (TextView) findViewById(R.id.tvSecondName);

        tvThirdName = (TextView) findViewById(R.id.tvThirdName);
        tvSecondUnit = (TextView) findViewById(R.id.tvSecondUnit);
        tvThirdUnit = (TextView) findViewById(R.id.tvThirdUnit);
    }

    public void setData(RankingData data[], int type) {
        tvFirstName.setText(data[0].getUserName());
        tvSecondName.setText(data[1].getUserName());
        tvThirdName.setText(data[2].getUserName());

        if (!TextUtils.isEmpty(data[0].getAvatar())) {
            BitmapLoader.ins().loadImage(data[0].getAvatar(), R.drawable.ic_default_avatar, rivFirstAvatar);
        }
        if (!TextUtils.isEmpty(data[1].getAvatar())) {
            BitmapLoader.ins().loadImage(data[1].getAvatar(), R.drawable.ic_default_avatar, rivSecondAvatar);
        }
        if (!TextUtils.isEmpty(data[2].getAvatar())) {
            BitmapLoader.ins().loadImage(data[2].getAvatar(), R.drawable.ic_default_avatar, rivThirdAvatar);
        }
        if (AppConstant.TYPE_COST_TIME == type) {
            tvFirstUnit.setText(data[0].getCostValue() +"分钟");
            tvSecondUnit.setText(data[1].getCostValue() +"分钟");
            tvThirdUnit.setText(data[2].getCostValue() +"分钟");
        } else {
            tvFirstUnit.setText(data[0].getCostValue() +"千卡");
            tvSecondUnit.setText(data[1].getCostValue() +"千卡");
            tvThirdUnit.setText(data[2].getCostValue() +"千卡");
        }
    }

}
