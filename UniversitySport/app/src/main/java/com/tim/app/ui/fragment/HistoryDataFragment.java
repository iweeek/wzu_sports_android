package com.tim.app.ui.fragment;

import android.app.ActivityGroup;
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
import com.tim.app.server.entry.HistoryAreaSportEntry;
import com.tim.app.server.entry.HistoryRunningSportEntry;
import com.tim.app.server.entry.HistorySportEntry;
import com.tim.app.ui.activity.HistoryItem;
import com.tim.app.ui.adapter.HistorySportListAdapter;
import com.tim.app.ui.view.HistoryDataHeadView;
import com.tim.app.util.MyDateUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.R.attr.data;
import static android.R.id.list;
import static com.tim.app.R.id.map;

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
    private List<HistoryItem> dataList = new ArrayList<HistoryItem>();

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

    class MyComparator implements Comparator<HistorySportEntry> {
        @Override
        public int compare(HistorySportEntry lhs, HistorySportEntry rhs) {
            Date ldate = new Date(lhs.getStartTime());
            //            Date ldate = new Date(lhs.getSportDate());
            Date rdate = new Date(rhs.getStartTime());
            //            Date rdate = new Date(rhs.getSportDate());
            return rdate.compareTo(ldate);
        }
    }

    private void initData() {

        if (type == AppConstant.THIS_WEEK) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoWeek++, pageSizeWeek, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        Date startDate = MyDateUtil.getCurrentWeekStartDate();
                        Date endDate= new Date();

                        JSONObject student = json.optJSONObject("data").optJSONObject("student");
                        try {
                            int accuRunningActivityCount = student.optInt("accuRunningActivityCount");
                            int accuAreaActivityCount = student.optInt("accuAreaActivityCount");

                            int qualifiedRunningActivityCount = student.optInt("qualifiedRunningActivityCount");
                            int qualifiedAreaActivityCount = student.optInt("qualifiedAreaActivityCount");

                            int runningActivityTimeCosted = student.optInt("runningActivityTimeCosted");
                            int areaActivityTimeCosted = student.optInt("areaActivityTimeCosted");

                            int runningActivityKcalConsumption = student.optInt("runningActivityKcalConsumption");
                            int areaActivityKcalConsumption = student.optInt("areaActivityKcalConsumption");

                            String totalActivityCount = String.valueOf(accuAreaActivityCount + accuRunningActivityCount);
                            String totalqualifiedActivityCount = String.valueOf(qualifiedAreaActivityCount + qualifiedRunningActivityCount);
                            String totalActivityTimeCosted = String.valueOf(runningActivityTimeCosted + areaActivityTimeCosted);
                            String toalActivityKcalConsumption = String.valueOf(runningActivityKcalConsumption + areaActivityKcalConsumption);

                            headView.setData("本周累计次数（次）", totalActivityCount, totalqualifiedActivityCount, totalActivityTimeCosted, toalActivityKcalConsumption);

                            JSONArray runningSportArray = student.optJSONObject("runningActivities").optJSONArray("data");
                            JSONArray areaSportArray = student.optJSONObject("areaActivities").optJSONArray("data");

                            for (LocalDate date = new LocalDate(endDate); date.isAfter(new LocalDate(startDate).minusDays(1)); date = date.minusDays(1)) {
                                Log.d(TAG, "startDate: " + startDate);
                                Log.d(TAG, "date: " + date);
                                HistoryItem item = new HistoryItem();
                                item.historySportEntryList = null;
                                for (int i = 0; i < runningSportArray.length(); i++) {
                                    Log.d(TAG, "running sport date: " + runningSportArray.optJSONObject(i).optString("sportDate"));
                                    Log.d(TAG, "date: " + date.toString());
                                    if ((runningSportArray.optJSONObject(i).optString("sportDate")).equals(date.toString())) {
                                        HistoryRunningSportEntry entry = new HistoryRunningSportEntry();
                                        entry.setSportId(runningSportArray.optJSONObject(i).optInt("runningSportId"));
                                        entry.setCostTime(Integer.valueOf(runningSportArray.optJSONObject(i).optString("costTime")));
                                        entry.setDistance(Integer.valueOf(runningSportArray.optJSONObject(i).optString("distance")));
                                        entry.setKcalConsumed(Integer.valueOf(runningSportArray.optJSONObject(i).optString("kcalConsumed")));
                                        entry.setQualified(runningSportArray.optJSONObject(i).optBoolean("qualified"));
                                        entry.setStartTime(Long.valueOf(runningSportArray.optJSONObject(i).optString("startTime")));
                                        entry.setSportDate(String.valueOf(runningSportArray.optJSONObject(i).optString("sportDate")));
                                        //过滤不完整数据
                                        if (Long.valueOf(runningSportArray.optJSONObject(i).getString("endedAt")) > 0) {
                                            entry.setEndAt(Long.valueOf(runningSportArray.optJSONObject(i).getString("endedAt")));
                                        } else {
                                            continue;
                                        }
                                        entry.setSportName(runningSportArray.optJSONObject(i).optJSONObject("runningSport").optString("name"));
                                        entry.setType(HistorySportEntry.RUNNING_TYPE);

                                        if (item.historySportEntryList == null) {
                                            item.historySportEntryList = new ArrayList<HistorySportEntry>();
                                        }
                                        item.historySportEntryList.add(entry);
                                        Log.d(TAG, "ag.runningSportEntry sport date: " + date);
                                    }
                                }

                                for (int i = 0; i < areaSportArray.length(); i++) {
                                    Log.d(TAG, "area sport date: " + areaSportArray.optJSONObject(i).optString("sportDate"));
                                    Log.d(TAG, "date: " + date.toString());
                                    if ((areaSportArray.optJSONObject(i).optString("sportDate")).equals(date.toString())) {
                                        HistoryAreaSportEntry entry = new HistoryAreaSportEntry();
                                        entry.setSportId(areaSportArray.optJSONObject(i).optInt("areaSportId"));
                                        entry.setCostTime(Integer.valueOf(areaSportArray.optJSONObject(i).optString("costTime")));
                                        entry.setKcalConsumed(Integer.valueOf(areaSportArray.optJSONObject(i).optString("kcalConsumed")));
                                        entry.setQualified(areaSportArray.optJSONObject(i).optBoolean("qualified"));
                                        entry.setStartTime(Long.valueOf(areaSportArray.optJSONObject(i).optString("startTime")));
                                        entry.setSportDate(String.valueOf(areaSportArray.optJSONObject(i).optString("sportDate")));
                                        //过滤不完整数据
                                        if (Long.valueOf(areaSportArray.optJSONObject(i).getString("endedAt")) > 0) {
                                            entry.setEndAt(Long.valueOf(areaSportArray.optJSONObject(i).getString("endedAt")));
                                        } else {
                                            continue;
                                        }
                                        entry.setSportName(areaSportArray.optJSONObject(i).optJSONObject("areaSport").optString("name"));
                                        entry.setType(HistorySportEntry.AREA_TYPE);

                                        if (item.historySportEntryList == null) {
                                            item.historySportEntryList = new ArrayList<HistorySportEntry>();
                                        }
                                        item.historySportEntryList.add(entry);
                                        Log.d(TAG, "ag.areaSportEntry sport date: " + date);
                                    }
                                }

                                if (item.historySportEntryList != null) {
                                    item.date = date.toString();
                                    dataList.add(item);
                                }
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
        } /*else if (type == AppConstant.THIS_MONTH) {
            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoMonth++, pageSizeMonth, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        JSONObject student = json.optJSONObject("data").optJSONObject("student");
                        try {
                            int accuRunningActivityCount = student.optInt("accuRunningActivityCount");
                            int accuAreaActivityCount = student.optInt("accuAreaActivityCount");

                            int qualifiedRunningActivityCount = student.optInt("qualifiedRunningActivityCount");
                            int qualifiedAreaActivityCount = student.optInt("qualifiedAreaActivityCount");

                            int runningActivityTimeCosted = student.optInt("runningActivityTimeCosted");
                            int areaActivityTimeCosted = student.optInt("areaActivityTimeCosted");

                            int runningActivityKcalConsumption = student.optInt("runningActivityKcalConsumption");
                            int areaActivityKcalConsumption = student.optInt("areaActivityKcalConsumption");

                            String totalActivityCount = String.valueOf(accuAreaActivityCount + accuRunningActivityCount);
                            String totalqualifiedActivityCount = String.valueOf(qualifiedAreaActivityCount + qualifiedRunningActivityCount);
                            String totalActivityTimeCosted = String.valueOf(runningActivityTimeCosted + areaActivityTimeCosted);
                            String toalActivityKcalConsumption = String.valueOf(runningActivityKcalConsumption + areaActivityKcalConsumption);

                            headView.setData("本月累计次数（次）", totalActivityCount, totalqualifiedActivityCount, totalActivityTimeCosted, toalActivityKcalConsumption);

                            JSONArray runningSportArray = student.optJSONObject("runningActivities").optJSONArray("data");
                            JSONArray areaSportArray = student.optJSONObject("areaActivities").optJSONArray("data");

                            for (int i = 0; i < runningSportArray.length(); i++) {
                                HistoryRunningSportEntry data = new HistoryRunningSportEntry();
                                data.setSportId(runningSportArray.optJSONObject(i).optInt("runningSportId"));
                                data.setCostTime(Integer.valueOf(runningSportArray.optJSONObject(i).optString("costTime")));
                                data.setDistance(Integer.valueOf(runningSportArray.optJSONObject(i).optString("distance")));
                                data.setKcalConsumed(Integer.valueOf(runningSportArray.optJSONObject(i).optString("kcalConsumed")));
                                data.setQualified(runningSportArray.optJSONObject(i).optBoolean("qualified"));
                                data.setStartTime(Long.valueOf(runningSportArray.optJSONObject(i).optString("startTime")));
                                data.setSportDate(String.valueOf(runningSportArray.optJSONObject(i).optString("sportDate")));
                                data.setEndAt(Long.valueOf(runningSportArray.optJSONObject(i).getString("endedAt")));
                                data.setSportName(runningSportArray.optJSONObject(i).optJSONObject("runningSport").optString("name"));
                                dataList.add(data);
                            }
                            for (int i = 0; i < areaSportArray.length(); i++) {
                                HistoryAreaSportEntry data = new HistoryAreaSportEntry();
                                data.setSportId(areaSportArray.optJSONObject(i).optInt("areaSportId"));
                                data.setCostTime(Integer.valueOf(areaSportArray.optJSONObject(i).optString("costTime")));
                                data.setKcalConsumed(Integer.valueOf(areaSportArray.optJSONObject(i).optString("kcalConsumed")));
                                data.setQualified(areaSportArray.optJSONObject(i).optBoolean("qualified"));
                                data.setStartTime(Long.valueOf(areaSportArray.optJSONObject(i).optString("startTime")));
                                data.setSportDate(String.valueOf(areaSportArray.optJSONObject(i).optString("sportDate")));
                                data.setEndAt(Long.valueOf(areaSportArray.optJSONObject(i).getString("endedAt")));
                                data.setSportName(areaSportArray.optJSONObject(i).optJSONObject("areaSport").optString("name"));
                                dataList.add(data);
                            }

//                            for (int i = 0; i < dataList.size(); i++) {
//                                Log.d(TAG, "dataList:" + dataList.get(i).toString());
//                            }
//
//                            Collections.sort(dataList, new MyComparator());
//
//
//                            for (int i = 0; i < dataList.size(); i++) {
//                                Log.d(TAG, "dataList:" + dataList.get(i).toString());
//                            }
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
                        Log.d(TAG, json + "haahahahhahaha");
                        JSONObject student = json.optJSONObject("data").optJSONObject("student");
                        try {
                            int accuRunningActivityCount = student.optInt("accuRunningActivityCount");
                            int accuAreaActivityCount = student.optInt("accuAreaActivityCount");

                            int qualifiedRunningActivityCount = student.optInt("qualifiedRunningActivityCount");
                            int qualifiedAreaActivityCount = student.optInt("qualifiedAreaActivityCount");

                            int runningActivityTimeCosted = student.optInt("runningActivityTimeCosted");
                            int areaActivityTimeCosted = student.optInt("areaActivityTimeCosted");

                            int runningActivityKcalConsumption = student.optInt("runningActivityKcalConsumption");
                            int areaActivityKcalConsumption = student.optInt("areaActivityKcalConsumption");

                            String totalActivityCount = String.valueOf(accuAreaActivityCount + accuRunningActivityCount);
                            String totalqualifiedActivityCount = String.valueOf(qualifiedAreaActivityCount + qualifiedRunningActivityCount);
                            String totalActivityTimeCosted = String.valueOf(runningActivityTimeCosted + areaActivityTimeCosted);
                            String toalActivityKcalConsumption = String.valueOf(runningActivityKcalConsumption + areaActivityKcalConsumption);

                            headView.setData("本学期累计次数（次）", totalActivityCount, totalqualifiedActivityCount, totalActivityTimeCosted, toalActivityKcalConsumption);

                            JSONArray runningSportArray = student.optJSONObject("runningActivities").optJSONArray("data");
                            JSONArray areaSportArray = student.optJSONObject("areaActivities").optJSONArray("data");

                            for (int i = 0; i < runningSportArray.length(); i++) {
                                HistoryRunningSportEntry data = new HistoryRunningSportEntry();
                                data.setSportId(runningSportArray.optJSONObject(i).optInt("runningSportId"));
                                data.setCostTime(Integer.valueOf(runningSportArray.optJSONObject(i).optString("costTime")));
                                data.setDistance(Integer.valueOf(runningSportArray.optJSONObject(i).optString("distance")));
                                data.setKcalConsumed(Integer.valueOf(runningSportArray.optJSONObject(i).optString("kcalConsumed")));
                                data.setQualified(runningSportArray.optJSONObject(i).optBoolean("qualified"));
                                data.setStartTime(Long.valueOf(runningSportArray.optJSONObject(i).optString("startTime")));
                                data.setSportDate(String.valueOf(runningSportArray.optJSONObject(i).optString("sportDate")));
                                data.setEndAt(Long.valueOf(runningSportArray.optJSONObject(i).getString("endedAt")));
                                data.setSportName(runningSportArray.optJSONObject(i).optJSONObject("runningSport").optString("name"));
                                dataList.add(data);
                            }
                            for (int i = 0; i < areaSportArray.length(); i++) {
                                HistoryAreaSportEntry data = new HistoryAreaSportEntry();
                                data.setSportId(areaSportArray.optJSONObject(i).optInt("areaSportId"));
                                data.setCostTime(Integer.valueOf(areaSportArray.optJSONObject(i).optString("costTime")));
                                data.setKcalConsumed(Integer.valueOf(areaSportArray.optJSONObject(i).optString("kcalConsumed")));
                                data.setQualified(areaSportArray.optJSONObject(i).optBoolean("qualified"));
                                data.setStartTime(Long.valueOf(areaSportArray.optJSONObject(i).optString("startTime")));
                                data.setSportDate(String.valueOf(areaSportArray.optJSONObject(i).optString("sportDate")));
                                data.setEndAt(Long.valueOf(areaSportArray.optJSONObject(i).getString("endedAt")));
                                data.setSportName(areaSportArray.optJSONObject(i).optJSONObject("areaSport").optString("name"));
                                dataList.add(data);
                            }

//                            for (int i = 0; i < dataList.size(); i++) {
//                                Log.d(TAG, "dataList:" + dataList.get(i).toString());
//                            }
//
//                            Collections.sort(dataList, new MyComparator());
//
//
//                            for (int i = 0; i < dataList.size(); i++) {
//                                Log.d(TAG, "dataList:" + dataList.get(i).toString());
//                            }
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
                        Log.d(TAG, json + "haahahahhahaha");
                        JSONObject student = json.optJSONObject("data").optJSONObject("student");
                        try {
                            int accuRunningActivityCount = student.optInt("accuRunningActivityCount");
                            int accuAreaActivityCount = student.optInt("accuAreaActivityCount");

                            int qualifiedRunningActivityCount = student.optInt("qualifiedRunningActivityCount");
                            int qualifiedAreaActivityCount = student.optInt("qualifiedAreaActivityCount");

                            int runningActivityTimeCosted = student.optInt("runningActivityTimeCosted");
                            int areaActivityTimeCosted = student.optInt("areaActivityTimeCosted");

                            int runningActivityKcalConsumption = student.optInt("runningActivityKcalConsumption");
                            int areaActivityKcalConsumption = student.optInt("areaActivityKcalConsumption");

                            String totalActivityCount = String.valueOf(accuAreaActivityCount + accuRunningActivityCount);
                            String totalqualifiedActivityCount = String.valueOf(qualifiedAreaActivityCount + qualifiedRunningActivityCount);
                            String totalActivityTimeCosted = String.valueOf(runningActivityTimeCosted + areaActivityTimeCosted);
                            String toalActivityKcalConsumption = String.valueOf(runningActivityKcalConsumption + areaActivityKcalConsumption);

                            headView.setData("历史累计次数（次）", totalActivityCount, totalqualifiedActivityCount, totalActivityTimeCosted, toalActivityKcalConsumption);

                            JSONArray runningSportArray = student.optJSONObject("runningActivities").optJSONArray("data");
                            JSONArray areaSportArray = student.optJSONObject("areaActivities").optJSONArray("data");

                            for (int i = 0; i < runningSportArray.length(); i++) {
                                HistoryRunningSportEntry data = new HistoryRunningSportEntry();
                                data.setSportId(runningSportArray.optJSONObject(i).optInt("runningSportId"));
                                data.setCostTime(Integer.valueOf(runningSportArray.optJSONObject(i).optString("costTime")));
                                data.setDistance(Integer.valueOf(runningSportArray.optJSONObject(i).optString("distance")));
                                data.setKcalConsumed(Integer.valueOf(runningSportArray.optJSONObject(i).optString("kcalConsumed")));
                                data.setQualified(runningSportArray.optJSONObject(i).optBoolean("qualified"));
                                data.setStartTime(Long.valueOf(runningSportArray.optJSONObject(i).optString("startTime")));
                                data.setSportDate(String.valueOf(runningSportArray.optJSONObject(i).optString("sportDate")));
                                data.setEndAt(Long.valueOf(runningSportArray.optJSONObject(i).getString("endedAt")));
                                data.setSportName(runningSportArray.optJSONObject(i).optJSONObject("runningSport").optString("name"));
                                dataList.add(data);
                            }
                            for (int i = 0; i < areaSportArray.length(); i++) {
                                HistoryAreaSportEntry data = new HistoryAreaSportEntry();
                                data.setSportId(areaSportArray.optJSONObject(i).optInt("areaSportId"));
                                data.setCostTime(Integer.valueOf(areaSportArray.optJSONObject(i).optString("costTime")));
                                data.setKcalConsumed(Integer.valueOf(areaSportArray.optJSONObject(i).optString("kcalConsumed")));
                                data.setQualified(areaSportArray.optJSONObject(i).optBoolean("qualified"));
                                data.setStartTime(Long.valueOf(areaSportArray.optJSONObject(i).optString("startTime")));
                                data.setSportDate(String.valueOf(areaSportArray.optJSONObject(i).optString("sportDate")));
                                data.setEndAt(Long.valueOf(areaSportArray.optJSONObject(i).getString("endedAt")));
                                data.setSportName(areaSportArray.optJSONObject(i).optJSONObject("areaSport").optString("name"));
                                dataList.add(data);
                            }

//                            for (int i = 0; i < dataList.size(); i++) {
//                                Log.d(TAG, "dataList:" + dataList.get(i).toString());
//                            }
//
//                            Collections.sort(dataList, new MyComparator());
//
//
//                            for (int i = 0; i < dataList.size(); i++) {
//                                Log.d(TAG, "dataList:" + dataList.get(i).toString());
//                            }
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
                    }else {
                        emptyLayout.showEmptyOrError(errCode);
                        return false;
                    }
                }

            });
                        }*/
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
        //        if (type == AppConstant.THIS_WEEK) {
        //            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoWeek, pageSizeWeek, type, new JsonResponseCallback() {
        //                @Override
        //                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
        //                    if (errCode == 0) {
        //                        try {
        //                            pageCountWeek = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
        //                                    getString("pagesCount"));
        //                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentWeekActivities").
        //                                    getJSONArray("data");
        //                            for (int i = 0; i < historySportArray.length(); i++) {
        //                                HistoryRunningSportEntry data = new HistoryRunningSportEntry();
        //                                data.setRunningSportId(historySportArray.getJSONObject(i).getInt("id"));
        //                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
        //                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
        //                                data.setKcalConsumed(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
        //                                data.setCostTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
        //                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
        //                                data.setQualified(historySportArray.getJSONObject(i).getBoolean("qualified"));
        //                                dataList.add(data);
        //                            }
        //                            adapter.notifyDataSetChanged();
        //                            return true;
        //                        } catch (org.json.JSONException e) {
        //                            e.printStackTrace();
        //                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
        //                            return false;
        //                        }
        //                    } else {
        //                        return false;
        //                    }
        //                }
        //
        //            });
        //
        //            if (pageNoWeek != pageCountWeek) {
        //                pageNoWeek++;
        //                lrvLoadMore.loadMoreFinish(false, true);
        //            } else {
        //                lrvLoadMore.loadMoreFinish(false, false);
        //            }
        //        } else if (type == AppConstant.THIS_MONTH) {
        //            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoMonth, pageSizeMonth, type, new JsonResponseCallback() {
        //                @Override
        //                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
        //                    if (errCode == 0) {
        //                        try {
        //                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentMonthActivities").
        //                                    getJSONArray("data");
        //                            for (int i = 0; i < historySportArray.length(); i++) {
        //                                HistoryRunningSportEntry data = new HistoryRunningSportEntry();
        //                                data.setRunningSportId(historySportArray.getJSONObject(i).getInt("id"));
        //                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
        //                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
        //                                data.setKcalConsumed(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
        //                                data.setCostTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
        //                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
        //                                data.setQualified(historySportArray.getJSONObject(i).getBoolean("qualified"));
        //                                dataList.add(data);
        //                            }
        //                            adapter.notifyDataSetChanged();
        //                            return true;
        //                        } catch (org.json.JSONException e) {
        //                            e.printStackTrace();
        //                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
        //                            return false;
        //                        }
        //                    } else {
        //                        return false;
        //                    }
        //                }
        //
        //            });
        //
        //            if (pageNoMonth != pageCountMonth) {
        //                pageNoMonth++;
        //                lrvLoadMore.loadMoreFinish(false, true);
        //            } else {
        //                lrvLoadMore.loadMoreFinish(false, false);
        //            }
        //
        //        } else if (type == AppConstant.THIS_TERM) {
        //            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoTerm, pageSizeTerm, type, new JsonResponseCallback() {
        //                @Override
        //                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
        //                    if (errCode == 0) {
        //                        try {
        //                            pageCountTerm = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("currentTermActivities").
        //                                    getString("pagesCount"));
        //                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("currentTermActivities").
        //                                    getJSONArray("data");
        //                            for (int i = 0; i < historySportArray.length(); i++) {
        //                                HistoryRunningSportEntry data = new HistoryRunningSportEntry();
        //                                data.setRunningSportId(historySportArray.getJSONObject(i).getInt("id"));
        //                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
        //                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
        //                                data.setKcalConsumed(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
        //                                data.setCostTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
        //                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
        //                                data.setQualified(historySportArray.getJSONObject(i).getBoolean("qualified"));
        //                                dataList.add(data);
        //                            }
        //                            adapter.notifyDataSetChanged();
        //                            return true;
        //                        } catch (org.json.JSONException e) {
        //                            e.printStackTrace();
        //                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
        //                            return false;
        //                        }
        //                    } else {
        //                        return false;
        //                    }
        //                }
        //
        //            });
        //
        //            if (pageNoTerm != pageCountTerm) {
        //                pageNoTerm++;
        //                lrvLoadMore.loadMoreFinish(false, true);
        //            } else {
        //                lrvLoadMore.loadMoreFinish(false, false);
        //            }
        //
        //        } else {
        //            ServerInterface.instance().queryHistorySportsRecord(studentId, pageNoHistory, pageSizeTerm, type, new JsonResponseCallback() {
        //                @Override
        //                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
        //                    if (errCode == 0) {
        //                        try {
        //                            pageCountTerm = Integer.valueOf(json.optJSONObject("data").optJSONObject("student").optJSONObject("activities").
        //                                    getString("pagesCount"));
        //                            JSONArray historySportArray = json.optJSONObject("data").optJSONObject("student").optJSONObject("activities").
        //                                    getJSONArray("data");
        //                            for (int i = 0; i < historySportArray.length(); i++) {
        //                                HistoryRunningSportEntry data = new HistoryRunningSportEntry();
        //                                data.setRunningSportId(historySportArray.getJSONObject(i).getInt("id"));
        //                                data.setSportName(historySportArray.getJSONObject(i).optJSONObject("runningSport").getString("name"));
        //                                data.setStartTime(Long.valueOf(historySportArray.getJSONObject(i).getString("startTime")));
        //                                data.setKcalConsumed(Integer.valueOf(historySportArray.getJSONObject(i).getString("kcalConsumed")));
        //                                data.setCostTime(Integer.valueOf(historySportArray.getJSONObject(i).getString("costTime")));
        //                                data.setSportDistance(Integer.valueOf(historySportArray.getJSONObject(i).getString("distance")));
        //                                dataList.add(data);
        //                            }
        //                            adapter.notifyDataSetChanged();
        //                            return true;
        //                        } catch (org.json.JSONException e) {
        //                            e.printStackTrace();
        //                            Log.e(TAG, "queryHistorySportsRecord onJsonResponse e: " + e);
        //                            return false;
        //                        }
        //                    } else {
        //                        return false;
        //                    }
        //                }
        //
        //            });
        //
        //            if (pageNoHistory != pageCountHistory) {
        //                pageNoHistory++;
        //                lrvLoadMore.loadMoreFinish(false, true);
        //            } else {
        //                lrvLoadMore.loadMoreFinish(false, false);
        //            }
        //        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
