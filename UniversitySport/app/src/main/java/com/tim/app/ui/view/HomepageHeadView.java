package com.tim.app.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tim.app.R;
import com.tim.app.ui.activity.HistorySportActivity;
import com.tim.app.ui.activity.SchoolRankingActivity;

/**
 * 首页的headerview
 */
public class HomepageHeadView extends LinearLayout implements View.OnClickListener {

    private TextView tvCurTermAccuTimes;
    //    private TextView tvAccumulCostEnergy;
//    private TextView tvAccumulCostTime;
    private TextView tvCurSignInCount;
    private TextView tvCurTermTargetTimes;
    private ImageView ProgressMan;
    private static Typeface typeface;

    private RelativeLayout rlTop;
    //    private RelativeLayout rlRank;
    private LinearLayout rlHeadView;
    //    private RelativeLayout rlSecond;
//    private LinearLayout llBadNetworkFresh;
    private Context ctx;
    private ProgressBar pbReachTargetTimes;

    private Handler mHandler;

    private int MSG = 1;
    private int max = 100;
    private int pro = 0;

    private int width;
    private int w = 180;

    public HomepageHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvCurTermAccuTimes = (TextView) findViewById(R.id.tvCurTermAccuTimes);
//        tvAccumulCostEnergy = (TextView) findViewById(R.id.tvAccumulCostEnergy);
//        tvAccumulCostTime = (TextView) findViewById(R.id.tvAccumulCostTime);
        tvCurSignInCount = (TextView) findViewById(R.id.tvCurSignInCount);
        tvCurTermTargetTimes = (TextView) findViewById(R.id.tvCurTermTargetTimes);
        pbReachTargetTimes = (ProgressBar) findViewById(R.id.pbReachTargetTimes);

//        rlSecond = (RelativeLayout) findViewById(R.id.rlSecond);
//        rlSecond.setOnClickListener(this);
        rlHeadView = (LinearLayout) findViewById(R.id.llHeadView);
        rlHeadView.setOnClickListener(this);
//        rlRank = (RelativeLayout) findViewById(R.id.rlRank);
//        rlRank.setOnClickListener(this);
//        rlTop = (RelativeLayout) findViewById(R.id.rlTop);
//        rlTop.setOnClickListener(this);

        ProgressMan = (ImageView) findViewById(R.id.ProgressMan);

//        llBadNetworkFresh = (LinearLayout) findViewById(R.id.llBadNetworkFresh);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlRank:
                Intent intent = new Intent(getContext(), SchoolRankingActivity.class);
                getContext().startActivity(intent);
                break;
//            case R.id.rlTop:
//                getContext().startActivity(new Intent(getContext(), HistorySportActivity.class));
//                break;
//            case R.id.rlSecond:
//                getContext().startActivity(new Intent(getContext(), HistorySportActivity.class));
//                break;
            case R.id.llHeadView:
                getContext().startActivity(new Intent(getContext(), HistorySportActivity.class));
                break;
        }
    }

    public void setLayoutVisible(int id, boolean isVisible) {
        ViewGroup vg = (ViewGroup) findViewById(id);
        if (isVisible) {
            vg.setVisibility(View.VISIBLE);
        } else {
            vg.setVisibility(View.INVISIBLE);
        }
    }

    public void displayBadNetworkLayout() {
        rlHeadView.setVisibility(View.INVISIBLE);
//        llBadNetworkFresh.setVisibility(View.VISIBLE);
//        llBadNetworkFresh.setOnClickListener((MainActivity) ctx);
    }

    public void displayNormalLayout() {
        rlHeadView.setVisibility(View.VISIBLE);
//        llBadNetworkFresh.setVisibility(View.INVISIBLE);
    }

    /**
     * 刷新界面
     */
    public void setData(String curTermSportCount, String KcalComsuption, String costedTime,
                        String curTermSignInCount, String curTermTargetCount) {
        tvCurTermAccuTimes.setText(Html.fromHtml("本学期累计运动<b><font color='#314666'> " + curTermSportCount + " </font></b>次"));
//        tvAccumulCostEnergy.setText(getContext().getString(R.string.digitalPlaceholder, KcalComsuption));
//        tvAccumulCostTime.setText((getContext().getString(R.string.digitalPlaceholder, costedTime)));
        tvCurSignInCount.setText((getContext().getString(R.string.digitalPlaceholder, curTermSignInCount)));
        tvCurTermTargetTimes.setText((getContext().getString(R.string.digitalPlaceholder, curTermTargetCount)));
        tvCurSignInCount.setTypeface(getTypeface(getContext()), Typeface.BOLD);  //设置字体
        tvCurTermTargetTimes.setTypeface(getTypeface(getContext()), Typeface.BOLD);  //设置字体

        float q = Float.valueOf(curTermSignInCount);
        float t = Float.valueOf(curTermTargetCount);
        float r = q / t;

        pbReachTargetTimes.setProgress((int) (r * 100));
////180 60

        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();

        max = (int) (r * 100);
        Log.e("haha", "setData: --------------->"+max);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //处理消息
                switch (msg.what) {
                    case 1:
                        //设置滚动条和text的值
                        pbReachTargetTimes.setProgress(pro);
                        break;
                    default:
                        break;
                }
            }
        };
        start();

    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //子线程循环间隔消息
                    while (pro < max) {
                        pro += 1;
                        w += (width - 180) / 100;
                        ProgressMan.setX((float) w);
                        Message msg = new Message();
                        msg.what = MSG;
                        mHandler.sendMessage(msg);
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Typeface getTypeface(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RussoOne-Regular.ttf");
        }
        return typeface;
    }

}
