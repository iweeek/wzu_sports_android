package com.tim.app.sport;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tim.app.server.entry.db.RunningActivityRecord;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nimon on 2017/6/18.
 */

public class RunningActivityCallback implements SQLite.TableInterface {

    public RunningActivityCallback() {
    }

    private static RunningActivityCallback instance;

    public static RunningActivityCallback getInstance() {
        if (instance == null) {
            instance = new RunningActivityCallback();
        }
        return instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    // db config
    ///////////////////////////////////////////////////////////////////////////
    public static final String DB_NAME = "wzu_sports_record";
    public static final int DB_VERSION = 1;

    ///////////////////////////////////////////////////////////////////////////
    // table phrase
    ///////////////////////////////////////////////////////////////////////////
    public static final String TABLE_RUNNING_ACTIVITY = "wzsport_running_activity";
    public static final String KEY_ID = "id";
    public static final String KEY_RUNNING_SPORT_ID = "runningSportId";
    public static final String KEY_END_RUNNING_SPORT_ID = "endRunningSportId";
    public static final String KEY_RUNNING_STUDENT_ID = "studentId";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_STEP_COUNT = "stepCount";
    public static final String KEY_COST_TIME = "costTime";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_STEP_PER_SECOND = "stepPerSecond";
    public static final String KEY_DISTANCE_PER_STEP = "distancePerStep";
    public static final String KEY_TARGET_FINISHED_TIME = "targetFinishedTime";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_KCAL_CONSUMED = "kcalConsumed";
    public static final String KEY_QUALIFIED = "qualified";
    public static final String KEY_IS_VALID = "isValid";
    public static final String KEY_IS_VERIFIED = "isVerified";
    public static final String KEY_QUALIFIED_DISTANCE = "qualifiedDistance";
    public static final String KEY_QUALIFIED_COST_TIME = "qualifiedCostTime";
    public static final String KEY_MIN_COST_TIME = "minCostTime";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";
    public static final String KEY_ENDED_AT = "endedAt";
    public static final String KEY_ENDED_BY = "endedBy";

    /*
    //mysql
    CREATE TABLE `wzsport_running_activity` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `running_sport_id` bigint(11) unsigned NOT NULL COMMENT '活动开始时，活动所属的运动项目Id',
  `end_running_sport_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '活动结束时，活动所属的运动项目Id',
  `student_id` bigint(11) unsigned NOT NULL COMMENT '学生Id',
  `distance` int(11) NOT NULL DEFAULT '0' COMMENT '运动距离',
  `step_count` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '步数',
  `cost_time` int(11) NOT NULL DEFAULT '0' COMMENT '消耗时间',
  `speed` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '速度',
  `step_per_second` decimal(4,2) NOT NULL DEFAULT '0.00' COMMENT '每秒步数',
  `distance_per_step` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '每步的距离、步幅',
  `target_finished_time` int(11) NOT NULL DEFAULT '0',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `kcal_consumed` int(11) NOT NULL DEFAULT '0',
  `qualified` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `is_valid` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效，布尔值，0表示无效，1表示有效',
  `is_verified` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否通过审核，0，未通过；1，通过。',
  `qualified_distance` int(11) unsigned NOT NULL DEFAULT '0',
  `qualified_cost_time` int(11) unsigned NOT NULL DEFAULT '0',
  `min_cost_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最低消耗时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ended_at` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ended_by` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：客户端；1：服务器',
  PRIMARY KEY (`id`),
  KEY `i_studentId` (`student_id`) USING BTREE,
  KEY `i_runningSportId` (`running_sport_id`) USING BTREE
)
     */
    public static final String TABLE_RUNNING_SQL = "CREATE TABLE " + TABLE_RUNNING_ACTIVITY + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_RUNNING_SPORT_ID + " BIGINT(11), " +
            KEY_END_RUNNING_SPORT_ID + " BIGINT, " +
            KEY_RUNNING_STUDENT_ID + " BIGINT(11), " +
            KEY_DISTANCE + " INTEGER, " +
            KEY_STEP_COUNT + " INTEGER, " +
            KEY_COST_TIME + " INTEGER, " +
            KEY_SPEED + " DOUBLE, " +
            KEY_STEP_PER_SECOND + " DOUBLE, " +
            KEY_DISTANCE_PER_STEP + " DOUBLE, " +
            KEY_TARGET_FINISHED_TIME + " INTEGER, " +
            KEY_START_TIME + " INTEGER, " +
            KEY_KCAL_CONSUMED + " INTEGER, " +
            KEY_QUALIFIED + " INTEGER, " +
            KEY_IS_VALID + " INTEGER, " +
            KEY_IS_VERIFIED + " INTEGER, " +
            KEY_QUALIFIED_DISTANCE + " INTEGER, " +
            KEY_QUALIFIED_COST_TIME + " INTEGER, " +
            KEY_MIN_COST_TIME + " INTEGER, " +
            KEY_CREATED_AT + " INTEGER, " +
            KEY_UPDATED_AT + " INTEGER, " +
            KEY_ENDED_AT + " INTEGER, " +
            KEY_ENDED_BY + " INTEGER " +
            ")";

    @Override
    public String getDBName() {
        return DB_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_RUNNING_ACTIVITY;
    }

    @Override
    public int getDBVersion() {
        return DB_VERSION;
    }

    @Override
    public void doUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 0:
                db.execSQL(TABLE_RUNNING_SQL); // 升级操作；
            case 1:
                break;
            default:
                break;
        }
    }

    @Override
    public List<String> createTableSql() {
        return Arrays.asList(
                TABLE_RUNNING_SQL
        );
    }

    /**
     * 根据对象中的字段填充 {@link ContentValues} 对象
     * @param tableName 表名
     * @param t 对象
     * @param values 键值对
     * @param <T>
     */
    @Override
    public <T> void assignValuesByEntity(String tableName, T t, ContentValues values) {
        switch (tableName){
            case TABLE_RUNNING_ACTIVITY:
                if(t instanceof RunningActivityRecord){
                    RunningActivityRecord record = (RunningActivityRecord) t;
                    values.put(KEY_ID, record.getId());
                    values.put(KEY_RUNNING_SPORT_ID, record.getRunningSportId());
                    values.put(KEY_END_RUNNING_SPORT_ID, record.getEndRunningSportId());
                    values.put(KEY_RUNNING_STUDENT_ID, record.getStudentId());
                    values.put(KEY_DISTANCE, record.getDistance());
                    values.put(KEY_STEP_COUNT, record.getStepCount());
                    values.put(KEY_COST_TIME, record.getCostTime());
                    values.put(KEY_SPEED, record.getSpeed());
                    values.put(KEY_STEP_PER_SECOND, record.getStepPerSecond());
                    values.put(KEY_DISTANCE_PER_STEP, record.getDistancePerStep());
                    values.put(KEY_TARGET_FINISHED_TIME, record.getTargetFinishedTime());
                    values.put(KEY_START_TIME, record.getStartTime());
                    values.put(KEY_KCAL_CONSUMED, record.getKcalConsumed());
                    values.put(KEY_QUALIFIED, record.getQualified());
                    values.put(KEY_IS_VALID, record.getIsValid());
                    values.put(KEY_IS_VERIFIED, record.getIsVerified());
                    values.put(KEY_QUALIFIED_DISTANCE, record.getQualifiedDistance());
                    values.put(KEY_QUALIFIED_COST_TIME, record.getQualifiedCostTime());
                    values.put(KEY_MIN_COST_TIME, record.getMinCostTime());
                    values.put(KEY_CREATED_AT, record.getCreatedAt());
                    values.put(KEY_UPDATED_AT, record.getUpdatedAt());
                    values.put(KEY_ENDED_AT, record.getEndedAt());
                    values.put(KEY_ENDED_BY, record.getEndedBy());
                }
                break;

        }
    }

    @Override
    public long saveRecord(SQLiteDatabase db, ContentValues values) {
        long result = db.insert(RunningActivityCallback.TABLE_RUNNING_ACTIVITY, null, values);
        return 0;
    }

    @Override
    public long updateRecord(SQLiteDatabase db, ContentValues values) {
        long result = db.update(RunningActivityCallback.TABLE_RUNNING_ACTIVITY,
                values, "id = ?",  new String[] {values.getAsString("id")} );
        return result;
    }

    @Override
    public RunningActivityRecord getEntityByCursor(String tableName, Cursor c) {
        switch (tableName) {
            case TABLE_RUNNING_ACTIVITY:
                return new RunningActivityRecord(
                        c.getLong(0),
                        c.getLong(1),
                        c.getLong(2),
                        c.getLong(3),
                        c.getInt(4),
                        c.getInt(5),
                        c.getInt(6),
                        c.getLong(7),
                        c.getLong(8),
                        c.getLong(9),
                        c.getInt(10),
                        c.getInt(11),
                        c.getInt(12),
                        c.getInt(13),
                        c.getInt(14),
                        c.getInt(15),
                        c.getInt(16),
                        c.getInt(17),
                        c.getInt(18),
                        c.getInt(19),
                        c.getInt(20),
                        c.getInt(21),
                        c.getInt(22)
                        );
        }
        return null;
    }
}
