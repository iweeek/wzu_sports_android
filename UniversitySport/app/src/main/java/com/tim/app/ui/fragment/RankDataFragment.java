package com.tim.app.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.library.base.BaseFragment;
import com.application.library.widget.EmptyLayout;
import com.application.library.widget.loadmore.LoadMoreContainer;
import com.application.library.widget.loadmore.LoadMoreHandler;
import com.application.library.widget.loadmore.LoadMoreRecycleViewContainer;
import com.application.library.widget.recycle.HorizontalDividerItemDecoration;
import com.application.library.widget.recycle.WrapRecyclerView;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.AppKey;
import com.tim.app.server.entry.HistoryData;
import com.tim.app.ui.adapter.HistoryDataAdapter;
import com.tim.app.ui.view.HistoryDataHeadView;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史数据
 */
public class RankDataFragment extends BaseFragment implements View.OnClickListener, LoadMoreHandler {

    public static final String TAG = "HistoryDataFragment";
    private static final int PAGE_SIZE = 20;

    private View rootView;
    private LoadMoreRecycleViewContainer load_more;
    private WrapRecyclerView wrvHistoryData;
    private EmptyLayout emptyLayout;

    private HistoryDataAdapter adapter;
    private List<HistoryData> dataList;

    private HistoryDataHeadView headView;

    int type;

    public static RankDataFragment newInstance(int type) {
        RankDataFragment fragment = new RankDataFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_history, container, false);

            load_more = (LoadMoreRecycleViewContainer) rootView.findViewById(R.id.load_more);
            wrvHistoryData = (WrapRecyclerView) rootView.findViewById(R.id.wrvHistoryData);
            wrvHistoryData.setOverScrollMode(View.OVER_SCROLL_NEVER);

            load_more.useDefaultFooter(View.GONE);
            load_more.setAutoLoadMore(true);
            load_more.setLoadMoreHandler(this);

            emptyLayout = new EmptyLayout(getActivity(), load_more);
//            emptyLayout.showLoading();
            emptyLayout.setEmptyButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emptyLayout.showLoading();

                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            wrvHistoryData.setLayoutManager(layoutManager);
            wrvHistoryData.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(getResources().getColor(R.color.view_background_color)).size((int) getResources().getDimension(R.dimen.dimen_2)).build());

            headView = (HistoryDataHeadView) LayoutInflater.from(getActivity()).inflate(R.layout.historydata_head_view, null);
            wrvHistoryData.addHeaderView(headView);

            dataList = new ArrayList<>();
            adapter = new HistoryDataAdapter(getActivity(), dataList);
            wrvHistoryData.setAdapter(adapter);

            if (getArguments() != null) {
                type = getArguments().getInt("type");
            }

        }
        initData();
        return rootView;
    }

    private void initData() {
        for (int i = 0; i < 5; i++) {
            HistoryData historyData = new HistoryData();
            historyData.setSportDesc("累计分段距离快走或跑");
            historyData.setTime(System.currentTimeMillis());
            historyData.setSpeed("1.0");
            historyData.setCompleteCount(4);
            historyData.setMinDistance(1000);
            historyData.setCostNumber(1000);
            historyData.setSportTime(300);
            dataList.add(historyData);
        }
        adapter.notifyDataSetChanged();
        if(AppKey.TYPE_WEEK == type){
            headView.setData("本周训练",3,5,1,1200,120);
        }else if(AppKey.TYPE_MONTH == type){
            headView.setData("本月训练",3,5,1,1200,120);
        }else if(AppKey.TYPE_TERM == type){
            headView.setData("本学期训练",3,5,1,1200,120);
        }
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
            default:
                break;
        }
    }

    @Override
    public void onLoadMore(LoadMoreContainer loadMoreContainer) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
