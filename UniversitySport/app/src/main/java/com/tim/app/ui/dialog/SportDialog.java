package com.tim.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;

import com.tim.app.R;

/**
 * @创建者 倪军
 * @创建时间 2017/8/29
 * @描述
 */

public class SportDialog extends Dialog implements OnClickListener{
    public SportDialog(@NonNull Context context) {
        super(context, R.style.CommonDialog);

        setContentView(R.layout.dialog_sport);

    }

    @Override
    public void onClick(View v) {

    }
}
