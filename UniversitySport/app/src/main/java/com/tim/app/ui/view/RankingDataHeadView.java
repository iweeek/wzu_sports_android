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

    /**
     * 刷新界面
     *
     * @param firstAvatar
     * @param firstName
     * @param firstValue
     * @param secondAvatar
     * @param secondName
     * @param secondValue
     * @param thirdAvatar
     * @param thirdName
     * @param thirdValue
     * @param type
     */
    public void setData(String firstAvatar, String firstName, int firstValue, String secondAvatar,
                        String secondName, int secondValue, String thirdAvatar, String thirdName, int thirdValue, int type) {
        tvFirstName.setText(firstName);
        tvSecondName.setText(secondName);
        tvThirdName.setText(thirdName);

        if (!TextUtils.isEmpty(firstAvatar)) {
            BitmapLoader.ins().loadImage(firstAvatar, R.drawable.ic_default_avatar, rivFirstAvatar);
        }
        if (!TextUtils.isEmpty(secondAvatar)) {
            BitmapLoader.ins().loadImage(secondAvatar, R.drawable.ic_default_avatar, rivSecondAvatar);
        }
        if (!TextUtils.isEmpty(thirdAvatar)) {
            BitmapLoader.ins().loadImage(thirdAvatar, R.drawable.ic_default_avatar, rivThirdAvatar);
        }
        if (AppConstant.TYPE_COST_TIME == type) {
            tvFirstUnit.setText(firstValue +"分钟");
            tvSecondUnit.setText(secondValue +"分钟");
            tvThirdUnit.setText(thirdValue +"分钟");
        } else {
            tvFirstUnit.setText(firstValue +"千卡");
            tvSecondUnit.setText(secondValue +"千卡");
            tvThirdUnit.setText(thirdValue +"千卡");
        }
    }

}
