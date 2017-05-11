package com.application.library.widget.loadmore;


import com.application.library.widget.refresh.PtrFrameLayout;
import com.application.library.widget.refresh.PtrUIHandler;
import com.application.library.widget.refresh.indicator.PtrIndicator;

public abstract class PtrUIRefreshCompleteHandler implements PtrUIHandler {

    @Override
    public void onUIReset(PtrFrameLayout frame) {
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }
}
