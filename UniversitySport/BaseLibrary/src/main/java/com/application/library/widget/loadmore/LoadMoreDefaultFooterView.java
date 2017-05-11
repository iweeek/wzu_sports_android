package com.application.library.widget.loadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.library.R;


public class LoadMoreDefaultFooterView extends RelativeLayout implements LoadMoreUIHandler {
    private RelativeLayout footer_layout;
    private TextView mTextView;
    private View progressbar, bottom_view;
    private View vLeftLine, vRightLine;

    public LoadMoreDefaultFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupViews();
    }

    private void setupViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.cube_views_load_more_default_footer, this);
        footer_layout = (RelativeLayout) findViewById(R.id.footer_layout);
        mTextView = (TextView) findViewById(R.id.cube_views_load_more_default_footer_text_view);
        progressbar = findViewById(R.id.progressbar);
        bottom_view = findViewById(R.id.bottom_view);
        vLeftLine = findViewById(R.id.vLeftLine);
        vRightLine = findViewById(R.id.vRightLine);
    }

    @Override
    public void onLoading(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        footer_layout.setVisibility(View.VISIBLE);
        mTextView.setText(R.string.cube_views_load_more_loading);
        progressbar.setVisibility(View.VISIBLE);
        vLeftLine.setVisibility(View.GONE);
        vRightLine.setVisibility(View.GONE);
    }

    @Override
    public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore) {
        progressbar.setVisibility(View.GONE);
        vLeftLine.setVisibility(View.VISIBLE);
        vRightLine.setVisibility(View.VISIBLE);
        if (!hasMore) {
            setVisibility(VISIBLE);
            if (empty) {
//                mTextView.setText(R.string.load_more_data);
                footer_layout.setVisibility(View.GONE);
            } else {
                mTextView.setText(getResources().getString(R.string.no_more_data));
                footer_layout.setVisibility(View.VISIBLE);
            }

        } else {
            footer_layout.setVisibility(View.VISIBLE);
            setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onWaitToLoadMore(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        footer_layout.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.GONE);
        vLeftLine.setVisibility(View.VISIBLE);
        vRightLine.setVisibility(View.VISIBLE);
        mTextView.setText(R.string.cube_views_load_more_click_to_load_more);
    }

    public void setBottomViewVisibility(int visibility) {
        bottom_view.setVisibility(visibility);
    }
}
