package com.tim.app.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.library.base.BaseFragment;
import com.application.library.net.JsonResponseCallback;
import com.application.library.widget.EmptyLayout;
import com.application.library.widget.loadmore.LoadMoreContainer;
import com.application.library.widget.loadmore.LoadMoreHandler;
import com.application.library.widget.loadmore.LoadMoreRecycleViewContainer;
import com.application.library.widget.recycle.HorizontalDividerItemDecoration;
import com.application.library.widget.recycle.WrapRecyclerView;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.AppKey;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.HistoryData;
import com.tim.app.ui.adapter.HistoryDataAdapter;
import com.tim.app.ui.view.HistoryDataHeadView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史数据
 */
public class HistoryDataFragment extends BaseFragment implements View.OnClickListener, LoadMoreHandler {

    public static final String TAG = "HistoryDataFragment";
    private static final int PAGE_SIZE = 20;

    private View rootView;
    private LoadMoreRecycleViewContainer lrvLoadMore;
    private WrapRecyclerView wrvHistoryData;
    private EmptyLayout emptyLayout;

    private HistoryDataAdapter adapter;
    private List<HistoryData> dataList;

    private HistoryDataHeadView headView;

    int type;
    private int universityId;
    private int pageCountWeek;
    private int pageSizeWeek = 6;
    private int pageNoWeek = 1;
    private int studentId = 1;

    public static HistoryDataFragment newInstance(int type) {
        HistoryDataFragment fragment = new HistoryDataFragment();
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

            lrvLoadMore = (LoadMoreRecycleViewContainer) rootView.findViewById(R.id.lrvLoadMore);
            wrvHistoryData = (WrapRecyclerView) rootView.findViewById(R.id.wrvHistoryData);
            //去除滑动到顶部或者是底部时会出现阴影的问题
//            wrvHistoryData.setOverScrollMode(View.OVER_SCROLL_NEVER);

            lrvLoadMore.useDefaultFooter(View.GONE);
            lrvLoadMore.setAutoLoadMore(true);
            lrvLoadMore.setLoadMoreHandler(this);

            emptyLayout = new EmptyLayout(getActivity(), lrvLoadMore);
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
        if (type == AppKey.THIS_WEEK) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoWeek, pageSizeWeek, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            pageCountWeek = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
                                    getString("pagesCount"));
                            JSONArray historyDataArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
                                    getJSONArray("data");
                            for (int i = 0; i < historyDataArray.length(); i++) {
                                HistoryData data = new HistoryData();
                                data.setSportDesc(historyDataArray.getJSONObject(i).optJSONObject("runningProject").getString("name"));
                                data.setTime(Long.valueOf(historyDataArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historyDataArray.getJSONObject(i).getString("caloriesConsumed")));
                                data.setSportTime(Integer.valueOf(historyDataArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historyDataArray.getJSONObject(i).getString("distance")));
                                dataList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            return true;
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

            });
        }
//        for (int i = 0; i < 5; i++) {
//            HistoryData historyData = new HistoryData();
//            historyData.setSportDesc("累计分段距离快走或跑");
//            historyData.setTime(System.currentTimeMillis());
//            historyData.setSpeed("1.0");
//            historyData.setCompleteCount(4);
//            historyData.setMinDistance(1000);
//            historyData.setCostEnergy(1000);
//            historyData.setSportTime(300);
//            dataList.add(historyData);
//        }
//        if(AppKey.TYPE_WEEK == type){
//            headView.setData("本周训练",3,5,1,1200,120);
//        }else if(AppKey.TYPE_MONTH == type){
//            headView.setData("本月训练",3,5,1,1200,120);
//        }else if(AppKey.TYPE_TERM == type){
//            headView.setData("本学期训练",3,5,1,1200,120);
//        }
        adapter.notifyDataSetChanged();
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
        if (type == AppKey.THIS_WEEK) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoWeek, pageSizeWeek, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            pageCountWeek = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
                                    getString("pagesCount"));
                            JSONArray historyDataArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
                                    getJSONArray("data");
                            for (int i = 0; i < historyDataArray.length(); i++) {
                                HistoryData data = new HistoryData();
                                data.setSportDesc(historyDataArray.getJSONObject(i).optJSONObject("runningProject").getString("name"));
                                data.setTime(Long.valueOf(historyDataArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historyDataArray.getJSONObject(i).getString("caloriesConsumed")));
                                data.setSportTime(Integer.valueOf(historyDataArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historyDataArray.getJSONObject(i).getString("distance")));
                                dataList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            return true;
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

            });

            if (pageNoWeek != pageCountWeek) {
                lrvLoadMore.loadMoreFinish(false, true);
            } else {
                lrvLoadMore.loadMoreFinish(false, false);
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
