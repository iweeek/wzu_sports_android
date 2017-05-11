package com.application.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.application.library.R;


public class LoadingDialog extends Dialog {
    private TextView tv_text;
//    private ImageView iv_loading;
//    private AnimationDrawable loading_anim;

    public LoadingDialog(Context context) {
        super(context, R.style.dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress_layout, null);
//        iv_loading = (ImageView) view.findViewById(R.id.iv_loading);
        tv_text = (TextView) view.findViewById(R.id.tv_text);
        setContentView(view);
//        loading_anim = (AnimationDrawable) iv_loading.getBackground();
    }

    public void setContent(String content) {
        if (TextUtils.isEmpty(content)) {
            tv_text.setVisibility(View.GONE);
        } else {
            tv_text.setVisibility(View.VISIBLE);
            tv_text.setText(content);
        }
    }

    public void setImageBackground(int background) {
//        iv_loading.setBackgroundResource(background);
//        loading_anim = (AnimationDrawable) iv_loading.getBackground();
    }

    @Override
    public void show() {
        super.show();
//        if (loading_anim != null && !loading_anim.isRunning()) {
//            loading_anim.start();
//        }
    }

    @Override
    public void dismiss() {
//        if (loading_anim != null && loading_anim.isRunning()) {
//            loading_anim.stop();
//        }
        super.dismiss();
    }
}
