package com.tim.app.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.library.base.BaseFragment;
import com.application.library.runtime.event.EventListener;
import com.application.library.runtime.event.EventManager;
import com.application.library.widget.EmptyLayout;
import com.application.library.widget.loadmore.LoadMoreContainer;
import com.application.library.widget.loadmore.LoadMoreHandler;
import com.application.library.widget.loadmore.LoadMoreRecycleViewContainer;
import com.application.library.widget.recycle.HorizontalDividerItemDecoration;
import com.application.library.widget.recycle.WrapRecyclerView;
import com.application.library.widget.refresh.PtrClassicFrameLayout;
import com.application.library.widget.refresh.PtrDefaultHandler;
import com.application.library.widget.refresh.PtrFrameLayout;
import com.application.library.widget.refresh.PtrHandler;
import com.application.library.widget.refresh.header.FrameSearchHeader;
import com.application.library.widget.roundimg.RoundedImageView;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.EventTag;
import com.tim.app.ui.activity.setting.PhoneLoginActivity;

/**
 * 主页
 * 列表中播放视频需实现VideoListAdapter.OnPlayClickListener, RecyclerView.OnChildAttachStateChangeListener
 */
public class HomePageFragment extends BaseFragment implements View.OnClickListener, PtrHandler, LoadMoreHandler, RecyclerView.OnChildAttachStateChangeListener {

    public static final String TAG = "HomePageFragment";
    private static final int PAGE_SIZE = 20;
    private View rootView;
    private PtrClassicFrameLayout refresh_layout;
    private LoadMoreRecycleViewContainer load_more;
    private WrapRecyclerView rv_home;
    private EmptyLayout emptyLayout;

    private RoundedImageView iv_avatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_homepage, container, false);

            refresh_layout = (PtrClassicFrameLayout) rootView.findViewById(R.id.refresh_layout);

            iv_avatar = (RoundedImageView) rootView.findViewById(R.id.iv_avatar);
            iv_avatar.setOnClickListener(this);

            FrameSearchHeader frameSearchHeader = new FrameSearchHeader(getActivity());
            refresh_layout.setHeaderView(frameSearchHeader);
            refresh_layout.addPtrUIHandler(frameSearchHeader);
            load_more = (LoadMoreRecycleViewContainer) rootView.findViewById(R.id.load_more);
            rv_home = (WrapRecyclerView) rootView.findViewById(R.id.rv_home);
            rv_home.setOverScrollMode(View.OVER_SCROLL_NEVER);

            refresh_layout.disableWhenHorizontalMove(true);
            refresh_layout.setPtrHandler(this);
            load_more.useDefaultFooter(View.GONE);
            load_more.setAutoLoadMore(true);
            load_more.setLoadMoreHandler(this);
            rv_home.addOnChildAttachStateChangeListener(this);

            emptyLayout = new EmptyLayout(getActivity(), refresh_layout);
            emptyLayout.showLoading();
            emptyLayout.setEmptyButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emptyLayout.showLoading();
//                    API_Home.ins().getHomepageData(TAG, 0, PAGE_SIZE, CacheMode.FIRST_CACHE_THEN_REQUEST, refreshCallback);
                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rv_home.setLayoutManager(layoutManager);
            rv_home.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(getResources().getColor(R.color.transparent)).size((int) getResources().getDimension(R.dimen.dimen_10)).build());

//            API_Home.ins().getHomepageData(TAG, 0, PAGE_SIZE, CacheMode.FIRST_CACHE_THEN_REQUEST, refreshCallback);
        } else {
            if (refresh_layout != null) {
                refresh_layout.refreshComplete();
            }
        }
        EventManager.ins().registListener(EventTag.ACCOUNT_LOGIN, eventListener);
        EventManager.ins().registListener(EventTag.ACCOUNT_LOGOUT, eventListener);
        EventManager.ins().registListener(EventTag.ACCOUNT_UPDATE_INFO, eventListener);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_avatar:
                PhoneLoginActivity.start(getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, rv_home, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
//        API_Home.ins().getHomepageData(TAG, 0, PAGE_SIZE, CacheMode.DEFAULT, refreshCallback);
    }

    @Override
    public void onLoadMore(LoadMoreContainer loadMoreContainer) {
//        API_Home.ins().getHomepageData(TAG, page_start, PAGE_SIZE, CacheMode.NO_CACHE, moreCallback);
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {
        int index = rv_home.getChildAdapterPosition(view);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    EventListener eventListener = new EventListener() {
        @Override
        public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
            switch (what) {
                case EventTag.ACCOUNT_LOGIN:
                case EventTag.ACCOUNT_UPDATE_INFO:
//                    BitmapLoader.ins().loadImage(UserManager.ins().getAvatar(), R.drawable.ic_def_avatar_small, iv_avatar);
                    break;
                case EventTag.ACCOUNT_LOGOUT:
                    iv_avatar.setImageResource(R.drawable.ic_def_avatar_small);
                    break;
            }
        }
    };

}
