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
import com.application.library.widget.recycle.WrapRecyclerView;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.RankingData;
import com.tim.app.ui.adapter.RankingDataAdapter;
import com.tim.app.ui.view.RankingDataHeadView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜数据
 */
public class RankingDataFragment extends BaseFragment implements View.OnClickListener, LoadMoreHandler {

    public static final String TAG = "RankingDataFragment";
    private static final int PAGE_SIZE = 20;

    private View rootView;
    private LoadMoreRecycleViewContainer lrvLoadMore;
    private WrapRecyclerView wrvHistoryData;
    private EmptyLayout emptyLayout;

    private RankingDataAdapter adapter;
    private List<RankingData> dataList;

    private RankingDataHeadView headView;

    int type;
    private int universityId = 1;
    private int pageNoEnergy = 1;
    private int pageSizeEnergy = 6;
    private int pageCountEnergy = -1;

    private int pageNoTime = 1;
    private int pageSizeTime = 6;
    private int pageCountTime = -1;

    public static RankingDataFragment newInstance(int type) {
        RankingDataFragment fragment = new RankingDataFragment();
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
            wrvHistoryData.setOverScrollMode(View.OVER_SCROLL_NEVER);

            lrvLoadMore.useDefaultFooter(View.GONE);
            lrvLoadMore.setAutoLoadMore(true);
            lrvLoadMore.setLoadMoreHandler(this);

            emptyLayout = new EmptyLayout(getActivity(), lrvLoadMore);
            emptyLayout.showLoading();
            emptyLayout.setEmptyButtonShow(false);
            emptyLayout.setErrorButtonShow(true);
            emptyLayout.setEmptyDrawable(R.drawable.ic_empty_rank_data);
            emptyLayout.setEmptyText("当前没有排行榜数据");
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

            headView = (RankingDataHeadView) LayoutInflater.from(getActivity()).inflate(R.layout.rankingdata_head_view, null);
            wrvHistoryData.addHeaderView(headView);

            if (getArguments() != null) {
                type = getArguments().getInt("type");
            }

            dataList = new ArrayList<RankingData>();
            adapter = new RankingDataAdapter(getActivity(), dataList, type);
            wrvHistoryData.setAdapter(adapter);

        }
        initData();
        return rootView;
    }

    private void initData() {
        if (type == AppConstant.TYPE_COST_ENERGY) {
            ServerInterface.instance().queryCollegeSportsRankingData(universityId, pageSizeEnergy, pageNoEnergy++, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {

                        Log.d(TAG, "pageNoEnergy:" + pageNoEnergy);
                        JSONObject jsonObject = json.optJSONObject("data").optJSONObject("university")
                                .optJSONObject("kcalConsumptionRanking");
                        try {
                            pageCountEnergy = Integer.valueOf(jsonObject.optString("pagesCount"));
                            JSONArray rankingDataArray = jsonObject.optJSONArray("data");
                            RankingData headData[] = new RankingData[3];
                            for (int i = 0; i < 3; i++) {
                                headData[i] = new RankingData();
                                headData[i].setAvatar(rankingDataArray.getJSONObject(i).getString("avatarUrl"));
                                headData[i].setUserName(rankingDataArray.getJSONObject(i).getString("studentName"));
                                headData[i].setCostValue(Integer.valueOf(rankingDataArray.getJSONObject(i).getString("kcalConsumption")));
                                Log.d(TAG, "headData[i]:" + headData[i].toString());
                            }
                            headView.setData(headData, AppConstant.TYPE_COST_ENERGY);

                            for (int i = 3; i < rankingDataArray.length(); i++) {
                                RankingData data = new RankingData();
                                data.setAvatar("");
                                data.setUserName(rankingDataArray.getJSONObject(i).getString("studentName"));
                                data.setCostValue(Integer.valueOf(rankingDataArray.getJSONObject(i).getString("kcalConsumption")));
                                data.setAvatar(rankingDataArray.getJSONObject(i).getString("avatarUrl"));
                                dataList.add(data);
                                Log.d(TAG, "dataList:" + data);
                            }
                            adapter.notifyDataSetChanged();
                            //see #663_1
                            if (dataList.size() > 0) {
                                emptyLayout.showContent();
                                //} else if (dataList.size() > 0) {
                                // emptyLayout.showContent();
                                // }else{
                            }else{
                                emptyLayout.showEmpty();
                            }

                            return true;
                        } catch (org.json.JSONException e) {
                            emptyLayout.showEmpty();
                            e.printStackTrace();
                            Log.e(TAG, "queryCollegeSportsRankingData onJsonResponse e: " + e);
                            return false;
                        }
                    } else {
                        emptyLayout.showEmptyOrError(errCode);
                        return false;
                    }
                }

            });
        } else {
            ServerInterface.instance().queryCollegeSportsRankingData(universityId, pageSizeTime, pageNoTime++, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        JSONObject jsonObject = json.optJSONObject("data").optJSONObject("university")
                                .optJSONObject("timeCostedRanking");
                        try {
                            pageCountEnergy = Integer.valueOf(jsonObject.getString("pagesCount"));
                            JSONArray rankingDataArray = jsonObject.getJSONArray("data");
                            RankingData headData[] = new RankingData[3];
                            for (int i = 0; i < 3; i++) {
                                headData[i] = new RankingData();
                                headData[i].setAvatar(rankingDataArray.getJSONObject(i).getString("avatarUrl"));
                                headData[i].setUserName(rankingDataArray.getJSONObject(i).getString("studentName"));
                                headData[i].setCostValue(Integer.valueOf(rankingDataArray.getJSONObject(i).getString("timeCosted")));
                            }
                            headView.setData(headData, AppConstant.TYPE_COST_TIME);
                            for (int i = 3; i < rankingDataArray.length(); i++) {
                                RankingData data = new RankingData();
                                data.setAvatar("");
                                data.setUserName(rankingDataArray.getJSONObject(i).getString("studentName"));
                                data.setCostValue(Integer.valueOf(rankingDataArray.getJSONObject(i).getString("timeCosted")));
                                data.setAvatar(rankingDataArray.getJSONObject(i).getString("avatarUrl"));
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
                            emptyLayout.showEmpty();
                            e.printStackTrace();
                            Log.e(TAG, "queryCollegeSportsRankingData onJsonResponse e: " + e);
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
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onLoadMore(LoadMoreContainer loadMoreContainer) {
        if (type == AppConstant.TYPE_COST_ENERGY) {
            ServerInterface.instance().queryCollegeSportsRankingData(universityId, pageSizeEnergy, pageNoEnergy++, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            JSONArray rankingDataArray = json.optJSONObject("data").optJSONObject("university").optJSONObject("kcalConsumptionRanking").
                                    getJSONArray("data");
                            for (int i = 0; i < rankingDataArray.length(); i++) {
                                RankingData data = new RankingData();
                                data.setAvatar("");
                                data.setUserName(rankingDataArray.getJSONObject(i).getString("studentName"));
                                data.setCostValue(Integer.valueOf(rankingDataArray.getJSONObject(i).getString("kcalConsumption")));
                                data.setAvatar(rankingDataArray.getJSONObject(i).getString("avatarUrl"));
                                dataList.add(data);
                                Log.d(TAG, "onLoadMore  :    data:" + data);
                            }
                            adapter.notifyDataSetChanged();
                            return true;
                        } catch (org.json.JSONException e) {
                            Log.e(TAG, "queryCurTermData onJsonResponse e: ");
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

            });

            if (pageNoEnergy != pageCountEnergy) {
                lrvLoadMore.loadMoreFinish(false, true);
            } else {
                lrvLoadMore.loadMoreFinish(false, false);
            }
        } else {
            ServerInterface.instance().queryCollegeSportsRankingData(universityId, pageSizeEnergy, pageNoTime++, type, new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            JSONArray rankingDataArray = json.optJSONObject("data").optJSONObject("university").optJSONObject("timeCostedRanking").
                                    getJSONArray("data");
                            for (int i = 0; i < rankingDataArray.length(); i++) {
                                RankingData data = new RankingData();
                                data.setAvatar("");
                                data.setUserName(rankingDataArray.getJSONObject(i).getString("studentName"));
                                data.setCostValue(Integer.valueOf(rankingDataArray.getJSONObject(i).getString("timeCosted")));
                                data.setAvatar(rankingDataArray.getJSONObject(i).getString("avatarUrl"));
                                dataList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            return true;
                        } catch (org.json.JSONException e) {
                            Log.e(TAG, "queryCurTermData onJsonResponse e: ");
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

            });
            if (pageNoTime != pageCountTime) {
                lrvLoadMore.loadMoreFinish(false, true);
            } else {
                lrvLoadMore.loadMoreFinish(false, false);
            }
        }
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
