package com.tim.app.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
//        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
//
//        int width = wm.getDefaultDisplay().getWidth();
//        int height = wm.getDefaultDisplay().getHeight();
//
//        ProgressMan.setX(width);

    }

    public static Typeface getTypeface(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RussoOne-Regular.ttf");
        }
        return typeface;
    }

}
