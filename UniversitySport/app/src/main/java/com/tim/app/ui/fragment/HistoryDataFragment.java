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
import com.application.library.log.DLOG;
import com.application.library.net.JsonResponseCallback;
import com.application.library.widget.EmptyLayout;
import com.application.library.widget.loadmore.LoadMoreContainer;
import com.application.library.widget.loadmore.LoadMoreHandler;
import com.application.library.widget.loadmore.LoadMoreRecycleViewContainer;
import com.application.library.widget.recycle.HorizontalDividerItemDecoration;
import com.application.library.widget.recycle.WrapRecyclerView;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.HistorySportEntry;
import com.tim.app.ui.adapter.HistorySportListAdapter;
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

    private HistorySportListAdapter adapter;
    private List<HistorySportEntry> dataList;

    private HistoryDataHeadView headView;

    int type;
    private int universityId;
    private int studentId = 2;

    private int pageCountWeek;
    private int pageSizeWeek = 6;
    private int pageNoWeek = 1;

    private int pageCountMonth;
    private int pageSizeMonth = 6;
    private int pageNoMonth = 1;

    private int pageCountTerm;
    private int pageSizeTerm = 6;
    private int pageNoTerm = 1;

    private int pageCountHistory;
    private int pageSizeHistory = 6;
    private int pageNoHistory = 1;

    private String accuTimes;
    private String qualifiedTime;
    private String energyCost;
    private String timeCost;

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
            emptyLayout.showLoading();
            emptyLayout.setEmptyButtonShow(false);
            emptyLayout.setErrorButtonShow(true);
            emptyLayout.setEmptyDrawable(R.drawable.ic_empty_hisorty_data);
            emptyLayout.setEmptyText("当前没有运动记录");
            emptyLayout.setEmptyButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emptyLayout.showLoading();
                    initData();
                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            wrvHistoryData.setLayoutManager(layoutManager);
            wrvHistoryData.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(getResources().getColor(R.color.view_background_color)).size((int) getResources().getDimension(R.dimen.dimen_2)).build());

            headView = (HistoryDataHeadView) LayoutInflater.from(getActivity()).inflate(R.layout.history_data_head_view, null);

            wrvHistoryData.addHeaderView(headView);

            dataList = new ArrayList<>();
            adapter = new HistorySportListAdapter(getActivity(), dataList);
            wrvHistoryData.setAdapter(adapter);

            if (getArguments() != null) {
                type = getArguments().getInt("type");
            }

        }
        headView.setData("0", "0", "0", "0", "0");
        initData();
        return rootView;
    }

    private void initData() {
        if (type == AppConstant.THIS_WEEK) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoWeek++, pageSizeWeek, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            pageCountWeek = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
                                    getString("pagesCount"));
                            accuTimes = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
                                    getString("dataCount");
                            qualifiedTime = json.optJSONObject("data").optJSONObject("student").getString("qualifiedActivityCount");
                            energyCost = json.optJSONObject("data").optJSONObject("student").getString("caloriesConsumption");
                            timeCost = json.optJSONObject("data").optJSONObject("student").getString("timeCosted");

                            headView.setData("本周累计次数（次）", accuTimes, qualifiedTime, energyCost, timeCost);
                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
                                    getJSONArray("data");
                            for (int i = 0; i < historySportArray.length(); i++) {
                                HistorySportEntry data = new HistorySportEntry();
                                data.setActivityId(historySportArray.getJSONObject(i).getInt("id"));
                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
                                data.setSportTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
                                data.setQualified(historySportArray.getJSONObject(i).getBoolean("qualified"));
                                dataList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            if (dataList.size() == 0) {
                                emptyLayout.showEmpty();
                            } else {
                                emptyLayout.showContent();
                            }
                            return true;
                        } catch (org.json.JSONException e) {
                            emptyLayout.showEmptyOrError(errCode);
                            e.printStackTrace();
                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
                            return false;
                        }
                    } else {
                        emptyLayout.showEmptyOrError(errCode);
                        return false;
                    }
                }

            });
        } else if (type == AppConstant.THIS_MONTH) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoMonth++, pageSizeMonth, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            pageCountMonth = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("currentMonthActivities").
                                    getString("pagesCount"));
                            accuTimes = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentMonthActivities").
                                    getString("dataCount");
                            qualifiedTime = json.optJSONObject("data").optJSONObject("student").getString("qualifiedActivityCount");
                            energyCost = json.optJSONObject("data").optJSONObject("student").getString("caloriesConsumption");
                            timeCost = json.optJSONObject("data").optJSONObject("student").getString("timeCosted");

                            headView.setData("本月累计次数（次）", accuTimes, qualifiedTime, energyCost, timeCost);
                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentMonthActivities").
                                    getJSONArray("data");
                            for (int i = 0; i < historySportArray.length(); i++) {
                                HistorySportEntry data = new HistorySportEntry();
                                data.setActivityId(historySportArray.getJSONObject(i).getInt("id"));
                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
                                data.setSportTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
                                data.setQualified(historySportArray.getJSONObject(i).getBoolean("qualified"));
                                dataList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            if (dataList.size() == 0) {
                                emptyLayout.showEmpty();
                            } else {
                                emptyLayout.showContent();
                            }
                            return true;
                        } catch (org.json.JSONException e) {
                            emptyLayout.showEmptyOrError(errCode);
                            e.printStackTrace();
                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
                            return false;
                        }
                    } else {
                        emptyLayout.showEmptyOrError(errCode);
                        return false;
                    }
                }

            });
        } else if (type == AppConstant.THIS_TERM) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoTerm++, pageSizeTerm, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            pageCountTerm = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("currentTermActivities").
                                    getString("pagesCount"));
                            accuTimes = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentTermActivities").
                                    getString("dataCount");
                            qualifiedTime = json.optJSONObject("data").optJSONObject("student").getString("qualifiedActivityCount");
                            energyCost = json.optJSONObject("data").optJSONObject("student").getString("caloriesConsumption");
                            timeCost = json.optJSONObject("data").optJSONObject("student").getString("timeCosted");

                            headView.setData("本学期累计次数（次）", accuTimes, qualifiedTime, energyCost, timeCost);
                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentTermActivities").
                                    getJSONArray("data");
                            for (int i = 0; i < historySportArray.length(); i++) {
                                HistorySportEntry data = new HistorySportEntry();
                                data.setActivityId(historySportArray.getJSONObject(i).getInt("id"));
                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
                                data.setSportTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
                                data.setQualified(historySportArray.getJSONObject(i).getBoolean("qualified"));
                                dataList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            if (dataList.size() == 0) {
                                emptyLayout.showEmpty();
                            } else {
                                emptyLayout.showContent();
                            }
                            return true;
                        } catch (org.json.JSONException e) {
                            emptyLayout.showEmptyOrError(errCode);
                            e.printStackTrace();
                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
                            return false;
                        }
                    } else {
                        emptyLayout.showEmptyOrError(errCode);
                        return false;
                    }
                }

            });
        } else {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoHistory++, pageSizeHistory, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            pageCountTerm = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("activities").
                                    getString("pagesCount"));
                            accuTimes = json.optJSONObject("data").optJSONObject("student").optJSONObject("activities").
                                    getString("dataCount");
                            qualifiedTime = json.optJSONObject("data").optJSONObject("student").getString("qualifiedActivityCount");
                            energyCost = json.optJSONObject("data").optJSONObject("student").getString("caloriesConsumption");
                            timeCost = json.optJSONObject("data").optJSONObject("student").getString("timeCosted");

                            headView.setData("历史累计次数（次）", accuTimes, qualifiedTime, energyCost, timeCost);
                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("activities").
                                    getJSONArray("data");
                            for (int i = 0; i < historySportArray.length(); i++) {
                                HistorySportEntry data = new HistorySportEntry();
                                data.setActivityId(historySportArray.getJSONObject(i).getInt("id"));
                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
                                data.setSportTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
                                dataList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            if (dataList.size() == 0) {
                                emptyLayout.showEmpty();
                            } else {
                                emptyLayout.showContent();
                            }
                            return true;
                        } catch (org.json.JSONException e) {
                            emptyLayout.showEmptyOrError(errCode);
                            e.printStackTrace();
                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
                            return false;
                        }
                    } else {
                        emptyLayout.showEmptyOrError(errCode);
                        return false;
                    }
                }

            });
        }

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
        DLOG.d(TAG, "onClick");
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onLoadMore(LoadMoreContainer loadMoreContainer) {
        if (type == AppConstant.THIS_WEEK) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoWeek, pageSizeWeek, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            pageCountWeek = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
                                    getString("pagesCount"));
                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
                                    getJSONArray("data");
                            for (int i = 0; i < historySportArray.length(); i++) {
                                HistorySportEntry data = new HistorySportEntry();
                                data.setActivityId(historySportArray.getJSONObject(i).getInt("id"));
                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
                                data.setSportTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
                                data.setQualified(historySportArray.getJSONObject(i).getBoolean("qualified"));
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
                pageNoWeek++;
                lrvLoadMore.loadMoreFinish(false, true);
            } else {
                lrvLoadMore.loadMoreFinish(false, false);
            }
        } else if (type == AppConstant.THIS_MONTH) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoMonth, pageSizeMonth, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentMonthActivities").
                                    getJSONArray("data");
                            for (int i = 0; i < historySportArray.length(); i++) {
                                HistorySportEntry data = new HistorySportEntry();
                                data.setActivityId(historySportArray.getJSONObject(i).getInt("id"));
                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
                                data.setSportTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
                                data.setQualified(historySportArray.getJSONObject(i).getBoolean("qualified"));
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

            if (pageNoMonth != pageCountMonth) {
                pageNoMonth++;
                lrvLoadMore.loadMoreFinish(false, true);
            } else {
                lrvLoadMore.loadMoreFinish(false, false);
            }

        } else if (type == AppConstant.THIS_TERM) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoTerm, pageSizeTerm, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            pageCountTerm = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("currentTermActivities").
                                    getString("pagesCount"));
                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentTermActivities").
                                    getJSONArray("data");
                            for (int i = 0; i < historySportArray.length(); i++) {
                                HistorySportEntry data = new HistorySportEntry();
                                data.setActivityId(historySportArray.getJSONObject(i).getInt("id"));
                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
                                data.setSportTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
                                data.setQualified(historySportArray.getJSONObject(i).getBoolean("qualified"));
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

            if (pageNoTerm != pageCountTerm) {
                pageNoTerm++;
                lrvLoadMore.loadMoreFinish(false, true);
            } else {
                lrvLoadMore.loadMoreFinish(false, false);
            }

        } else {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoHistory, pageSizeTerm, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            pageCountTerm = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("activities").
                                    getString("pagesCount"));
                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("activities").
                                    getJSONArray("data");
                            for (int i = 0; i < historySportArray.length(); i++) {
                                HistorySportEntry data = new HistorySportEntry();
                                data.setActivityId(historySportArray.getJSONObject(i).getInt("id"));
                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningProject").getString("name"));
                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
                                data.setCostEnergy(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
                                data.setSportTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
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

            if (pageNoHistory != pageCountHistory) {
                pageNoHistory++;
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
