package com.tim.app.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tim.app.R;


public class TipDialog extends Dialog implements View.OnClickListener {

    private DialogButtonOnClickListener listener_1, listener_2;
    private Context context;
    private TextView tv_des;
    private View line_1;
    private Button btn_1;
    private View line_2;
    private Button btn_2;
    private TextView tvPromptTitle;
    private Button ripple_btn1, ripple_btn2;

    public TipDialog(Context context) {
        super(context, R.style.CommonDialog);
        setCanceledOnTouchOutside(true);
        getWindow().getAttributes().width = -1;
        getWindow().getAttributes().height = -2;
        getWindow().getAttributes().y = 0;
        getWindow().setGravity(Gravity.CENTER_VERTICAL);
        getWindow().setAttributes(getWindow().getAttributes());
        if (context instanceof Activity)
            setOwnerActivity((Activity) context);
        this.context = context;

        setContentView(R.layout.dialog_tip);
        tv_des = (TextView) findViewById(R.id.tv_des);
        tvPromptTitle = (TextView) findViewById(R.id.tvPromptTitle);
        line_1 = findViewById(R.id.line_1);
        btn_1 = (Button) findViewById(R.id.btn_1);
        line_2 = findViewById(R.id.line_2);
        btn_2 = (Button) findViewById(R.id.btn_2);
        ripple_btn1 = (Button) findViewById(R.id.btn_1);
        ripple_btn2 = (Button) findViewById(R.id.btn_2);
        tvPromptTitle.setVisibility(View.GONE);
    }

    public void setTextDes(String text) {
        tv_des.setText(text);
    }

    public void setContentTextSize(float size) {
        tv_des.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setTextDesColor(int textColor) {
        tv_des.setTextColor(textColor);
    }

    public void setButton1Color(int textColor) {
        btn_1.setTextColor(textColor);
    }

    public void setButton1TextSize(float textSize) {
        btn_1.setTextSize(textSize);
    }

    public void setButton2TextSize(float textSize) {
        btn_2.setTextSize(textSize);
    }

    public void setButton2Color(int textColor) {
        btn_2.setTextColor(textColor);
    }

    public void setButton1(String text, DialogButtonOnClickListener clickListener) {
        this.btn_1.setText(text);
        this.ripple_btn1.setVisibility(View.VISIBLE);
        this.listener_1 = clickListener;
        this.ripple_btn1.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
        fixlayout();
    }

    public void setNoPomptTitle() {
        tvPromptTitle.setVisibility(View.INVISIBLE);
    }

    public void setPromptTitle(String promptTitle) {
        tvPromptTitle.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(promptTitle)) {
            tvPromptTitle.setText(promptTitle);
        } else {
            tvPromptTitle.setText("提示");
        }
    }

    public void setButton2(String text, DialogButtonOnClickListener clickListener) {
        this.btn_2.setText(text);
        this.ripple_btn2.setVisibility(View.VISIBLE);
        this.listener_2 = clickListener;
        this.ripple_btn2.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
        fixlayout();
    }

    private void fixlayout() {
        if (ripple_btn1.getVisibility() == View.VISIBLE && ripple_btn2.getVisibility() == View.GONE) {
            // 显示btn1 隐藏btn2
            line_1.setVisibility(View.VISIBLE);
            line_2.setVisibility(View.GONE);

        } else if (ripple_btn1.getVisibility() == View.GONE && ripple_btn2.getVisibility() == View.VISIBLE) {
            // 显示btn2 隐藏btn1
            line_1.setVisibility(View.VISIBLE);
            line_2.setVisibility(View.GONE);

        } else if (ripple_btn1.getVisibility() == View.VISIBLE && ripple_btn2.getVisibility() == View.VISIBLE) {
            // 两个btn都显示
            line_1.setVisibility(View.VISIBLE);
            line_2.setVisibility(View.VISIBLE);

        } else {
            // 都隐藏
            line_1.setVisibility(View.GONE);
            line_2.setVisibility(View.GONE);
            ripple_btn1.setVisibility(View.GONE);
            ripple_btn2.setVisibility(View.GONE);
        }
    }

    public interface DialogButtonOnClickListener {
        void onClick(View button, TipDialog dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                if (listener_1 != null) {
                    listener_1.onClick(v, this);
                }
                break;
            case R.id.btn_2:
                if (listener_2 != null) {
                    listener_2.onClick(v, this);
                }
                break;
            default:
                break;
        }
    }
}
