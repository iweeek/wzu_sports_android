package com.tim.app.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.library.widget.roundimg.RoundedImageView;
import com.tim.app.R;
import com.tim.app.constant.AppKey;
import com.tim.app.util.BitmapLoader;

/**
 * 排行数据的headerview
 */
public class RankDataHeadView extends LinearLayout {

    RoundedImageView rivSecondAvatar;
    RoundedImageView rivFristAvatar;
    RoundedImageView rivThirdAvatar;

    private TextView tvSecondName;
    private TextView tvSecondUnit;
    private TextView tvFristName;
    private TextView tvFristUnit;
    private TextView tvThirdName;
    private TextView tvThirdUnit;

    public RankDataHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rivSecondAvatar = (RoundedImageView) findViewById(R.id.rivSecondAvatar);
        rivFristAvatar = (RoundedImageView) findViewById(R.id.rivFristAvatar);
        rivThirdAvatar = (RoundedImageView) findViewById(R.id.rivThirdAvatar);
        tvSecondName = (TextView) findViewById(R.id.tvSecondName);
        tvSecondUnit = (TextView) findViewById(R.id.tvSecondUnit);
        tvFristName = (TextView) findViewById(R.id.tvFristName);
        tvFristUnit = (TextView) findViewById(R.id.tvFristUnit);
        tvThirdName = (TextView) findViewById(R.id.tvThirdName);
        tvThirdUnit = (TextView) findViewById(R.id.tvThirdUnit);
    }

    /**
     * 刷新界面
     *
     * @param avart1
     * @param name1
     * @param value1
     * @param avart2
     * @param name2
     * @param value2
     * @param avart3
     * @param name3
     * @param value3
     * @param type
     */
    public void setData(String avart1, String name1, int value1, String avart2, String name2, int value2, String avart3, String name3, int value3, int type) {
        tvFristName.setText(name1);
        tvSecondName.setText(name2);
        tvThirdName.setText(name3);

        if (!TextUtils.isEmpty(avart1)) {
            BitmapLoader.ins().loadImage(avart1, R.drawable.ic_default_avatar, rivFristAvatar);
        }
        if (!TextUtils.isEmpty(avart2)) {
            BitmapLoader.ins().loadImage(avart2, R.drawable.ic_default_avatar, rivSecondAvatar);
        }
        if (!TextUtils.isEmpty(avart3)) {
            BitmapLoader.ins().loadImage(avart3, R.drawable.ic_default_avatar, rivThirdAvatar);
        }
        if (AppKey.TYPE_COST_TIME == type) {
            tvFristUnit.setText(value1 +"分钟");
            tvSecondUnit.setText(value2 +"分钟");
            tvThirdUnit.setText(value3 +"分钟");
        } else {
            tvFristUnit.setText(value1 +"千卡");
            tvSecondUnit.setText(value2 +"千卡");
            tvThirdUnit.setText(value3 +"千卡");
        }
    }

}
