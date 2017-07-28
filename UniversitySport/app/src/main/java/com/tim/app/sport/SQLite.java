package com.tim.app.sport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nimon on 2017/6/18.
 */

public class SQLite extends SQLiteOpenHelper {


    public interface TableInterface {
        //DB NAME
        String getDBName();

        String getTableName();

        int getDBVersion();

        void doUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

        List<String> createTableSql();

        <T> void assignValuesByEntity(String tableName, T t, ContentValues values);

        <T> T getEntityByCursor(String tableName, Cursor c);

    }

    public static void init(Context context, SQLite.TableInterface callBack) {
        if (instance == null) {
            instance = new SQLite(context, callBack);
        }
    }

    public static final String ILLEGAL_OPREATION = "非法操作，请先执行初始化操作。 SQLite.init()";
    private final static int DB_VERSION = 2;
    private static SQLite instance;

    private static final AtomicInteger openCounter = new AtomicInteger();

    private SQLite(final Context context) {
        super(context, mCallBack.getDBName(), null, DB_VERSION);
    }

    public static synchronized SQLite getInstance(final Context c) {
        if (instance == null) {
            instance = new SQLite(c.getApplicationContext());
        }
        openCounter.incrementAndGet();
        return instance;
    }

    @Override
    public void close() {
        if (openCounter.decrementAndGet() == 0) {
            super.close();
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        //todo 改写。
        for (String create_table : mCallBack.createTableSql()) {
            db.execSQL(create_table);
            Log.d(TAG, "create table " + "[ \n" + create_table + "\n ]" + " successful! ");
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // drop PRIMARY KEY constraint
            db.execSQL("CREATE TABLE " + mCallBack.getDBName() + "2 (date INTEGER, steps INTEGER)");
            db.execSQL("INSERT INTO " + mCallBack.getDBName() + "2 (date, steps) SELECT date, steps FROM " +
                    mCallBack.getDBName());
            db.execSQL("DROP TABLE " + mCallBack.getDBName());
            db.execSQL("ALTER TABLE " + mCallBack.getDBName() + "2 RENAME TO " + mCallBack.getDBName() + "");
        }
    }


    /**
     * @return the insert raw ID
     * @SmartNi 2017-06-17
     * save one Running Sports Record to Database.
     */
    public int saveRunningSportsRecord(int runningSportId, int activityId, int studentId, int currentDistance,
                                       long elapaseTime, long startTime, int steps, long date) {
        ContentValues values = new ContentValues();
        values.put("runningSportId", runningSportId);
        values.put("studentId", studentId);
        values.put("currentDistance", currentDistance);
        values.put("elapseTime", elapaseTime);
        values.put("startTime", startTime);
        values.put("steps", steps);
        values.put("date", date);
        values.put("activityId", activityId);
        long result = getWritableDatabase().insert(RunningSportsCallback.TABLE_RUNNING_SPORTS, null, values);
        return (int) result;
    }


    public int deleteSportsRecord(String tableName, String whereClause, String[] whereArgs) {

        int result = getWritableDatabase().delete(tableName, whereClause, whereArgs);
        return result;

    }

    public int count() {
        Cursor cursor = getWritableDatabase().rawQuery("select count(*) from  " +
                mCallBack.getDBName(), null);
        return cursor.getCount();
    }


    public static <T> List<T> query(String tableName,
                                    @Nullable String queryStr, @Nullable String[] whereArgs) {
        if (instance == null) {
            throw new IllegalStateException(ILLEGAL_OPREATION);
        }

        List<T> list = new ArrayList<>();
        SQLiteDatabase db = instance.getReadableDatabase();
        db.beginTransaction();
        try {
            db.setTransactionSuccessful();
            Cursor c = db.rawQuery(queryStr, whereArgs);
            if (c.moveToFirst()) {
                do {
                    T record = (T) instance.mCallBack
                            .getEntityByCursor(mCallBack.getTableName(), c);
                    if (record != null) {
                        list.add(record);
                    }
                } while (c.moveToNext());
                c.close();
            }
        } finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Self
    ///////////////////////////////////////////////////////////////////////////

    private static final String TAG = "SQLite";

    private static SQLite.TableInterface mCallBack = null;

    private SQLite(@NonNull Context context, @NonNull SQLite.TableInterface callBack) {
        super(context, callBack.getDBName(), null, callBack.getDBVersion());
        mCallBack = callBack;
    }

    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

}
