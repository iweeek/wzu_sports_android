package com.application.library.widget.loadmore;

public interface LoadMoreUIHandler {

    public static final int STATUS_LOADED_EMPTY = 1;
    public static final int STATUS_LOADED_NO_MORE = 2;

    public void onLoading(LoadMoreContainer container);

    public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore);

    public void onWaitToLoadMore(LoadMoreContainer container);
}