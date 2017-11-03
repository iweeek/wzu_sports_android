package com.tim.app.ui.activity;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.tim.app.R;

public class LoadingActivity extends AppCompatActivity {

    private LinearLayout layoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        layoutView= (LinearLayout) findViewById(R.id.ll_root);
        layoutView.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                // 圆形动画的x坐标  位于View的中心
                //int cx = (layoutView.getLeft() + layoutView.getRight()) / 2;

                int cx = 520;
                int cy = 1300;

                //圆形动画的y坐标  位于View的中心
                //int cy = (layoutView.getTop() + layoutView.getBottom()) / 2;

                //起始大小半径
                //float startX=100f;
                float startX=150f;

                //结束大小半径 大小为图片对角线的一半
                float startY= (float) Math.sqrt(cx*cx+cy*cy);
                Animator animator= ViewAnimationUtils.createCircularReveal(layoutView,cx,cy,startX,startY);

                //在动画开始的地方速率改变比较慢,然后开始加速
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(500);
                animator.start();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        //layoutView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        });
    }
}
