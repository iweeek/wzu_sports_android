package com.application.library.widget.loadmore;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.application.library.widget.recycle.WrapRecyclerView;


public class LoadMoreRecycleViewContainer extends LinearLayout implements LoadMoreContainer {

    private RecyclerView.OnScrollListener mOnScrollListener;
    private LoadMoreUIHandler mLoadMoreUIHandler;
    private LoadMoreHandler mLoadMoreHandler;

    private boolean mIsLoading;
    private boolean mHasMore = true;
    private boolean mAutoLoadMore = true;
    private boolean mShowLoadingForFirstPage = true;
    private View mFooterView;

    private WrapRecyclerView recyclerView;
    private boolean mListEmpty = true;

    public LoadMoreRecycleViewContainer(Context context) {
        super(context);
    }

    public LoadMoreRecycleViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        recyclerView = (WrapRecyclerView) getChildAt(0);
        if (!isInEditMode()) {
            init();
        }

    }

    /**
     * @param visibility 设置底部view是否可见
     */
    public void useDefaultFooter(int visibility) {
        LoadMoreDefaultFooterView footerView = new LoadMoreDefaultFooterView(getContext());
        footerView.setVisibility(GONE);
        footerView.setBottomViewVisibility(visibility);
        setLoadMoreView(footerView);
        setLoadMoreUIHandler(footerView);
    }

    private void init() {

        if (mFooterView != null) {
            recyclerView.addFootView(mFooterView);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean mIsEnd = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (null != mOnScrollListener) {
                    mOnScrollListener.onScrollStateChanged(recyclerView, newState);
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mIsEnd) {
                        onReachBottom();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (null != mOnScrollListener) {
                    mOnScrollListener.onScrolled(recyclerView, dx, dy);
                }
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    int lastVisiblePosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    if (lastVisiblePosition >= recyclerView.getAdapter().getItemCount() - 1) {
                        mIsEnd = true;
                    } else {
                        mIsEnd = false;
                    }
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    int last[] = new int[staggeredGridLayoutManager.getSpanCount()];
                    staggeredGridLayoutManager.findLastVisibleItemPositions(last);
                    for (int aLast : last) {
                        if (aLast >= recyclerView.getAdapter().getItemCount() - 1) {
                            mIsEnd = true;
                        } else {
                            mIsEnd = false;
                        }
                    }
                }
            }
        });
    }

    private void performLoadMore() {
        if (mIsLoading || !mHasMore) {
            return;
        }

        mIsLoading = true;

        if (mLoadMoreUIHandler != null && (!mListEmpty || mShowLoadingForFirstPage)) {
            mLoadMoreUIHandler.onLoading(this);
        }
        if (null != mLoadMoreHandler) {
            mLoadMoreHandler.onLoadMore(this);
        }
    }

    private void onReachBottom() {
        if (mAutoLoadMore) {
            performLoadMore();
        } else {
            if (mHasMore) {
                mLoadMoreUIHandler.onWaitToLoadMore(this);
            }
        }
    }

    @Override
    public void setShowLoadingForFirstPage(boolean showLoading) {
        mShowLoadingForFirstPage = showLoading;
    }

    @Override
    public void setAutoLoadMore(boolean autoLoadMore) {
        mAutoLoadMore = autoLoadMore;
    }

    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {

    }

    public void setOnScrollListener(RecyclerView.OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void setLoadMoreView(View view) {
        // has not been initialized
        if (recyclerView == null) {
            mFooterView = view;
            return;
        }
        // remove previous
        if (mFooterView != null && mFooterView != view) {
            recyclerView.removeFootView();
        }

        // add current
        mFooterView = view;
        mFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                performLoadMore();
            }
        });

        recyclerView.addFootView(mFooterView);
    }

    @Override
    public void setLoadMoreUIHandler(LoadMoreUIHandler handler) {
        mLoadMoreUIHandler = handler;
    }

    @Override
    public void setLoadMoreHandler(LoadMoreHandler handler) {
        mLoadMoreHandler = handler;
    }

    /**
     * page has loaded
     *
     * @param emptyResult
     * @param hasMore
     */
    @Override
    public void loadMoreFinish(boolean emptyResult, boolean hasMore) {
        mListEmpty = emptyResult;
        mIsLoading = false;
        mHasMore = hasMore;

        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler.onLoadFinish(this, emptyResult, hasMore);
        }
    }

    public boolean isEmpty(){
        return mListEmpty;
    }

    public boolean hasMore(){
        return  mHasMore;
    }
}