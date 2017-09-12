package com.tim.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.tim.app.R;

/**
 * @创建者 倪军
 * @创建时间 2017/8/29
 * @描述
 */

public class LocationDialog extends Dialog implements OnClickListener{
    public LocationDialog(@NonNull Context context) {
        super(context, R.style.CommonDialog);

        setContentView(R.layout.dialog_sport);

    }

    @Override
    public void onClick(View v) {

    }

    public void dismissDialog(){
        if(this.isShowing()){
            this.dismiss();
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = (float) 1;
        getWindow().setAttributes(params);
        return super.onTouchEvent(event);
    }
}
