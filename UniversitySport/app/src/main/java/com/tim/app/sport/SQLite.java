package com.tim.app.sport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.application.library.log.DLOG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nimon on 2017/6/18.
 */

public class SQLite extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    private static List<SQLite.TableInterface> mCallBacks = new ArrayList<>();
    private static Map<Class, SQLite.TableInterface> mCallBackMap = new HashMap<>();

    public static final String ILLEGAL_OPREATION = "非法操作，请先执行初始化操作。 SQLite.init()";
    private final static int DB_VERSION = 2;
    private static SQLite instance;

    private static final AtomicInteger openCounter = new AtomicInteger();

    public interface TableInterface {
        //DB NAME
        String getDBName();

        String getTableName();

        int getDBVersion();

        int count(SQLiteDatabase db);

        void doUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

        List<String> createTableSql();

        <T> void assignValuesByEntity(String tableName, T t, ContentValues values);

        <T> T getEntityByCursor(String tableName, Cursor c);

        long saveRecord(SQLiteDatabase db, ContentValues values);

        long updateRecord(SQLiteDatabase db, ContentValues values);

    }

    // public static void init(Context context, SQLite.TableInterface callBack) {
    //     if (instance == null) {
    //         instance = new SQLite(context, callBack);
    //     }
    //     mCallBacks.add(callBack);
    // }

    public static void init(Context context, Class theClass) {
        if (SQLite.TableInterface.class.isAssignableFrom(theClass)) {
            // theClass 实现了 SQLite.TableInterface
            Log.d(TAG, "theClass 实现了 SQLite.TableInterface");
            try {
                SQLite.TableInterface callBack = (TableInterface) theClass.newInstance();
                if (instance == null) {
                    instance = new SQLite(context, callBack);
                }
                mCallBacks.add(callBack);
                mCallBackMap.put(theClass, callBack);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            // theClass 没有实现 SQLite.TableInterface
            Log.d(TAG, "theClass 没有实现 SQLite.TableInterface");
        }
    }

    private SQLite(final Context context) {
        super(context, mCallBacks.get(0).getDBName(), null, DB_VERSION);
    }

    private SQLite(@NonNull Context context, @NonNull SQLite.TableInterface callBack) {
        super(context, callBack.getDBName(), null, callBack.getDBVersion());
    }

    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
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
        DLOG.d("mCallBacks.size()" + mCallBacks.size());
        for (TableInterface callback : mCallBacks) {
            for (String create_table : callback.createTableSql()) {
                db.execSQL(create_table);
                Log.d(TAG, "create table " + "[ \n" + create_table + "\n ]" + " successful! ");
            }
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // drop PRIMARY KEY constraint
            // db.execSQL("CREATE TABLE " + mCallBack.getDBName() + "2 (date INTEGER, steps INTEGER)");
            // db.execSQL("INSERT INTO " + mCallBack.getDBName() + "2 (date, steps) SELECT date, steps FROM " +
            //         mCallBack.getDBName());
            // db.execSQL("DROP TABLE " + mCallBack.getDBName());
            // db.execSQL("ALTER TABLE " + mCallBack.getDBName() + "2 RENAME TO " + mCallBack.getDBName() + "");
        }
    }

    public int count(TableInterface callBack) {
        // 判断是否实现了TableInterface
        // TableInterface callBack = mCallBackMap.get(clazz);
        int count = callBack.count(getWritableDatabase());
        return count;
    }

    /**
     * @return the insert raw ID
     * @SmartNi 2017-06-17
     * save one Running Sports Record to Database.
     */
    public long saveRecord(TableInterface callBack, ContentValues values) {
        long result = callBack.saveRecord(getWritableDatabase(), values);
        return (int) result;
    }

    public int updateRecord(TableInterface callBack, ContentValues values) {
        long result = callBack.updateRecord(getWritableDatabase(), values);
        return (int) result;
    }

    public int deleteSportsRecord(String tableName, String whereClause, String[] whereArgs) {

        int result = getWritableDatabase().delete(tableName, whereClause, whereArgs);
        return result;

    }

    public static <T> List<T> query(TableInterface callback,
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
                    T record = (T) callback.getEntityByCursor(callback.getTableName(), c);
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


}
