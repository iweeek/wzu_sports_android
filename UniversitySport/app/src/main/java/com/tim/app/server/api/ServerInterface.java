package com.tim.app.server.api;

import android.util.Log;

import com.application.library.net.ResponseCallback;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.model.HttpHeaders;
import com.tim.app.constant.AppConstant;
import com.tim.app.server.net.HttpMethod;
import com.tim.app.server.net.NetworkInterface;

import java.util.HashMap;

import static com.lzy.okhttputils.utils.OkLogger.tag;

/**
 * 接口
 */
public class ServerInterface {
    private static final String TAG = "ServerInterface";

    private static volatile ServerInterface instance = null;

    private static final String API_SCHEME = "api/";//扩展字段

    public static final String RUNNING_ACTIVITIES = "runningActivities";

    public static final String RUNNING_ACTIVITY_DATA = "runningActivityData";

    public static final String AREA_ACTIVITIES = "areaActivities";

    public static final String AREA_ACTIVITY_DATA = "areaActivityData";

    public static final String QUERY_INTERFACE = "graphql/query";

    private String queryStr;

    private ServerInterface() {

    }

    public static ServerInterface instance() {
        if (instance == null) {
            synchronized (ServerInterface.class) {
                if (instance == null) {
                    instance = new ServerInterface();
                }
            }
        }
        return instance;
    }

    /**
     * {@link com.tim.app.ui.activity.SportDetailActivity} 调用
     *
     * @param tag
     * @param runningSportId
     * @param studentId
     * @param startTime
     * @param callback
     */
    public void runningActivitiesStart(String tag, int runningSportId, int studentId, long startTime, ResponseCallback callback) {
        String url = API_SCHEME + RUNNING_ACTIVITIES + "/start";
        HashMap params = new HashMap();
        params.put("runningSportId", runningSportId);
        params.put("studentId", studentId);
        params.put("startTime", startTime);
        Log.d(TAG, "params: " + params);
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }

    /**
     * 结束跑步运动
     * {@link com.tim.app.ui.activity.SportDetailActivity} 调用
     *
     * @param tag
     * @param distance           距离
     * @param costTime           花费时间
     * @param targetFinishedTime 目标时间
     * @param callback
     */
    public void runningActivitiesEnd(String tag, int id, int distance, int stepCount, long costTime, long targetFinishedTime,
                                     ResponseCallback callback) {
        String url = API_SCHEME + RUNNING_ACTIVITIES + "/end";
        HashMap params = new HashMap();
        params.put("id", id);
        params.put("distance", distance);
        params.put("stepCount", stepCount);
        params.put("costTime", costTime);
        params.put("targetFinishedTime", targetFinishedTime);
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }

    /**
     * 向服务器提交数据
     * {@link com.tim.app.ui.activity.SportDetailActivity}  调用
     *
     * @param tag
     * @param activityId
     * @param acquisitionTime
     * @param stepCount
     * @param distance
     * @param longitude
     * @param latitude
     * @param locationType
     * @param isNormal
     * @param callback
     */
    public void runningActivityData(String tag, int activityId, long acquisitionTime, int stepCount, int distance, double longitude,
                                    double latitude, int locationType, boolean isNormal, ResponseCallback callback) {
        String url = API_SCHEME + RUNNING_ACTIVITY_DATA;
        HashMap params = new HashMap();
        params.put("activityId", activityId);
        //        params.put("acquisitionTime", acquisitionTime);
        params.put("stepCount", stepCount);
        params.put("distance", distance);
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        params.put("locationType", locationType);
        params.put("isNormal", isNormal);
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }

    /**
     * 创建本次区域运动项目
     *
     * @param tag
     * @param areaSportId
     * @param studentId
     * @param callback
     */
    public void areaActivities(String tag, int areaSportId, int studentId, ResponseCallback callback) {
        String url = API_SCHEME + AREA_ACTIVITIES;
        HashMap params = new HashMap();
        params.put("areaSportId", areaSportId);
        params.put("studentId", studentId);
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }

    /**
     * 结束本次区域运动
     *
     * @param tag
     * @param areaSportRecordId
     * @param callback
     */
    public void areaActivitiesEnd(String tag, int areaSportRecordId, ResponseCallback callback) {
        String url = API_SCHEME + AREA_ACTIVITIES + "/" + areaSportRecordId;
        HashMap params = new HashMap();
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }

    /**
     * 提交区域运动数据
     *
     * @param tag
     * @param areaSportRecordId
     * @param callback
     */
    public void areaActivityData(String tag, int areaSportRecordId, double longitude,
                                 double latitude, int locationType, ResponseCallback callback) {
        String url = API_SCHEME + AREA_ACTIVITY_DATA;
        HashMap params = new HashMap();
        params.put("activityId", areaSportRecordId);
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        params.put("locationType", locationType);
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }


    public void query(String queryStr, ResponseCallback callback) {
        String url = API_SCHEME + QUERY_INTERFACE;
        HttpHeaders headers = NetworkInterface.instance().getCommonHeaders();
        HashMap params = new HashMap();
        Log.d(TAG, "queryRunningSports: headers.toString()" + headers.toString());

        //        headers.put("Content-Type", "application/json;charset=UTF-8");
        //        Log.d(TAG, "headers: " + headers);
        //        headers.remove("content-type");
        //        Log.d(TAG, "headers: " + headers);
        //        headers.put("content-type", "x-www-form-urlencoded");
        //        Log.d(TAG, "headers: " + headers);
        params.put("query", queryStr);
        Log.d(TAG, "params: " + params);
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }


    /**
     * MainActivity 调用
     *
     * @param universityId
     * @param callback
     */
    public void queryRunningSports(int universityId, ResponseCallback callback) {
        queryStr = "{\n" +
                "  runningSports(universityId: 1) {\n" +
                "   acquisitionInterval\n" +
                "   participantNum\n" +
                "    id\n" +
                "    name\n" +
                "    qualifiedDistance\n" +
                "    qualifiedCostTime\n" +
                "  }\n" +
                "}\n";
        query(queryStr, callback);
    }

    /**
     * SportResultActivity 调用
     *
     * @param activityId
     * @param callback
     */
    public void queryRunningActivity(int activityId, ResponseCallback callback) {
        queryStr = "{\n" +
                "\trunningActivity(id:" + activityId + ") {\n" +
                "   distance\n" +
                "   costTime\n" +
                "   qualified\n" +
                "   qualifiedDistance" +
                "   qualifiedCostTime" +
                "   kcalConsumed" +
                "   runningSport {\n" +
                "       name\n" +
                "    }\n" +
                "    data{\n" +
                "      longitude\n" +
                "      latitude\n" +
                "      isNormal\n" +
                "      locationType\n" +
                "      stepCount\n" +
                "      distance\n" +
                "      acquisitionTime\n" +
                "    }\n" +
                "  }\n" +
                "}";

        query(queryStr, callback);
    }

    /**
     * MainActivity 调用
     *
     * @param universityId
     * @param studentId
     * @param callback
     */
    public void queryCurTermData(int universityId, int studentId, ResponseCallback callback) {
        String queryStr = "{\n" +
                "  student(id: "+studentId+") {\n" +
                "    areaActivityKcalConsumption\n" +
                "    runningActivityKcalConsumption\n" +
                "    areaActivityTimeCosted\n" +
                "    runningActivityTimeCosted\n" +
                "    currentTermQualifiedAreaActivityCount\n" +
                "    currentTermQualifiedRunningActivityCount\n" +
                "    currentTermAreaActivityCount\n" +
                "    currentTermRunningActivityCount\n" +
                "  }\n" +
                "  university(id: "+universityId+") {\n" +
                "    currentTerm {\n" +
                "      termSportsTask {\n" +
                "        targetSportsTimes\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        query(queryStr, callback);
    }

    public void queryCollegeSportsRankingData(int universityId, int pageSize, int pageNo, int type, ResponseCallback callback) {
        if (type == AppConstant.TYPE_COST_ENERGY) {
            queryStr = "{    \n" +
                    "  university(id:1) {\n" +
                    "\t\tkcalConsumptionRanking (pageSize:" + pageSize + " pageNumber:" + pageNo + "){\n" +
                    "      pagesCount\n" +
                    "      data{\n" +
                    "      studentId\n" +
                    "      studentName\n" +
                    "      avatarUrl\n" +
                    "      kcalConsumption\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        } else {
            queryStr = "{    \n" +
                    "  university(id:1) {\n" +
                    "\t\ttimeCostedRanking (pageSize:" + pageSize + " pageNumber:" + pageNo + "){\n" +
                    "      pagesCount\n" +
                    "      data{\n" +
                    "      studentId\n" +
                    "      studentName\n" +
                    "      timeCosted\n" +
                    "      avatarUrl\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }
        query(queryStr, callback);
    }

    public void queryHistorySportsRecord(int studentId, int pageNo, int pageSize, int type, ResponseCallback callback) {
        if (type == AppConstant.THIS_WEEK) {
            queryStr = "{\n" +
                    "  student(id: 2) {\n" +
                    "    accuRunningActivityCount(timeRange: CURRENT_WEEK)\n" +
                    "    accuAreaActivityCount(timeRange: CURRENT_WEEK)\n" +
                    "    qualifiedRunningActivityCount(timeRange: CURRENT_WEEK)\n" +
                    "    qualifiedAreaActivityCount(timeRange: CURRENT_WEEK)\n" +
                    "    runningActivityTimeCosted(timeRange: CURRENT_WEEK)\n" +
                    "    areaActivityTimeCosted(timeRange: CURRENT_WEEK)\n" +
                    "    runningActivityKcalConsumption(timeRange: CURRENT_WEEK)\n" +
                    "    areaActivityKcalConsumption(timeRange: CURRENT_WEEK)\n" +
                    "    runningActivities(startDate: \"2017-07-01\", endDate: \"2017-08-01\") {\n" +
                    "      data {\n" +
                    "        runningSportId\n" +
                    "        costTime\n" +
                    "        distance\n" +
                    "        kcalConsumed\n" +
                    "        qualified\n" +
                    "        startTime\n" +
                    "        sportDate\n" +
                    "        endedAt\n" +
                    "        runningSport {\n" +
                    "          name\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "    areaActivities(startDate: \"2017-07-01\", endDate: \"2017-08-01\") {\n" +
                    "      data {\n" +
                    "        areaSportId\n" +
                    "        costTime\n" +
                    "        kcalConsumed\n" +
                    "        qualified\n" +
                    "        startTime\n" +
                    "        sportDate\n" +
                    "        endedAt\n" +
                    "        areaSport {\n" +
                    "          name\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        } else if (type == AppConstant.THIS_MONTH) {
            queryStr = "{\n" +
                    "  student(id: 2) {\n" +
                    "    accuRunningActivityCount(timeRange: CURRENT_MONTH)\n" +
                    "    accuAreaActivityCount(timeRange: CURRENT_MONTH)\n" +
                    "    qualifiedRunningActivityCount(timeRange: CURRENT_MONTH)\n" +
                    "    qualifiedAreaActivityCount(timeRange: CURRENT_MONTH)\n" +
                    "    runningActivityTimeCosted(timeRange: CURRENT_MONTH)\n" +
                    "    areaActivityTimeCosted(timeRange: CURRENT_MONTH)\n" +
                    "    runningActivityKcalConsumption(timeRange: CURRENT_MONTH)\n" +
                    "    areaActivityKcalConsumption(timeRange: CURRENT_MONTH)\n" +
                    "    runningActivities(startDate: \"2017-07-01\", endDate: \"2017-08-01\") {\n" +
                    "      data {\n" +
                    "        runningSportId\n" +
                    "        costTime\n" +
                    "        distance\n" +
                    "        kcalConsumed\n" +
                    "        qualified\n" +
                    "        startTime\n" +
                    "        sportDate\n" +
                    "        endedAt\n" +
                    "        runningSport {\n" +
                    "          name\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "    areaActivities(startDate: \"2017-07-01\", endDate: \"2017-08-01\") {\n" +
                    "      data {\n" +
                    "        areaSportId\n" +
                    "        costTime\n" +
                    "        kcalConsumed\n" +
                    "        qualified\n" +
                    "        startTime\n" +
                    "        sportDate\n" +
                    "        endedAt\n" +
                    "        areaSport {\n" +
                    "          name\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n";
        } else if (type == AppConstant.THIS_TERM) {
            queryStr = "{\n" +
                    "  student(id: 2) {\n" +
                    "    accuRunningActivityCount(timeRange: CURRENT_TERM)\n" +
                    "    accuAreaActivityCount(timeRange: CURRENT_TERM)\n" +
                    "    qualifiedRunningActivityCount(timeRange: CURRENT_TERM)\n" +
                    "    qualifiedAreaActivityCount(timeRange: CURRENT_TERM)\n" +
                    "    runningActivityTimeCosted(timeRange: CURRENT_TERM)\n" +
                    "    areaActivityTimeCosted(timeRange: CURRENT_TERM)\n" +
                    "    runningActivityKcalConsumption(timeRange: CURRENT_TERM)\n" +
                    "    areaActivityKcalConsumption(timeRange: CURRENT_TERM)\n" +
                    "    runningActivities(startDate: \"2017-07-01\", endDate: \"2017-08-01\") {\n" +
                    "      data {\n" +
                    "        runningSportId\n" +
                    "        costTime\n" +
                    "        distance\n" +
                    "        kcalConsumed\n" +
                    "        qualified\n" +
                    "        startTime\n" +
                    "        sportDate\n" +
                    "        endedAt\n" +
                    "        runningSport {\n" +
                    "          name\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "    areaActivities(startDate: \"2017-07-01\", endDate: \"2017-08-01\") {\n" +
                    "      data {\n" +
                    "        areaSportId\n" +
                    "        costTime\n" +
                    "        kcalConsumed\n" +
                    "        qualified\n" +
                    "        startTime\n" +
                    "        sportDate\n" +
                    "        endedAt\n" +
                    "        areaSport {\n" +
                    "          name\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n";
        } else {
            queryStr = "{\n" +
                    "  student(id: 2) {\n" +
                    "    accuRunningActivityCount(timeRange: CURRENT_TERM)\n" +
                    "    accuAreaActivityCount(timeRange: CURRENT_TERM)\n" +
                    "    qualifiedRunningActivityCount(timeRange: CURRENT_TERM)\n" +
                    "    qualifiedAreaActivityCount(timeRange: CURRENT_TERM)\n" +
                    "    runningActivityTimeCosted(timeRange: CURRENT_TERM)\n" +
                    "    areaActivityTimeCosted(timeRange: CURRENT_TERM)\n" +
                    "    runningActivityKcalConsumption(timeRange: CURRENT_TERM)\n" +
                    "    areaActivityKcalConsumption(timeRange: CURRENT_TERM)\n" +
                    "    runningActivities(startDate: \"2017-07-01\", endDate: \"2017-08-01\") {\n" +
                    "      data {\n" +
                    "        runningSportId\n" +
                    "        costTime\n" +
                    "        distance\n" +
                    "        kcalConsumed\n" +
                    "        qualified\n" +
                    "        startTime\n" +
                    "        sportDate\n" +
                    "        endedAt\n" +
                    "        runningSport {\n" +
                    "          name\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "    areaActivities(startDate: \"2017-07-01\", endDate: \"2017-08-01\") {\n" +
                    "      data {\n" +
                    "        areaSportId\n" +
                    "        costTime\n" +
                    "        kcalConsumed\n" +
                    "        qualified\n" +
                    "        startTime\n" +
                    "        sportDate\n" +
                    "        endedAt\n" +
                    "        areaSport {\n" +
                    "          name\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n";
//            queryStr = "{    \n" +
//                    "\tstudent(id:" + studentId + ") {\n" +
//                    "qualifiedActivityCount\n" +
//                    "timeCosted\n" +
//                    "caloriesConsumption\n" +
//                    "    activities(pageNumber:" + pageNo + ", pageSize:" + pageSize + "){\n" +
//                    "      pagesCount\n" +
//                    "      dataCount\n" +
//                    "      data {\n" +
//                    "        id\n" +
//                    "        runningSportId\n" +
//                    "        costTime\n" +
//                    "        kcalConsumed\n" +
//                    "        startTime\n" +
//                    "        distance\n" +
//                    "        qualified\n" +
//                    "        runningSport{\n" +
//                    "          name\n" +
//                    "         }\n" +
//                    "      }\n" +
//                    "    }\n" +
//                    "  }\n" +
//                    "}";
        }
        query(queryStr, callback);
    }

    public void queryFitnessCheckData(int studentId, ResponseCallback callback) {
        String queryStr = "{\n" +
                "  student(id:" + studentId + ") {\n" +
                "    fitnessCheckDatas {\n" +
                "      height\n" +
                "      weight\n" +
                "      lungCapacity\n" +
                "      bmi\n" +
                "    }\n" +
                "  }\n" +
                "}";
        query(queryStr, callback);
    }

    public void queryAreaFixedLocationListData(int universityId, ResponseCallback callback) {
        String queryStr = "{\n" +
                "  fixLocationOutdoorSportPoints(universityId: 1)  {\n" +
                "    id\n" +
                "    name\n" +
                "    latitude\n" +
                "    longitude\n" +
                "    radius\n" +
                "    qualifiedCostTime\n" +
                "    universityId\n" +
                "  }\n" +
                "}\n";
        query(queryStr, callback);
    }

    /**
     * 首页查询
     * {@link com.tim.app.ui.activity.MainActivity}调用
     *
     * @param universityId
     * @param callback
     */
    public void queryAreaSportsData(int universityId, ResponseCallback callback) {
        String queryStr = "{\n" +
                "  query: areaSports(universityId:1){\n" +
                "    id\n" +
                "    name\n" +
                "    isEnable\n" +
                "    qualifiedCostTime\n" +
                "    acquisitionInterval\n" +
                "    universityId\n" +
                "  }\n" +
                "}\n" +
                "\n";
        query(queryStr, callback);
    }

}
