/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tim.app.sport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Database extends SQLiteOpenHelper {



    public static final String ILLEGAL_OPREATION = "非法操作，请先执行初始化操作。 Database.init()";
    private final static String TABLE_RUNNING_SPORTS = "wzu_sports_running_sports_record";
    private final static String TABLE_DAY_STEPS = "day_steps";
    private final static int DB_VERSION = 2;
    private static final AtomicInteger openCounter = new AtomicInteger();
    private static final String TAG = "Database";
    private static Database instance;
    // private static TableInterface mCallBack = null;

    private Database(final Context context) {
        super(context, TABLE_RUNNING_SPORTS, null, DB_VERSION);
    }

    // private Database(@NonNull Context context, @NonNull TableInterface callBack) {
    //     super(context, callBack.getName(), null, callBack.getDBVersion());
    //     mCallBack = callBack;
    // }

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // public static void init(Context context, TableInterface callBack) {
    //     if (instance == null) {
    //         instance = new Database(context, callBack);
    //     }
    // }

    public static synchronized Database getInstance(final Context c) {
        if (instance == null) {
            instance = new Database(c.getApplicationContext());
        }
        openCounter.incrementAndGet();
        return instance;
    }

    // public static <T> List<T> query(String tableName,
    //                                 @Nullable String queryStr, @Nullable String[] whereArgs) {
    //     if (instance == null) {
    //         throw new IllegalStateException(ILLEGAL_OPREATION);
    //     }
    //
    //     List<T> list = new ArrayList<>();
    //     SQLiteDatabase db = instance.getReadableDatabase();
    //     db.beginTransaction();
    //     try {
    //         db.setTransactionSuccessful();
    //         Cursor c = db.rawQuery(queryStr, whereArgs);
    //         if (c.moveToFirst()) {
    //             do {
    //                 T record = (T) instance.mCallBack
    //                         .getEntityByCursor(TABLE_RUNNING_SPORTS, c);
    //                 if (record != null) {
    //                     list.add(record);
    //                 }
    //             } while (c.moveToNext());
    //             c.close();
    //         }
    //     } finally {
    //         db.endTransaction();
    //         db.close();
    //     }
    //     return list;
    // }

    // public static String getTableName(){
    //     return mCallBack.getName();
    // }

    @Override
    public void close() {
        if (openCounter.decrementAndGet() == 0) {
            super.close();
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_RUNNING_SPORTS + " (date INTEGER," + " steps INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_DAY_STEPS + " (time INTEGER," + " steps INTEGER)");
    }

    // TODO
    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // drop PRIMARY KEY constraint
            db.execSQL("CREATE TABLE " + TABLE_RUNNING_SPORTS + "2 (date INTEGER, steps INTEGER)");
            db.execSQL("INSERT INTO " + TABLE_RUNNING_SPORTS + "2 (date, steps) SELECT date, steps FROM " +
                    TABLE_RUNNING_SPORTS);
            db.execSQL("DROP TABLE " + TABLE_RUNNING_SPORTS);
            db.execSQL("ALTER TABLE " + TABLE_RUNNING_SPORTS + "2 RENAME TO " + TABLE_RUNNING_SPORTS + "");
        }
    }

    /**
     * Query the 'steps' table. Remember to close the cursor!
     *
     * @param columns       the colums
     * @param selection     the selection
     * @param selectionArgs the selction arguments
     * @param groupBy       the group by statement
     * @param having        the having statement
     * @param orderBy       the order by statement
     * @return the cursor
     */
    public Cursor query(final String[] columns, final String selection,
                        final String[] selectionArgs, final String groupBy, final String having,
                        final String orderBy, final String limit) {
        return getReadableDatabase()
                .query(TABLE_RUNNING_SPORTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * Inserts a new entry in the database, if there is no entry for the given
     * date yet. Steps should be the current number of steps and it's negative
     * value will be used as offset for the new date. Also adds 'steps' steps to
     * the previous day, if there is an entry for that date.
     * <p/>
     * This method does nothing if there is already an entry for 'date' - use
     * {@link #\} in this case.
     * <p/>
     * To restore data from a backup, use {@link #insertDayFromBackup}
     *
     * @param date  the date in ms since 1970
     * @param steps the current step value to be used as negative offset for the
     *              new day; must be >= 0
     */
    public void insertNewDay(long date, int steps) {
        getWritableDatabase().beginTransaction();
        try {
            Cursor c = getReadableDatabase().query(TABLE_RUNNING_SPORTS, new String[]{"date"}, "date = ?",
                    new String[]{String.valueOf(date)}, null, null, null);
            if (c.getCount() == 0 && steps >= 0) {

                // add 'steps' to yesterdays count
                addToLastEntry(steps);

                // add today
                ContentValues values = new ContentValues();
                values.put("date", date);
                // use the negative steps as offset
                values.put("steps", -steps);
                getWritableDatabase().insert(TABLE_RUNNING_SPORTS, null, values);
            }
            c.close();
            logState();
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
    }

    /**
     * Adds the given number of steps to the last entry in the database
     *
     * @param steps the number of steps to add. Must be > 0
     */
    public void addToLastEntry(int steps) {
        if (steps > 0) {
            getWritableDatabase().execSQL("UPDATE " + TABLE_RUNNING_SPORTS + " SET steps = steps + " + steps +
                    " WHERE date = (SELECT MAX(date) FROM " + TABLE_RUNNING_SPORTS + ")");
        }
    }

    /**
     * Inserts a new entry in the database, if there is no entry for the given
     * date yet. Use this method for restoring data from a backup.
     * <p/>
     * This method does nothing if there is already an entry for 'date'.
     *
     * @param date  the date in ms since 1970
     * @param steps the step value for 'date'; must be >= 0
     * @return true if a new entry was created, false if there was already an
     * entry for 'date'
     */
    public boolean insertDayFromBackup(long date, int steps) {
        getWritableDatabase().beginTransaction();
        boolean re;
        try {
            Cursor c = getReadableDatabase().query(TABLE_RUNNING_SPORTS, new String[]{"date"}, "date = ?",
                    new String[]{String.valueOf(date)}, null, null, null);
            re = c.getCount() == 0 && steps >= 0;
            if (re) {
                ContentValues values = new ContentValues();
                values.put("date", date);
                values.put("steps", steps);
                getWritableDatabase().insert(TABLE_RUNNING_SPORTS, null, values);
            }
            c.close();
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
        return re;
    }

    /**
     * TODO Writes the current steps database to the log
     */
    public void logState() {
        Cursor c = getReadableDatabase()
                .query(TABLE_RUNNING_SPORTS, null, null, null, null, null, "date DESC", "5");
        c.close();
    }

    /**
     * Get the total of steps taken without today's value
     *
     * @return number of steps taken, ignoring today
     */
    public int getTotalWithoutToday() {
        Cursor c = getReadableDatabase()
                .query(TABLE_RUNNING_SPORTS, new String[]{"SUM(steps)"}, "steps > 0 AND date > 0 AND date < ?",
                        new String[]{String.valueOf(Util.getToday())}, null, null, null);
        c.moveToFirst();
        int re = c.getInt(0);
        c.close();
        return re;
    }

    /**
     * Get the maximum of steps walked in one day
     *
     * @return the maximum number of steps walked in one day
     */
    public int getRecord() {
        Cursor c = getReadableDatabase()
                .query(TABLE_RUNNING_SPORTS, new String[]{"MAX(steps)"}, "date > 0", null, null, null, null);
        c.moveToFirst();
        int re = c.getInt(0);
        c.close();
        return re;
    }

    /**
     * Get the maximum of steps walked in one day and the date that happend
     *
     * @return a pair containing the date (Date) in millis since 1970 and the
     * step value (Integer)
     */
    public Pair<Date, Integer> getRecordData() {
        Cursor c = getReadableDatabase()
                .query(TABLE_RUNNING_SPORTS, new String[]{"date, steps"}, "date > 0", null, null, null,
                        "steps DESC", "1");
        c.moveToFirst();
        Pair<Date, Integer> p = new Pair<Date, Integer>(new Date(c.getLong(0)), c.getInt(1));
        c.close();
        return p;
    }

    /**
     * Get the number of steps taken for a specific date.
     * <p/>
     * If date is Util.getToday(), this method returns the offset which needs to
     * be added to the value returned by getCurrentSteps() to get todays steps.
     *
     * @param date the date in millis since 1970
     * @return the steps taken on this date or Integer.MIN_VALUE if date doesn't
     * exist in the database
     */
    public int getSteps(final long date) {
        Cursor c = getReadableDatabase().query(TABLE_RUNNING_SPORTS, new String[]{"steps"}, "date = ?",
                new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        int re;
        if (c.getCount() == 0)
            re = Integer.MIN_VALUE;
        else
            re = c.getInt(0);
        c.close();
        return re;
    }

    /**
     * Gets the last num entries in descending order of date (newest first)
     *
     * @param num the number of entries to get
     * @return a list of long,integer pair - the first being the date, the second the number of steps
     */
    public List<Pair<Long, Integer>> getLastEntries(int num) {
        Cursor c = getReadableDatabase()
                .query(TABLE_RUNNING_SPORTS, new String[]{"date", "steps"}, "date > 0", null, null, null,
                        "date DESC", String.valueOf(num));
        int max = c.getCount();
        List<Pair<Long, Integer>> result = new ArrayList<>(max);
        if (c.moveToFirst()) {
            do {
                result.add(new Pair<>(c.getLong(0), c.getInt(1)));
            } while (c.moveToNext());
        }
        return result;
    }

    /**
     * Get the number of steps taken between 'start' and 'end' date
     * <p/>
     * Note that todays entry might have a negative value, so take care of that
     * if 'end' >= Util.getToday()!
     *
     * @param start start date in ms since 1970 (steps for this date included)
     * @param end   end date in ms since 1970 (steps for this date included)
     * @return the number of steps from 'start' to 'end'. Can be < 0 as todays
     * entry might have negative value
     */
    public int getSteps(final long start, final long end) {
        Cursor c = getReadableDatabase()
                .query(TABLE_RUNNING_SPORTS, new String[]{"SUM(steps)"}, "date >= ? AND date <= ?",
                        new String[]{String.valueOf(start), String.valueOf(end)}, null, null, null);
        int re;
        if (c.getCount() == 0) {
            re = 0;
        } else {
            c.moveToFirst();
            re = c.getInt(0);
        }
        c.close();
        return re;
    }

    /**
     * Removes all entries with negative values.
     * <p/>
     * Only call this directly after boot, otherwise it might remove the current
     * day as the current offset is likely to be negative
     */
    void removeNegativeEntries() {
        getWritableDatabase().delete(TABLE_RUNNING_SPORTS, "steps < ?", new String[]{"0"});
    }

    /**
     * Removes invalid entries from the database.
     * <p/>
     * Currently, an invalid input is such with steps >= 200,000
     */
    public void removeInvalidEntries() {
        getWritableDatabase().delete(TABLE_RUNNING_SPORTS, "steps >= ?", new String[]{"200000"});
    }

    /**
     * Get the number of 'valid' days (= days with a step value > 0).
     * <p/>
     * The current day is also added to this number, even if the value in the
     * database might still be < 0.
     * <p/>
     * It is safe to divide by the return value as this will be at least 1 (and
     * not 0).
     *
     * @return the number of days with a step value > 0, return will be >= 1
     */
    public int getDays() {
        Cursor c = getReadableDatabase()
                .query(TABLE_RUNNING_SPORTS, new String[]{"COUNT(*)"}, "steps > ? AND date < ? AND date > 0",
                        new String[]{String.valueOf(0), String.valueOf(Util.getToday())}, null,
                        null, null);
        c.moveToFirst();
        // todays is not counted yet
        int re = c.getInt(0) + 1;
        c.close();
        return re <= 0 ? 1 : re;
    }

    /**
     * Saves the current 'steps since boot' sensor value in the database.
     *
     * @param steps since boot
     */
    public void saveCurrentSteps(int steps) {
        ContentValues values = new ContentValues();
        values.put("steps", steps);
        // if 语句中执行了更新操作，如果返回 0 ，说明更新失败，重新插入新的记录。
        // date 是 -1 说明是自系统启动至现在的步数。
        if (getWritableDatabase().update(TABLE_RUNNING_SPORTS, values, "date = -1", null) == 0) {
            values.put("date", -1);
            getWritableDatabase().insert(TABLE_RUNNING_SPORTS, null, values);
        }
    }

    public void saveDaySteps(int steps) {
        ContentValues values = new ContentValues();
        values.put("steps", steps);

        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分");
        // String time = sdf.format(System.currentTimeMillis());
        // DLOG.d(TAG, time);
        values.put("time", System.currentTimeMillis());
        getWritableDatabase().insert(TABLE_DAY_STEPS, null, values);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Self
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Reads the latest saved value for the 'steps since boot' sensor value.
     *
     * @return the current number of steps saved in the database or 0 if there
     * is no entry
     */
    public int getCurrentSteps() {
        int re = getSteps(-1);
        return re == Integer.MIN_VALUE ? 0 : re;
    }

}
