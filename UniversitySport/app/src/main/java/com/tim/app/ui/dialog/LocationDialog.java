package com.tim.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.application.library.log.DLOG;
import com.tim.app.R;
import com.tim.app.util.BrightnessUtil;

/**
 * @创建者 倪军
 * @创建时间 2017/8/29
 * @描述
 */

public class LocationDialog extends Dialog implements OnClickListener{
    private static final String TAG = "LocationDialog";
    private Context context;
    private int brightness;
    private boolean autoBrightness;

    public LocationDialog(@NonNull Context context) {
        super(context, R.style.CommonDialog);
        setContentView(R.layout.dialog_sport);
        this.context = context;
        brightness = BrightnessUtil.getScreenBrightness(context);
        autoBrightness = BrightnessUtil.isAutoBrightness(getContext());
        DLOG.d(TAG, "autoBrightness:" + autoBrightness);
    }

    @Override
    public void onClick(View v) {

    }

    public void dismissDialog() {
        if (this.isShowing()) {
            this.dismiss();
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        DLOG.d(TAG, "BrightnessUtil.getScreenBrightness(getWindow())" + BrightnessUtil.getScreenBrightness(getWindow()));
        int needToLight = Float.compare(BrightnessUtil.getScreenBrightness(getWindow()), 0.1f);

        if (needToLight == 0 || BrightnessUtil.getScreenBrightness(getWindow()) == -1) {
            if (autoBrightness) {
                BrightnessUtil.setScreenBrightness(getWindow(), 255);
            } else {
                BrightnessUtil.setScreenBrightness(getWindow(), this.brightness);
            }
        }
        return super.onTouchEvent(event);
    }
}
