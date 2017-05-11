package com.application.library.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;

import com.application.library.widget.refresh.header.FrameHeader;


public class PtrClassicFrameLayout extends PtrFrameLayout {

    //    private PtrClassicDefaultHeader mPtrClassicHeader;
//    private MaterialHeader mPtrClassicHeader;
    private FrameHeader mPtrClassicHeader;
//    private RentalsSunHeaderView mPtrClassicHeader;

    public PtrClassicFrameLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        if (isInEditMode()) {
            return;
        }
//        mPtrClassicHeader = new MaterialHeader(getContext());
//        int[] colors = getResources().getIntArray(R.array.google_colors);
//        mPtrClassicHeader.setColorSchemeColors(colors);
//        mPtrClassicHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
//        mPtrClassicHeader.setPadding(0, PhoneUtil.dipToPixel(15, getContext()), 0, PhoneUtil.dipToPixel(15, getContext()));
//        mPtrClassicHeader.setPtrFrameLayout(this);
//        setLoadingMinTime(1000);
//        setDurationToClose(1500);
//        setHeaderView(mPtrClassicHeader);
//        addPtrUIHandler(mPtrClassicHeader);

        mPtrClassicHeader = new FrameHeader(getContext());
        setHeaderView(mPtrClassicHeader);
        addPtrUIHandler(mPtrClassicHeader);

//        mPtrClassicHeader = new RentalsSunHeaderView(getContext());
//        mPtrClassicHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
//        mPtrClassicHeader.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay.dp2px(10));
//        mPtrClassicHeader.setUp(this);
//
//        setLoadingMinTime(1000);
//        setDurationToCloseHeader(1500);
//        setHeaderView(mPtrClassicHeader);
//        addPtrUIHandler(mPtrClassicHeader);

    }

    public FrameHeader getHeader() {
        return mPtrClassicHeader;
    }

//    public MaterialHeader getHeader() {
//        return mPtrClassicHeader;
//    }

//    public RentalsSunHeaderView getHeader() {
//        return mPtrClassicHeader;
//    }

    /**
     * Specify the last update time by this key string
     *
     * @param key
     */
    public void setLastUpdateTimeKey(String key) {
//        if (mPtrClassicHeader != null) {
//            mPtrClassicHeader.setLastUpdateTimeKey(key);
//        }
    }

    /**
     * Using an object to specify the last update time.
     *
     * @param object
     */
    public void setLastUpdateTimeRelateObject(Object object) {
//        if (mPtrClassicHeader != null) {
//            mPtrClassicHeader.setLastUpdateTimeRelateObject(object);
//        }
    }
}
