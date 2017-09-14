package com.application.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.library.R;
import com.application.library.util.PhoneUtil;


public class EmptyLayout {
    public static final int STATE_LOADING = 1;
    public static final int STATE_EMPTY = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_CONTENT = 4;

    private Context mContext;
    //意外情况显示的视图。
    private FrameLayout mBackgroundView;
    //下面三个都是 mBackgroundView 的子类
    private ViewGroup mLoadingView;
    private ViewGroup mEmptyView;
    private ViewGroup mErrorView;
    //正常视图
    private View mContentView;
    private ImageView iv_loading, iv_empty, iv_error;
    private TextView tv_empty, tv_error, tv_status;
    private Button btn_empty, btn_error;
    private View empty_temp_view, error_temp_view, loading_temp_view;
    private LayoutInflater mInflater;
    //    private AnimationDrawable loading_anim;
    private boolean mViewsAdded = false;
    private View.OnClickListener mEmptyButtonClickListener;
    private View.OnClickListener mErrorButtonClickListener;

    private int state = STATE_LOADING;
    private int bg_color = 0;
    private int emptyDrawable = 0;
    private int errorDrawable = 0;
    private int tempHeight = 0;
    private float lineSpacingExtra = 0;
    private String loadingStr;
    private String emptyStr;
    private String errorStr;
    private String emptyButtonTitle;
    private String errorButtonTitle;
    private boolean isShowEmptyButton = false;
    private boolean isShowErrorButton = false;
    private boolean isShowTempView = false;
    private int marginTop = 0;

    public EmptyLayout(Context context, View contentView) {
        this.mContext = context;
        this.mContentView = contentView;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initDefaultValues();
    }

    public void setLayoutParams(final ViewGroup.LayoutParams params) {
        changeState();
        mBackgroundView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mBackgroundView.setLayoutParams(params);
            }
        });
        // mBackgroundView.setLayoutParams(params);
    }

    public void setBackgroundColor(int color) {
        this.bg_color = color;
    }

    public void setLoadingText(String loadingStr) {
        this.loadingStr = loadingStr;
    }

    public void setEmptyText(String emptyStr) {
        this.emptyStr = emptyStr;
    }

    public void setErrorText(String errorStr) {
        this.errorStr = errorStr;
    }

    public void setEmptyDrawable(int drawable) {
        this.emptyDrawable = drawable;
    }

    public void setErrorDrawable(int drawable) {
        this.errorDrawable = drawable;
    }

    public void setLineSpacingExtra(float lineSpace) {
        this.lineSpacingExtra = lineSpace;
    }

    public void setEmptyButtonText(String emptyButtonTitle) {
        this.emptyButtonTitle = emptyButtonTitle;
    }

    public String getEmptyButtonText() {
        return emptyButtonTitle;
    }

    public void setErrorButtonText(String errorButtonTitle) {
        this.errorButtonTitle = errorButtonTitle;
    }

    public String getErrorButtonText() {
        return errorButtonTitle;
    }

    public void setEmptyButtonShow(boolean isShowEmptyButton) {
        this.isShowEmptyButton = isShowEmptyButton;
    }

    public void setErrorButtonShow(boolean isShowErrorButton) {
        this.isShowErrorButton = isShowErrorButton;
    }

    public void setTempViewShow(boolean isShowTempView) {
        this.isShowTempView = isShowTempView;
    }

    public void setTempViewShow(boolean isShowTempView, int tempHeight) {
        this.isShowTempView = isShowTempView;
        this.tempHeight = tempHeight;
    }

    public void setEmptyButtonClickListener(View.OnClickListener emptyButtonClickListener) {
        this.mEmptyButtonClickListener = emptyButtonClickListener;
        this.mErrorButtonClickListener = emptyButtonClickListener;
        if (btn_empty != null) {
            btn_empty.setOnClickListener(emptyButtonClickListener);
        }
        if (btn_error != null) {
            btn_error.setOnClickListener(emptyButtonClickListener);
        }
        if (mEmptyView != null) {
            mEmptyView.setOnClickListener(emptyButtonClickListener);
        }
        if (mErrorView != null) {
            mErrorView.setOnClickListener(emptyButtonClickListener);
        }
        if (iv_empty != null) {
            iv_empty.setOnClickListener(emptyButtonClickListener);
        }
        if (iv_error != null) {
            iv_error.setOnClickListener(emptyButtonClickListener);
        }
    }

    public void setErrorButtonClickListener(View.OnClickListener errorButtonClickListener) {
        this.mErrorButtonClickListener = errorButtonClickListener;
        this.mEmptyButtonClickListener = errorButtonClickListener;
        if (btn_error != null) {
            btn_error.setOnClickListener(errorButtonClickListener);
        }
        if (iv_error != null) {
            iv_error.setOnClickListener(errorButtonClickListener);
        }
        if (iv_empty != null) {
            iv_empty.setOnClickListener(errorButtonClickListener);
        }
    }

    public void showLoading() {
        this.state = STATE_LOADING;
        changeState();
    }

    public void showEmpty() {
        this.state = STATE_EMPTY;
        changeState();
    }

    public void showError() {
        this.state = STATE_ERROR;
        changeState();
    }

    public void showContent() {
        this.state = STATE_CONTENT;
        changeState();
    }

    public void showEmptyOrError(int errcode) {
        if (errcode == -1) {
            showError();
        } else {
            showEmpty();
        }
    }

    public void setMargin(int top) {
        this.marginTop = top;
    }

    /**
     * 初始化基本值
     */
    private void initDefaultValues() {
        bg_color = Color.TRANSPARENT;
        emptyDrawable = R.drawable.icon_default_network;
        errorDrawable = R.drawable.icon_default_network;
        loadingStr = mContext.getString(R.string.def_data_loading);
        emptyStr = mContext.getString(R.string.def_empty_data_text);
        errorStr = mContext.getString(R.string.def_net_error_text);
        emptyButtonTitle = mContext.getString(R.string.def_empty_button_retry);
        errorButtonTitle = mContext.getString(R.string.def_empty_button_retry);
        lineSpacingExtra = PhoneUtil.dipToPixel(5, mContext);
        isShowEmptyButton = false;
        isShowErrorButton = false;
        isShowTempView = false;
    }

    private void initView() {
        if (mLoadingView == null) {
            mLoadingView = (ViewGroup) mInflater.inflate(R.layout.def_loading_layout, null);
//            iv_loading = (ImageView) mLoadingView.findViewById(R.id.iv_loading);
            tv_status = (TextView) mLoadingView.findViewById(R.id.tv_status);
            loading_temp_view = mLoadingView.findViewById(R.id.loading_temp_view);
//            loading_anim = (AnimationDrawable) iv_loading.getBackground();
        }

        if (mEmptyView == null) {
            mEmptyView = (ViewGroup) mInflater.inflate(R.layout.def_empty_layout, null);
            iv_empty = (ImageView) mEmptyView.findViewById(R.id.iv_empty);
            tv_empty = (TextView) mEmptyView.findViewById(R.id.tv_null_desc);
            btn_empty = (Button) mEmptyView.findViewById(R.id.btn_null);
            empty_temp_view = mEmptyView.findViewById(R.id.empty_temp_view);

            if (mEmptyButtonClickListener != null) {
                btn_empty.setOnClickListener(mEmptyButtonClickListener);
//                mEmptyView.setOnClickListener(mEmptyButtonClickListener);
                iv_empty.setOnClickListener(mEmptyButtonClickListener);
            }
            mEmptyView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (onTouchLayoutListener != null) {
                        onTouchLayoutListener.onTouchLayout();
                    }
                    return false;
                }
            });
        }

        if (mErrorView == null) {
            mErrorView = (ViewGroup) mInflater.inflate(R.layout.def_error_layout, null);
            iv_error = (ImageView) mErrorView.findViewById(R.id.iv_error);
            tv_error = (TextView) mErrorView.findViewById(R.id.tv_error_desc);
            btn_error = (Button) mErrorView.findViewById(R.id.btn_error);
            error_temp_view = mErrorView.findViewById(R.id.error_temp_view);

            if (mErrorButtonClickListener != null) {
//                btn_error.setOnClickListener(mErrorButtonClickListener);
//                mErrorView.setOnClickListener(mErrorButtonClickListener);
                iv_error.setOnClickListener(mErrorButtonClickListener);
            }
        }

        tv_status.setText(loadingStr);

        if (emptyDrawable == 0) {
            iv_empty.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.VISIBLE);
            iv_empty.setImageResource(emptyDrawable);
        }
        tv_empty.setText(emptyStr);
        tv_empty.setLineSpacing(lineSpacingExtra, 1f);
        btn_empty.setText(emptyButtonTitle);

        if (errorDrawable == 0) {
            iv_error.setVisibility(View.GONE);
        } else {
            iv_error.setVisibility(View.VISIBLE);
            iv_error.setImageResource(errorDrawable);
        }
        tv_error.setText(errorStr);
        btn_error.setText(errorButtonTitle);

        if (isShowEmptyButton) {
            btn_empty.setVisibility(View.VISIBLE);
        } else {
            btn_empty.setVisibility(View.GONE);
        }

        if (isShowErrorButton) {
            btn_error.setVisibility(View.VISIBLE);
//            btn_error.setVisibility(View.GONE);
        } else {
            btn_error.setVisibility(View.GONE);
        }

        if (isShowTempView) {
            loading_temp_view.setVisibility(View.VISIBLE);
            empty_temp_view.setVisibility(View.VISIBLE);
            error_temp_view.setVisibility(View.VISIBLE);
            if(tempHeight > 0){
                loading_temp_view.getLayoutParams().height = tempHeight;
                empty_temp_view.getLayoutParams().height = tempHeight;
                error_temp_view.getLayoutParams().height = tempHeight;
            }
        } else {
            loading_temp_view.setVisibility(View.GONE);
            empty_temp_view.setVisibility(View.GONE);
            error_temp_view.setVisibility(View.GONE);
        }
        //添加主要 view
        if (!mViewsAdded) {
            mBackgroundView = new FrameLayout(mContext);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.topMargin = marginTop;
            lp.gravity = Gravity.CENTER;
            mBackgroundView.setBackgroundColor(bg_color);
            mBackgroundView.setLayoutParams(lp);

            if (mLoadingView != null) {
                mBackgroundView.addView(mLoadingView);
            }

            if (mEmptyView != null) {
                mBackgroundView.addView(mEmptyView);
            }

            if (mErrorView != null) {
                mBackgroundView.addView(mErrorView);
            }

            mViewsAdded = true;

            if (mContentView != null) {
                ((ViewGroup) mContentView.getParent()).addView(mBackgroundView, 1);
            }

        }

    }

    private void changeState() {
        initView();

        if (mContentView == null)
            return;

        switch (state) {
            case STATE_LOADING:
                if (mBackgroundView != null) {
                    mBackgroundView.setVisibility(View.VISIBLE);
                }

                if (mLoadingView != null) {
//                    loading_anim.start();
                    mLoadingView.setVisibility(View.VISIBLE);
                }
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }

                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }
                mContentView.setVisibility(View.GONE);

                break;
            case STATE_EMPTY:
                if (mBackgroundView != null) {
                    mBackgroundView.setVisibility(View.VISIBLE);
                }

                if (mLoadingView != null) {
//                    loading_anim.stop();
                    mLoadingView.setVisibility(View.GONE);
                }
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.VISIBLE);
                }

                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }
                mContentView.setVisibility(View.GONE);

                break;
            case STATE_ERROR:
                if (mBackgroundView != null) {
                    mBackgroundView.setVisibility(View.VISIBLE);
                }

                if (mLoadingView != null) {
//                    loading_anim.stop();
                    mLoadingView.setVisibility(View.GONE);
                }
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }

                if (mErrorView != null) {
                    mErrorView.setVisibility(View.VISIBLE);
                }
                mContentView.setVisibility(View.GONE);

                break;
            case STATE_CONTENT:
                if (mBackgroundView != null) {
                    mBackgroundView.setVisibility(View.GONE);
                }

                mContentView.setVisibility(View.VISIBLE);

                break;

            default:
                break;
        }
    }

    OnTouchLayoutListener onTouchLayoutListener;

    public void setOnTouchLayoutListener(OnTouchLayoutListener onTouchLayoutListener) {
        this.onTouchLayoutListener = onTouchLayoutListener;

    }

    public interface OnTouchLayoutListener {
        void onTouchLayout();
    }
}
