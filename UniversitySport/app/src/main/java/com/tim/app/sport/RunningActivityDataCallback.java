package com.tim.app.sport;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tim.app.server.entry.db.RunningActivityDataRecord;

import java.util.Arrays;
import java.util.List;

/**
 * @创建者 倪军
 * @创建时间 04/12/2017
 * @描述
 */

public class RunningActivityDataCallback implements SQLite.TableInterface{

    public RunningActivityDataCallback() {
    }

    private static RunningActivityDataCallback instance;

    public static RunningActivityDataCallback getInstance() {
        if (instance == null) {
            instance = new RunningActivityDataCallback();
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
    public static final String TABLE_RUNNING_ACTIVITY_DATA = "wzsport_running_activity_data";
    public static final String KEY_ID = "id";
    public static final String KEY_ACTIVITY_ID = "activityId";
    public static final String KEY_ACQUISITION_TIME = "acquisitionTime";
    public static final String KEY_STEP_COUNT = "stepCount";
    public static final String KEY_STEP_COUNT_CAL = "stepCountCal";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_DISTANCE_PER_STEP = "distancePerStep";
    public static final String KEY_STEP_PER_SECOND = "stepPerSecond";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LOCATION_TYPE = "locationType";
    public static final String KEY_IS_NORMAL = "isNormal";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";

    /*
    //mysql
    CREATE TABLE `wzsport_running_activity_data` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `activity_id` bigint(20) NOT NULL,
  `acquisition_time` datetime NOT NULL,
  `step_count` smallint(5) unsigned NOT NULL,
  `step_count_cal` smallint(6) NOT NULL DEFAULT '0',
  `distance` smallint(5) unsigned NOT NULL,
  `distance_per_step` decimal(4,2) NOT NULL DEFAULT '0.00',
  `step_per_second` decimal(4,2) NOT NULL DEFAULT '0.00',
  `longitude` decimal(10,6) NOT NULL,
  `latitude` decimal(10,6) NOT NULL,
  `location_type` tinyint(3) unsigned NOT NULL,
  `is_normal` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `i_activity_id` (`activity_id`) USING BTREE
)
     */
    public static final String TABLE_RUNNING_DATA_SQL = "CREATE TABLE " + TABLE_RUNNING_ACTIVITY_DATA + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_ACTIVITY_ID + " BIGINT(11), " +
            KEY_ACQUISITION_TIME + " INTEGER, " +
            KEY_STEP_COUNT + " INTEGER, " +
            KEY_STEP_COUNT_CAL + " INTEGER, " +
            KEY_DISTANCE + " INTEGER, " +
            KEY_DISTANCE_PER_STEP + " DOUBLE, " +
            KEY_STEP_PER_SECOND + " DOUBLE, " +
            KEY_LONGITUDE + " DOUBLE, " +
            KEY_LATITUDE + " DOUBLE, " +
            KEY_LOCATION_TYPE + " INTEGER, " +
            KEY_IS_NORMAL + " INTEGER, " +
            KEY_CREATED_AT + " INTEGER, " +
            KEY_UPDATED_AT + " INTEGER" +
            ")";

    @Override
    public String getDBName() {
        return DB_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_RUNNING_ACTIVITY_DATA;
    }

    @Override
    public int getDBVersion() {
        return DB_VERSION;
    }

    @Override
    public int count(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select count(*) from  " +
                getTableName(), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        return count;
    }

    @Override
    public void doUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 0:
                db.execSQL(TABLE_RUNNING_DATA_SQL); // 升级操作；
            case 1:
                break;
            default:
                break;
        }
    }

    @Override
    public List<String> createTableSql() {
        return Arrays.asList(
                TABLE_RUNNING_DATA_SQL
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
            case TABLE_RUNNING_ACTIVITY_DATA:
                // if(t instanceof RunningActivityRecord){
                //     RunningActivityRecord record = (RunningActivityRecord) t;
                //     values.put(KEY_ID, record.getId());
                //     values.put(KEY_RUNNING_SPORT_ID, record.getRunningSportId());
                //     values.put(KEY_END_RUNNING_SPORT_ID, record.getEndRunningSportId());
                //     values.put(KEY_RUNNING_STUDENT_ID, record.getStudentId());
                //     values.put(KEY_DISTANCE, record.getDistance());
                //     values.put(KEY_STEP_COUNT, record.getStepCount());
                //     values.put(KEY_COST_TIME, record.getCostTime());
                //     values.put(KEY_SPEED, record.getSpeed());
                //     values.put(KEY_STEP_PER_SECOND, record.getStepPerSecond());
                //     values.put(KEY_DISTANCE_PER_STEP, record.getDistancePerStep());
                //     values.put(KEY_TARGET_FINISHED_TIME, record.getTargetFinishedTime());
                //     values.put(KEY_START_TIME, record.getStartTime());
                //     values.put(KEY_KCAL_CONSUMED, record.getKcalConsumed());
                //     values.put(KEY_QUALIFIED, record.getQualified());
                //     values.put(KEY_IS_VALID, record.getIsValid());
                //     values.put(KEY_IS_VERIFIED, record.getIsVerified());
                //     values.put(KEY_QUALIFIED_DISTANCE, record.getQualifiedDistance());
                //     values.put(KEY_QUALIFIED_COST_TIME, record.getQualifiedCostTime());
                //     values.put(KEY_MIN_COST_TIME, record.getMinCostTime());
                //     values.put(KEY_CREATED_AT, record.getCreatedAt());
                //     values.put(KEY_UPDATED_AT, record.getUpdatedAt());
                //     values.put(KEY_ENDED_AT, record.getEndedAt());
                //     values.put(KEY_ENDED_BY, record.getEndedBy());
                // }
                break;
        }
    }

    @Override
    public long saveRecord(SQLiteDatabase db, ContentValues values) {
        long result = db.insert(RunningActivityDataCallback.TABLE_RUNNING_ACTIVITY_DATA, null, values);
        return result;
    }

    @Override
    public long updateRecord(SQLiteDatabase db, ContentValues values) {
        long result = db.update(RunningActivityDataCallback.TABLE_RUNNING_ACTIVITY_DATA,
                values, "id = ?",  new String[] {values.getAsString("id")} );
        return result;
    }

    @Override
    public RunningActivityDataRecord getEntityByCursor(String tableName, Cursor c) {
        switch (tableName) {
            case TABLE_RUNNING_ACTIVITY_DATA:
                // return new RunningActivityDataRecord(
                //         c.getLong(0),
                //         c.getLong(1),
                //         c.getInt(2),
                //         c.getInt(3),
                //         c.getInt(4),
                //         c.getInt(5),
                //         c.getLong(6),
                //         c.getLong(7),
                //         c.getLong(8),
                //         c.getLong(9),
                //         c.getInt(10),
                //         c.getInt(11),
                //         c.getInt(12),
                //         c.getInt(13)
                // );
        }
        return null;
    }
}
