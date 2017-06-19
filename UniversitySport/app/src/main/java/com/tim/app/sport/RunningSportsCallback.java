package com.tim.app.sport;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tim.app.server.entry.RunningSportsRecord;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nimon on 2017/6/18.
 */

public class RunningSportsCallback implements SQLite.TableInterface {

    public RunningSportsCallback() {
    }

    private static RunningSportsCallback instance;

    public static RunningSportsCallback getInstance() {
        if (instance == null) {
            instance = new RunningSportsCallback();
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
    public static final String TABLE_RUNNING_SPORTS = "running_sports_record";
    public static final String KEY_RUNNING_ID = "id";
    public static final String KEY_RUNNING_PROJECTID = "projectId";
    public static final String KEY_RUNNING_STUDENTID = "studentId";
    public static final String KEY_RUNNING_CURRENTDISTANCE = "currentDistance";
    public static final String KEY_RUNNING_ELAPSETIME = "elapseTime";
    public static final String KEY_RUNNING_STARTTIME = "startTime";
    public static final String KEY_RUNNING_STEPS = "steps";
    public static final String KEY_RUNNING_DATE = "date";
    public static final String TABLE_RUNNING_SQL = "CREATE TABLE " + TABLE_RUNNING_SPORTS + " (" +
            KEY_RUNNING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_RUNNING_PROJECTID + " INTEGER, " +
            KEY_RUNNING_STUDENTID + " INTEGER, " +
            KEY_RUNNING_CURRENTDISTANCE + " INTEGER, " +
            KEY_RUNNING_ELAPSETIME + " INTEGER, " +
            KEY_RUNNING_STARTTIME + " INTEGER, " +
            KEY_RUNNING_STEPS + " INTEGER, " +
            KEY_RUNNING_DATE + " INTEGER " +
            ")";


    @Override
    public String getDBName() {
        return DB_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_RUNNING_SPORTS;
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
            case TABLE_RUNNING_SPORTS:
                if(t instanceof  RunningSportsRecord){
                    RunningSportsRecord record = (RunningSportsRecord) t;
                    values.put(KEY_RUNNING_ID,record.getId());
                    values.put(KEY_RUNNING_PROJECTID,record.getProjectId());
                    values.put(KEY_RUNNING_STUDENTID,record.getStudentId());
                    values.put(KEY_RUNNING_CURRENTDISTANCE,record.getCurrentDistance());
                    values.put(KEY_RUNNING_ELAPSETIME,record.getElapseTime());
                    values.put(KEY_RUNNING_STARTTIME,record.getStartTime());
                    values.put(KEY_RUNNING_STEPS,record.getSteps());
                    values.put(KEY_RUNNING_DATE,record.getDate());
                }
                break;

        }
    }

    @Override
    public Object getEntityByCursor(String tableName, Cursor c) {
        switch (tableName) {
            case TABLE_RUNNING_SPORTS:
                return new RunningSportsRecord(
                        c.getInt(0),
                        c.getInt(1),
                        c.getInt(2),
                        c.getInt(3),
                        c.getInt(4),
                        c.getLong(5),
                        c.getInt(6),
                        c.getLong(7));
        }
        return null;
    }
}
