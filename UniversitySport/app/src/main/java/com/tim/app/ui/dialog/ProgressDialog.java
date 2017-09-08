package com.tim.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;

import com.tim.app.R;

/**
 * @创建者 倪军
 * @创建时间 2017/9/8
 * @描述
 */

public class ProgressDialog extends Dialog {

    public ProgressDialog(@NonNull Context context) {
        super(context, R.style.CommonDialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0);
        setContentView(R.layout.dialog_progress);
    }

    //    @Override
    //    public boolean onTouchEvent(@NonNull MotionEvent event) {
    //        WindowManager.LayoutParams params = getWindow().getAttributes();
    //        params.screenBrightness = (float) 1;
    //        getWindow().setAttributes(params);
    //        return super.onTouchEvent(event);
    //    }
}
