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

    public static final String INIT_PUSH = "runningActivities";//

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
     * 提交运动数据
     *
     * @param tag
     * @param projectId  运动项目ID
     * @param studentId  学生ID
     * @param distance   距离
     * @param costTime   花费时间
     * @param targetTime 目标时间
     * @param startTime  开始时间
     * @param callback
     */
    public void postRunningActDate(String tag, int projectId, int studentId, int distance, long costTime, long targetTime,
                                   long startTime, ResponseCallback callback) {
        String url = API_SCHEME + INIT_PUSH;
        HashMap params = new HashMap();
        params.put("projectId", projectId);
        params.put("studentId", studentId);
        params.put("distance", distance);
        params.put("costTime", costTime);
        params.put("targetTime", targetTime);
        params.put("startTime", startTime);
        Log.d(TAG, "params: " + params);
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }

    public void query(String queryStr, ResponseCallback callback) {
        String url = API_SCHEME + QUERY_INTERFACE;
        HttpHeaders headers = NetworkInterface.instance().getCommonHeaders();
        HashMap params = new HashMap();
        Log.d(TAG, "queryRunningProjects: headers.toString()" + headers.toString());

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

    //    public void queryRunningProjects(int universityId, ResponseCallback callback) {
    //        queryStr = "{runningProjects(universityId:" + universityId + ")" +
    //                "{id name qualifiedDistance qualifiedCostTime}}";
    //        query(queryStr, callback);
    //    }
    public void queryRunningProjects(int universityId, ResponseCallback callback) {
        queryStr = "{\n" +
                "  runningProjects(universityId: 1) {\n" +
                "    id\n" +
                "    name\n" +
                "    qualifiedDistance\n" +
                "    qualifiedCostTime\n" +
                "  }\n" +
                "}\n";
        query(queryStr, callback);
    }

    public void queryCurTermData(int universityId, int studentId, ResponseCallback callback) {
        String queryStr = "{\n" +
                "\tuniversity(id:" + universityId + ") {\n" +
                "       currentTerm {\n" +
                "        termSportsTask {\n" +
                "          targetSportsTimes\n" +
                "        }\n" +
                "      }\n" +
                "  }\n" +
                "  \n" +
                "  student(id:1) {\n" +
                "    caloriesConsumption\n" +
                "    timeCosted\n" +
                "    currentTermQualifiedActivityCount\n" +
                "    currentTermActivityCount\n" +
                "  }\n" +
                "}";
        query(queryStr, callback);
    }

    public void queryCollegeSportsRankingData(int universityId, int pageSize, int pageNo, int type, ResponseCallback callback) {
        if (type == AppConstant.TYPE_COST_ENERGY) {
            queryStr = "{    \n" +
                    "  university(id:1) {\n" +
                    "\t\tcaloriesConsumptionRanking (pageSize:" + pageSize + " pageNumber:" + pageNo + "){\n" +
                    "      pagesCount\n" +
                    "      data{\n" +
                    "      studentId\n" +
                    "      studentName\n" +
                    "      avatarUrl\n" +
                    "      caloriesConsumption\n" +
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
            queryStr = "{    \n" +
                    "\tstudent(id:" + studentId + ") {\n" +
                    "    currentWeekActivities(pageNumber:" + pageNo + ", pageSize:" + pageSize + "){\n" +
                    "      pagesCount\n" +
                    "      data {\n" +
                    "        projectId\n" +
                    "        costTime\n" +
                    "        caloriesConsumed\n" +
                    "        startTime\n" +
                    "        distance\n" +
                    "        qualified\n" +
                    "        runningProject{\n" +
                    "          name\n" +
                    "         }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        } else if (type == AppConstant.THIS_MONTH) {
            queryStr = "{    \n" +
                    "\tstudent(id:" + studentId + ") {\n" +
                    "    currentMonthActivities(pageNumber:" + pageNo + ", pageSize:" + pageSize + "){\n" +
                    "      pagesCount\n" +
                    "      data {\n" +
                    "        projectId\n" +
                    "        costTime\n" +
                    "        caloriesConsumed\n" +
                    "        startTime\n" +
                    "        distance\n" +
                    "        qualified\n" +
                    "        runningProject{\n" +
                    "          name\n" +
                    "         }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        } else {
            queryStr = "{    \n" +
                    "\tstudent(id:" + studentId + ") {\n" +
                    "    currentTermActivities(pageNumber:" + pageNo + ", pageSize:" + pageSize + "){\n" +
                    "      pagesCount\n" +
                    "      data {\n" +
                    "        projectId\n" +
                    "        costTime\n" +
                    "        caloriesConsumed\n" +
                    "        startTime\n" +
                    "        distance\n" +
                    "        qualified\n" +
                    "        runningProject{\n" +
                    "          name\n" +
                    "         }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
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

}
